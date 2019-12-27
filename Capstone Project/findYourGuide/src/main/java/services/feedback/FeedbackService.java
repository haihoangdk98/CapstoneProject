package services.feedback;

import entities.Feedback;

import java.util.List;

public interface FeedbackService {
    List<Feedback> findAllFeedback() throws Exception;

    void createFeedback(int account_id, String message);
}
