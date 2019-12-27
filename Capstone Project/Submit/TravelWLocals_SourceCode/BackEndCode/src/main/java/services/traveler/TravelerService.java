package services.traveler;

import entities.Traveler;

public interface TravelerService {
    boolean createTraveler(Traveler newTraveler) throws Exception;

    Traveler findTravelerWithId(long id) throws Exception;

    void updateTraveler(Traveler travelerNeedUpdate) throws Exception;

    void favoritePost(int traveler_id, int post_id) throws Exception;

    boolean isLackingProfile(long traveler_id) throws Exception;

    void updateLackingProfile(Traveler traveler) throws Exception;
    
    void unlikePost(int traveler_id, int post_id) throws Exception;
}
