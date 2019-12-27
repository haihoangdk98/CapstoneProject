package services.Location;

import entities.Location;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LocationServiceImpl implements LocationService {
    private JdbcTemplate jdbcTemplate;

    public LocationServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Location> showAllLocation() throws Exception {
        return jdbcTemplate.query("select * from locations", new RowMapper<Location>() {
            @Override
            public Location mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Location(
                        resultSet.getInt("location_id"),
                        resultSet.getString("place"),
                        resultSet.getString("city")
                );
            }
        });
    }

    @Override
    public void createLocation(String country, String city, String place) throws Exception {
        String query = "insert into locations (country, city, place) values (?,?,?)";
        jdbcTemplate.update(query, country, city, place);
    }
}
