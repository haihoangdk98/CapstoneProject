package services.Paypal;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import entities.Order;

public interface PaypalService {
    public Payment createPayment(Double total, String currency, String description, String cancelUrl, String successUrl) throws PayPalRESTException;

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

    public void createTransactionRecord(String transaction_id, String payment_id, String payer_id, String description, boolean success) throws Exception;

    public double getTransactionFee(Order order) throws Exception;

    public String getTransactionDescription(Order order) throws Exception;

    public Refund refundPayment(String transaction_id) throws PayPalRESTException;

    public void createRefundRecord(String transaction_id, String message) throws Exception;
}
