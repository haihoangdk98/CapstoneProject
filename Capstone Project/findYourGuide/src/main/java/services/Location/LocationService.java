package services.Location;

import entities.Location;

import java.util.List;

public interface LocationService {

    List<Location> showAllLocation() throws Exception;

    void createLocation(String country, String city, String place) throws Exception;
}
