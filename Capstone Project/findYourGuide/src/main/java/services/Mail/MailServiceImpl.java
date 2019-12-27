package services.Mail;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;
import services.Post.PostService;
import services.guider.GuiderService;
import services.traveler.TravelerService;
import services.trip.TripService;

import java.time.format.DateTimeFormatter;

@Repository
public class MailServiceImpl implements MailService {
    private static final String UNCONFIRMED = "WAITING";
    private static final String ONGOING = "ONGOING";
    private static final String FINISHED = "FINISHED";
    private static final String CANCELLED = "CANCELLED";

    private JavaMailSender emailSender;
    private TravelerService travelerService;
    private GuiderService guiderService;
    private PostService postService;
    private TripService tripService;

    @Value("${sysadmin.email}")
    private String sysadminEmail;

    @Autowired
    public MailServiceImpl(JavaMailSender jm, TravelerService ts, GuiderService gs, PostService ps, TripService ots) {
        this.emailSender = jm;
        this.travelerService = ts;
        this.guiderService = gs;
        this.postService = ps;
        this.tripService = ots;
    }

    @Override
    public boolean sendMail(String email, String subject, String content) throws Exception {
        // Create a mail
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(content);
        // Send mail
        this.emailSender.send(mail);
        return true;
    }

    @Override
    public String getMailContent(Order order, String orderStatus) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        String content = "";
        Traveler traveler = travelerService.findTravelerWithId(order.getTraveler_id());
        Post post = postService.findSpecificPost(order.getPost_id());
        Guider guider = guiderService.findGuiderWithPostId(order.getPost_id());

        // create mail content
        content = content.concat("Dear Mr/Ms " + traveler.getLast_name() + "\n\n");
        switch (orderStatus) {
            case UNCONFIRMED:
                content = content.concat("Your tour has been booked successfully.\n");
                break;
            case ONGOING:
                content = content.concat("Your tour has been accepted by " + guider.getFirst_name() + " " + guider.getLast_name() + ".\n");
                break;
            case CANCELLED:
                content = content.concat("Your tour has been cancelled by " + guider.getFirst_name() + " " + guider.getLast_name() + ".\n");
                break;
            case FINISHED:
                content = content.concat("Your tour has finished.\n");
                break;
        }
        content = content.concat("Below is the information of your tour:\n");
        content = content.concat("Tour: " + post.getTitle() + "\n");
        content = content.concat("Your guider: " + guider.getFirst_name() + " " + guider.getLast_name() + "\n");
        content = content.concat("Begin on: " + order.getBegin_date().format(formatter)
                + " - Expected end on: " + order.getFinish_date().format(formatter) + "\n");
        content = content.concat("The tour has " + order.getAdult_quantity() + " adults and " + order.getChildren_quantity() + " children.\n");
        content = content.concat("Total: " + order.getFee_paid() + "$\n\n");
        String tourStatus = "";
        if (order.getStatus() == null) {
            tourStatus = "Waiting for confirmation";
        } else {
            switch (order.getStatus()) {
                case ONGOING:
                    tourStatus = "Ongoing";
                    break;
                case CANCELLED:
                    tourStatus = "Cancelled";
                    break;
                case FINISHED:
                    tourStatus = "Finished";
                    break;
            }
        }
        content = content.concat("Status: " + tourStatus + "\n\n");
        content = content.concat("Thank your for using our service. We wish you a great trip and happy experience.\n\n");
        content = content.concat("Sincerely,\n");
        content = content.concat("TravelWLocal");
        return content;
    }

    @Override
    public String acceptContractMailContent(long guider_id) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String content = "";
        Contract contract = guiderService.findGuiderContract(guider_id);
        content = content.concat("Dear Mr/Ms " + contract.getName() + "\n\n");
        content = content.concat("Your contract to become a guider has been accepted !\n\n");
        content = content.concat("Here is the information you have given us upon setting up the contract\n");
        content = content.concat("Name: " + contract.getName() + "\n");
        content = content.concat("Nationality: " + contract.getNationality() + "\n");
        content = content.concat("Date of birth (MM/dd/yyyy): " + contract.getDate_of_birth().format(formatter) + "\n");
        if (contract.getGender() == 1) {
            content = content.concat("Gender: Male\n");
        } else if (contract.getGender() == 2) {
            content = content.concat("Gender: Female\n");
        } else {
            content = content.concat("Gender: Other\n");
        }
        content = content.concat("Hometown: " + contract.getHometown() + "\n");
        content = content.concat("Address: " + contract.getAddress() + "\n");
        content = content.concat("Identity Card Number: " + contract.getIdentity_card_number() + "\n");
        content = content.concat("Identity Card Issued Date: " + contract.getCard_issued_date().format(formatter) + "\n");
        content = content.concat("Identity Card Issued Province: " + contract.getCard_issued_province() + "\n");
        content = content.concat("If any information were mistakenly given, please contact us immediately.\n");
        content = content.concat("We also recommend to update your profile if you have not done it, so the customers can have " +
                "a better understanding about you !\n\n");
        content = content.concat("Thank your for using our service.\n\n");
        content = content.concat("Sincerely,\n");
        content = content.concat("TravelWLocal");
        return content;
    }

    @Override
    public String rejectContractMailContent(long guider_id) throws Exception {
        String content = "";
        Contract contract = guiderService.findGuiderContract(guider_id);
        content = content.concat("Dear Mr/Ms " + contract.getName() + "\n\n");
        content = content.concat("Thank you for your application for the position of tour guider at our TravelWLocal website. " +
                "As you can imagine, we received a large number of applications. " +
                "I am sorry to inform you that you have not been selected for this position.\n\n");
        content = content.concat("We at TravelWLocal thanks you for the time you invested in applying for the position. " +
                "We encourage you to apply for future openings for which you qualify.\n\n");
        content = content.concat("Best wishes for successful in life. Thank you, again, for your interest in our website.\n\n");
        content = content.concat("Thank your for using our service.\n\n");
        content = content.concat("Sincerely,\n");
        content = content.concat("TravelWLocal");
        return content;
    }
}
