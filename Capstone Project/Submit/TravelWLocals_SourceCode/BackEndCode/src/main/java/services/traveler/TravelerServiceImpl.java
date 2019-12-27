package services.traveler;

import entities.Traveler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import services.GeneralService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class TravelerServiceImpl implements TravelerService {
    private JdbcTemplate jdbcTemplate;
    private GeneralService generalService;

    @Value("${order.server.root.url}")
    private String URL_ROOT_SERVER;

    @Autowired
    public TravelerServiceImpl(JdbcTemplate jdbcTemplate, GeneralService gs) {
        this.jdbcTemplate = jdbcTemplate;
        this.generalService = gs;
    }

    @Override
    public boolean createTraveler(Traveler newTraveler) throws Exception {
        String insertQuery = "insert into traveler (traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(insertQuery, newTraveler.getTraveler_id(), newTraveler.getFirst_name(), newTraveler.getLast_name(),
                newTraveler.getPhone(), newTraveler.getGender(), Timestamp.valueOf(newTraveler.getDate_of_birth()),
                newTraveler.getStreet(), newTraveler.getHouse_number(), newTraveler.getSlogan(),
                newTraveler.getPostal_code(), newTraveler.getAbout_me(),
                generalService.createSqlArray(Arrays.asList(newTraveler.getLanguage())),
                newTraveler.getCountry(), newTraveler.getCity(), newTraveler.getAvatar_link());
        return true;
    }

    @Override
    public Traveler findTravelerWithId(long id) throws Exception {
        List<Traveler> searchTraveler = new ArrayList<>();
        String query = "select * from traveler where traveler_id = ?";
        searchTraveler = jdbcTemplate.query(query, new RowMapper<Traveler>() {
            @Override
            public Traveler mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Traveler(rs.getLong("traveler_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("phone"),
                        rs.getInt("gender"), rs.getTimestamp("date_of_birth").toLocalDateTime(),
                        rs.getString("street"), rs.getString("house_number"),
                        rs.getString("postal_code"),
                        rs.getString("slogan"), rs.getString("about_me"),
                        generalService.checkForNull(rs.getArray("language")), rs.getString("country"),
                        rs.getString("city"), rs.getString("avatar_link"));
            }
        }, id);
        if (searchTraveler.isEmpty()) {
            return null;
        }
        return searchTraveler.get(0);
    }

    @Override
    public void updateTraveler(Traveler travelerNeedUpdate) throws Exception {
        String query = "update traveler set first_name = ?, last_name = ?, phone = ?, gender = ?," +
                "date_of_birth = ?, street = ?, house_number = ?, slogan = ?, postal_code = ?, about_me = ?," +
                "language = ?, country = ?, city = ?, avatar_link = ? where traveler_id = ?";
        String[] images = new String[]{travelerNeedUpdate.getAvatar_link()};
        List<String> avatarList = generalService.convertBase64toImageAndChangeName(images);
        String avatar;
        if (avatarList.isEmpty()) {
            avatar = "https://res.cloudinary.com/findguider/image/upload/account.jpg";
        } else {
            avatar = avatarList.get(0);
        }
        jdbcTemplate.update(query, travelerNeedUpdate.getFirst_name(), travelerNeedUpdate.getLast_name(),
                travelerNeedUpdate.getPhone(), travelerNeedUpdate.getGender(),
                Timestamp.valueOf(travelerNeedUpdate.getDate_of_birth()), travelerNeedUpdate.getStreet(),
                travelerNeedUpdate.getHouse_number(), travelerNeedUpdate.getSlogan(), travelerNeedUpdate.getPostal_code(),
                travelerNeedUpdate.getAbout_me(), generalService.createSqlArray(Arrays.asList(travelerNeedUpdate.getLanguage())),
                travelerNeedUpdate.getCountry(), travelerNeedUpdate.getCity(), avatar, travelerNeedUpdate.getTraveler_id());
    }

    @Override
    public void favoritePost(int traveler_id, int post_id) throws Exception {
        String query = "insert into favoritepost (traveler_id, post_id) values (?,?)";
        jdbcTemplate.update(query, traveler_id, post_id);
    }

    @Override
    public boolean isLackingProfile(long traveler_id) throws Exception {
        String query = "select traveler_id, first_name, last_name, phone from traveler where traveler_id = ?";
        Traveler traveler = jdbcTemplate.queryForObject(query, new RowMapper<Traveler>() {
            @Override
            public Traveler mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Traveler(rs.getLong("traveler_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("phone"));
            }
        }, traveler_id);
        if (traveler.getFirst_name() == null || "".equals(traveler.getFirst_name())) {
            return true;
        }
        if (traveler.getLast_name() == null || "".equals(traveler.getLast_name())) {
            return true;
        }
        if (traveler.getPhone() == null || "".equals(traveler.getPhone())) {
            return true;
        }
        return false;
    }

    @Override
    public void updateLackingProfile(Traveler traveler) throws Exception {
        String query = "update traveler set first_name = ?, last_name = ?, phone = ? where traveler_id = ?";
        jdbcTemplate.update(query, traveler.getFirst_name(), traveler.getLast_name(), traveler.getPhone(), traveler.getTraveler_id());
    }
    
    @Override
    public void unlikePost(int traveler_id, int post_id) throws Exception {
        String query = "DELETE FROM favoritepost WHERE  traveler_id = ? and post_id = ?";
        jdbcTemplate.update(query, traveler_id, post_id);
    }
}
