package services.Category;

import entities.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import services.GeneralService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoryServiceImpl implements CategoryService {
    private JdbcTemplate jdbcTemplate;
    private GeneralService generalService;

    public CategoryServiceImpl(JdbcTemplate jdbcTemplate, GeneralService gs) {
        this.jdbcTemplate = jdbcTemplate;
        this.generalService = gs;
    }

    @Override
    public List<Category> findAllCategory() throws Exception {
        String query = "select * from category";
        return jdbcTemplate.query(query, new RowMapper<Category>() {
            public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("image")
                );
            }
        });
    }

    @Override
    public void createCategory(Category category) throws Exception {
        String[] images = new String[]{category.getImage()};
        List<String> imageList = generalService.convertBase64toImageAndChangeName(images);
        if (imageList.isEmpty()) {
            throw new Exception("Image was null");
        }
        String query = "insert into category (name,image) values (?,?)";
        jdbcTemplate.update(query, category.getCategory(), imageList.get(0));
    }
}
