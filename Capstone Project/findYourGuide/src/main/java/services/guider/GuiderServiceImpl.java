package services.guider;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import configuration.CloudinaryConfig;
import entities.Contract;
import entities.Guider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import services.GeneralService;

import java.io.File;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class GuiderServiceImpl implements GuiderService {

    @Value("${order.server.root.url}")
    private String URL_ROOT_SERVER;
    private JdbcTemplate jdbcTemplate;
    private GeneralService generalService;
    private int LIMIT = 9;

    @Autowired
    public GuiderServiceImpl(JdbcTemplate jdbcTemplate, GeneralService generalService) {
        this.jdbcTemplate = jdbcTemplate;
        this.generalService = generalService;
    }

    @Override
    public Guider findGuiderWithID(long id) throws Exception {
        Guider result = new Guider();
        String query = "select g1.*, a2.user_name from guider as g1 inner join "
                + " account as a2 on g1.guider_id = a2.account_id "
                + " where g1.guider_id = ? ";
        result = jdbcTemplate.queryForObject(query, new RowMapper<Guider>() {
            public Guider mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Guider(rs.getLong("guider_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getTimestamp("date_of_birth").toLocalDateTime(), rs.getString("phone"),
                        rs.getString("about_me"),
                        rs.getLong("contribution"), rs.getString("city"),
                        generalService.checkForNull(rs.getArray("languages")),
                        rs.getBoolean("active"), rs.getLong("rated"), rs.getString("avatar"),
                        rs.getString("passion"), rs.getString("user_name"), rs.getString("profile_video"));
            }
        }, id);
        return result;
    }

    @Override
    public Guider findGuiderWithPostId(long id) throws Exception {
        String query = "select g.* from guider as g, post as p where g.guider_id = p.guider_id and p.post_id = ?";
        return jdbcTemplate.queryForObject(query, new RowMapper<Guider>() {
            public Guider mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Guider(rs.getLong("guider_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getTimestamp("date_of_birth").toLocalDateTime(), rs.getString("phone"),
                        rs.getString("about_me"),
                        rs.getLong("contribution"), rs.getString("city"),
                        generalService.checkForNull(rs.getArray("languages")),
                        rs.getBoolean("active"), rs.getLong("rated"), rs.getString("avatar"),
                        rs.getString("passion"), rs.getString("profile_video"));
            }
        }, id);
    }

    @Override
    public Contract findGuiderContract(long id) throws Exception {
        Contract result;
        String query = "select con_de.* from contract_detail as con_de " +
                "where con_de.contract_id = (select contract_id from contract where guider_id = ?)";
        result = jdbcTemplate.queryForObject(query, new RowMapper<Contract>() {
            @Override
            public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
                Contract ct = new Contract(rs.getLong("contract_id"), rs.getInt("guider_id"),
                        rs.getString("name"), rs.getString("nationality"),
                        rs.getTimestamp("date_of_birth").toLocalDateTime(), rs.getInt("gender"),
                        rs.getString("hometown"),
                        rs.getString("address"), rs.getString("identity_card_number"),
                        rs.getTimestamp("card_issued_date").toLocalDateTime(), rs.getString("card_issued_province"),
                        (rs.getTimestamp("account_active_date") != null ? rs.getTimestamp("account_active_date").toLocalDateTime() : null),
                        (rs.getTimestamp("account_deactive_date") != null ? rs.getTimestamp("account_deactive_date").toLocalDateTime() : null));
                return ct;
            }
        }, id);
        return result;
    }

    @Override
    public List<Contract> getActiveContracts() throws Exception {
        String query = "select * from contract_detail as con_de where con_de.contract_id in (select contract_id from contract)";
        return jdbcTemplate.query(query, new RowMapper<Contract>() {
            @Override
            public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Contract(rs.getInt("contract_id"), rs.getInt("guider_id"), rs.getString("name"),
                        rs.getString("nationality"), rs.getTimestamp("date_of_birth").toLocalDateTime(),
                        rs.getInt("gender"), rs.getString("hometown"), rs.getString("address"),
                        rs.getString("identity_card_number"), rs.getTimestamp("card_issued_date").toLocalDateTime(),
                        rs.getString("card_issued_province"), rs.getString("file_link"));
            }
        });
    }

    @Override
    public long createGuider(Guider newGuider) throws Exception {
        String query = "insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion,profile_video)" +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(query, newGuider.getGuider_id(), newGuider.getFirst_name(), newGuider.getLast_name(),
                Timestamp.valueOf(newGuider.getDate_of_birth()), newGuider.getPhone(), newGuider.getAbout_me(), newGuider.getContribution(),
                newGuider.getCity(), generalService.createSqlArray(Arrays.asList(newGuider.getLanguages())), newGuider.isActive(),
                newGuider.getRated(), newGuider.getAvatar(), newGuider.getPassion(), newGuider.getProfile_video());
        return newGuider.getGuider_id();
    }

    @Override
    public long createGuiderContract(Contract contract) throws Exception {
        String check = "select count(contract_id) from contract where guider_id = ?";
        int count = jdbcTemplate.queryForObject(check, new Object[]{contract.getGuider_id()}, int.class);
        if (count != 0) {
            jdbcTemplate.update("update guider set active = false where guider_id = ?", contract.getGuider_id());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "insert into contract_detail (name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,guider_id,file_link)" +
                "values (?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"contract_id"});
            ps.setString(1, contract.getName());
            ps.setString(2, contract.getNationality());
            ps.setTimestamp(3, Timestamp.valueOf(contract.getDate_of_birth()));
            ps.setLong(4, contract.getGender());
            ps.setString(5, contract.getHometown());
            ps.setString(6, contract.getAddress());
            ps.setString(7, contract.getIdentity_card_number());
            ps.setTimestamp(8, Timestamp.valueOf(contract.getCard_issued_date()));
            ps.setString(9, contract.getCard_issued_province());
            ps.setLong(10, contract.getGuider_id());
            ps.setString(11, contract.getFile_link());
            return ps;
        }, keyHolder);
        return (int) keyHolder.getKey();
    }

    @Override
    public long updateGuiderWithId(Guider guiderUpdate) throws Exception {
        String query = "update guider set first_name = ?, last_name = ?, date_of_birth = ?, phone = ?, about_me = ?, city = ?, " +
                "languages = ?, avatar = ?, passion = ?, profile_video =? where guider_id = ?";
        String[] images = new String[]{guiderUpdate.getAvatar()};
        List<String> avatarList = generalService.convertBase64toImageAndChangeName(images);
        String avatar;
        if (avatarList.isEmpty()) {
            avatar = "https://res.cloudinary.com/findguider/image/upload/account.jpg";
        } else {
            avatar = avatarList.get(0);
        }
        jdbcTemplate.update(query, guiderUpdate.getFirst_name(), guiderUpdate.getLast_name(),
                Timestamp.valueOf(guiderUpdate.getDate_of_birth()), guiderUpdate.getPhone(), guiderUpdate.getAbout_me(), guiderUpdate.getCity(),
                generalService.createSqlArray(Arrays.asList(guiderUpdate.getLanguages())), avatar,
                guiderUpdate.getPassion(), guiderUpdate.getProfile_video(), guiderUpdate.getGuider_id());
        return guiderUpdate.getGuider_id();
    }

    @Override
    public long activateGuider(long id) throws Exception {
        String query = "update guider set active = true where guider_id = ? and active = false";
        jdbcTemplate.update(query, id);
        return id;
    }

    @Override
    public long deactivateGuider(long id) throws Exception {
        String query = "update guider set active = false where guider_id = ? and active = true";
        jdbcTemplate.update(query, id);
        return id;
    }


    @Override
    public List<Guider> searchGuiderByName(String key, int page) throws Exception {
        key = key.toUpperCase().trim().replaceAll(" +", " ");
        List<Guider> result = new ArrayList<>();
        String query = "select g.* from guider as g "
                + " inner join account as a on g.guider_id = a.account_id "
                + " where (upper(g.first_name) like '%" + key + "%' "
                + " or upper(g.last_name) like '%" + key + "%'  or upper(a.user_name) like '%" + key + "%')"
                + " and g.active = true"
                + " order by g.guider_id limit ? offset ?";
        result = jdbcTemplate.query(query, new RowMapper<Guider>() {
            public Guider mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Guider(rs.getLong("guider_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getTimestamp("date_of_birth").toLocalDateTime(), rs.getString("phone"),
                        rs.getString("about_me"),
                        rs.getLong("contribution"), rs.getString("city"),
                        generalService.checkForNull(rs.getArray("languages")),
                        rs.getBoolean("active"), rs.getLong("rated"), rs.getString("avatar"),
                        rs.getString("passion"), rs.getString("profile_video"));
            }
        }, LIMIT, LIMIT * page);
        return result;
    }

    @Override
    public int searchGuiderByNamePageCount(String key) throws Exception {
        key = key.toUpperCase();
        String query = "select count(guider_id) from guider as g "
                + " inner join account as a on g.guider_id = a.account_id "
                + " where upper(g.first_name) like '%" + key + "%' "
                + " or upper(g.last_name) like '%" + key + "%'  or upper(a.user_name) like '%" + key + "%'";
        double count = jdbcTemplate.queryForObject(query, new Object[]{}, double.class);
        int page = (int) Math.ceil(count / LIMIT);
        return page;
    }

    @Override
    public List<Guider> getTopGuiderByRate() throws Exception {
        List<Guider> result = new ArrayList<>();
        String query = "SELECT * FROM guider where active = true and guider_id in (select guider_id from post) order by rated desc limit 6";
        result = jdbcTemplate.query(query, new RowMapper<Guider>() {
            public Guider mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Guider(rs.getLong("guider_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getTimestamp("date_of_birth").toLocalDateTime(), rs.getString("phone"),
                        rs.getString("about_me"),
                        rs.getLong("contribution"), rs.getString("city"),
                        generalService.checkForNull(rs.getArray("languages")),
                        rs.getBoolean("active"), rs.getLong("rated"), rs.getString("avatar"),
                        rs.getString("passion"), rs.getString("profile_video"));
            }
        });
        return result;
    }

    @Override
    public List<Guider> getTopGuiderByContribute() throws Exception {
        List<Guider> result = new ArrayList<>();
        String query = "SELECT * FROM guider where active = true and guider_id in (select guider_id from post) order by contribution desc limit 6";
        result = jdbcTemplate.query(query, new RowMapper<Guider>() {
            public Guider mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Guider(rs.getLong("guider_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getTimestamp("date_of_birth").toLocalDateTime(), rs.getString("phone"),
                        rs.getString("about_me"),
                        rs.getLong("contribution"), rs.getString("city"),
                        generalService.checkForNull(rs.getArray("languages")),
                        rs.getBoolean("active"), rs.getLong("rated"), rs.getString("avatar"),
                        rs.getString("passion"), rs.getString("profile_video"));
            }
        });
        return result;
    }

    @Override
    public void linkGuiderWithContract(long guider_id, long contract_id) throws Exception {
        String check = "select count(guider_id) from contract where guider_id = ?";
        int count = jdbcTemplate.queryForObject(check, new Object[]{guider_id}, int.class);
        if (count == 0) {
            // if new guider with new contract
            // link contract
            String query = "insert into contract (guider_id, contract_id) values (?,?)";
            jdbcTemplate.update(query, guider_id, contract_id);

            // update active date
            String query2 = "update contract_detail set account_active_date = ? where contract_id = ?";
            jdbcTemplate.update(query2, Timestamp.valueOf(LocalDateTime.now()), contract_id);

            // activate guider
            String query3 = "update guider set active = true where guider_id = ?";
            jdbcTemplate.update(query3, guider_id);
        } else {
            // if old guider update their contract
            // de-active old contract
            String getOldContractQuery = "select contract_id from contract where guider_id = ?";
            int oldContractId = jdbcTemplate.queryForObject(getOldContractQuery, new Object[]{guider_id}, int.class);
            String deactivateOldContractQuery = "update contract_detail set account_deactive_date = ? where contract_id = ?";
            jdbcTemplate.update(deactivateOldContractQuery, Timestamp.valueOf(LocalDateTime.now()), oldContractId);

            // link to new contract
            String query = "update contract set contract_id = ? where guider_id = ?";
            jdbcTemplate.update(query, contract_id, guider_id);

            // update active date
            String query2 = "update contract_detail set account_active_date = ? where contract_id = ?";
            jdbcTemplate.update(query2, Timestamp.valueOf(LocalDateTime.now()), contract_id);
        }
    }

    @Override
    public List<Contract> getAllContract() throws Exception {
        // Automatic reject contract without document
        String getRejectList = "select contract_id from contract_detail where file_link is null and account_deactive_date is null";
        List<Long> rejectList = jdbcTemplate.query(getRejectList, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("contract_id");
            }
        });
        if (!rejectList.isEmpty()) {
            for (long id : rejectList) {
                this.rejectContract(id);
            }
        }

        // Get waiting for acceptance contract list
        List<Contract> contractList;
        String query = "select * from contract_detail where account_active_date is null and account_deactive_date is null " +
                "and contract_id not in (select contract_id from contract)";
        contractList = jdbcTemplate.query(query, new RowMapper<Contract>() {
            @Override
            public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Contract(rs.getInt("contract_id"), rs.getInt("guider_id"), rs.getString("name"),
                        rs.getString("nationality"), rs.getTimestamp("date_of_birth").toLocalDateTime(),
                        rs.getInt("gender"), rs.getString("hometown"), rs.getString("address"),
                        rs.getString("identity_card_number"), rs.getTimestamp("card_issued_date").toLocalDateTime(),
                        rs.getString("card_issued_province"), rs.getString("file_link"));
            }
        });
        return contractList;
    }

    @Override
    public void rejectContract(long contract_id) throws Exception {
        String query = "update contract_detail set account_deactive_date = now() where contract_id = ?";
        jdbcTemplate.update(query, contract_id);
    }

    @Override
    public void uploadContractDocument(MultipartFile file, long contract_id) throws Exception {
        CloudinaryConfig cloudinaryConfig = new CloudinaryConfig();
        Cloudinary cloudinary = cloudinaryConfig.getCloudinary();
        byte[] bytes = file.getBytes();
        Long originalId = generalService.generateLongId();
        String fileName = originalId.toString();
        Map uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.asMap("public_id", fileName));
        String docUrl = (String) uploadResult.get("url");
        String query = "update contract_detail set file_link = ? where contract_id = ?";
        jdbcTemplate.update(query, docUrl, contract_id);
    }

    @Override
    public File getDocumentToDownload(long contract_id) throws Exception {
        String query = "select file_link from contract_detail where contract_id = ?";
        String fileUrl = jdbcTemplate.queryForObject(query, new Object[]{contract_id}, String.class);
        File pdfFile = Paths.get(fileUrl).toFile();
        return pdfFile;
    }
}
