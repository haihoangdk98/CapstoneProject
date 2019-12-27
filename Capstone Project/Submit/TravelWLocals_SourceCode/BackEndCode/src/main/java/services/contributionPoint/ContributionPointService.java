package services.contributionPoint;

public interface ContributionPointService {

    void penaltyGuiderForCancel(int guider_id) throws Exception;

    void calculatePostAndGuiderRating(long post_id) throws Exception;
}
