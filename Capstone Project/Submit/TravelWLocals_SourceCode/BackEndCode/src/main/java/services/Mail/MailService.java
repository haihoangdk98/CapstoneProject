package services.Mail;

import entities.Order;

public interface MailService {
    boolean sendMail(String email, String subject, String content) throws Exception;

    String getMailContent(Order order, String orderStatus) throws Exception;

    String acceptContractMailContent(long guider_id) throws Exception;

    String rejectContractMailContent(long guider_id) throws Exception;
}
