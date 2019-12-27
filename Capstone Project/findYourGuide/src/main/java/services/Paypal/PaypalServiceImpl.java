package services.Paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaypalServiceImpl implements PaypalService {
    private JdbcTemplate jdbcTemplate;
    private APIContext apiContext;

    @Autowired
    public PaypalServiceImpl(JdbcTemplate jt, APIContext context) {
        this.jdbcTemplate = jt;
        this.apiContext = context;
    }

    @Override
    public Payment createPayment(Double total, String currency, String description, String cancelUrl, String successUrl) throws PayPalRESTException {
        // Create amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));
        //Create transaction
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        // Create payer
        Payer payer = new Payer();
        payer.setPaymentMethod(PaypalPaymentMethod.paypal.toString());
        // Create payment
        Payment payment = new Payment();
        payment.setIntent(PaypalPaymentIntent.sale.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        apiContext.setMaskRequestId(true);
        return payment.create(apiContext);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    @Override
    public void createTransactionRecord(String transaction_id, String payment_id,
                                        String payer_id, String description, boolean success) throws Exception {
        String insertQuery = "insert into transaction (transaction_id, payment_id, payer_id, description, date_of_transaction, success) " +
                "values (?,?,?,?,?,?)";
        jdbcTemplate.update(insertQuery, transaction_id, payment_id, payer_id, description,
                new java.sql.Timestamp(System.currentTimeMillis()), success);
    }

    @Override
    public double getTransactionFee(Order order) throws Exception {
        double fee = 0;
        String query = "select price from post where post_id = ?";
        double price = jdbcTemplate.queryForObject(query, new Object[]{order.getPost_id()}, double.class);
        fee = price * order.getAdult_quantity() + (price / 2) * order.getChildren_quantity();
        return fee;
    }

    @Override
    public String getTransactionDescription(Order order) throws Exception {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        List<String> description = new ArrayList<>();
        String ds = "";
        ds = "On " + format.format(order.getBegin_date()) + "\n";
        ds += ". Include adult: " + order.getAdult_quantity() + ", children: " + order.getChildren_quantity()
                + ". Fee: " + order.getFee_paid() + ".";
        String query = "SELECT title, traveler.first_name as traFname, traveler.last_name as traLname, " +
                "guider.first_name as guFname, guider.last_name as guLname " +
                "FROM post " +
                "join guider on post.guider_id = guider.guider_id " +
                "join traveler on traveler_id = ? " +
                "where post_id = ?";
        description = jdbcTemplate.query(query, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String info = "";
                if (rs.getString("title") != null) {
                    info += " On tour " + rs.getString("title");
                }
                if (rs.getString("traFname") != null && (rs.getString("traLname") != null)) {
                    info += " of " + rs.getString("traFname") + " " + rs.getString("traLname");
                }
                if (rs.getString("guFname") != null && rs.getString("guLname") != null) {
                    info += " and " + rs.getString("guFname") + " " + rs.getString("guLname");
                }
                return info;
            }
        }, order.getTraveler_id(), order.getPost_id());
        if (description != null && !description.isEmpty()) {
            ds += description.get(0);
        }
        return ds;
    }

    @Override
    public Refund refundPayment(String transaction_id) throws PayPalRESTException {
        String query = "SELECT fee_paid FROM trip where transaction_id = ?";
        double fee = jdbcTemplate.queryForObject(query, new Object[]{transaction_id}, double.class);
        // Create new refund
        Refund refund = new Refund();
        // Create amount
        Amount amount = new Amount();
        amount.setTotal(String.format("%.2f", fee));
        amount.setCurrency("USD");
        refund.setAmount(amount);
        Sale sale = new Sale();
        sale.setId(transaction_id);
        return sale.refund(apiContext, refund);
    }

    @Override
    public void createRefundRecord(String transaction_id, String message) throws Exception {
        String query = "insert into refund (transaction_id, date_of_refund, message) values (?,?,?)";
        jdbcTemplate.update(query, transaction_id, new java.sql.Timestamp(System.currentTimeMillis()), message);
    }
}
