package services.Category;

import entities.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import services.GeneralService;
import winter.findGuider.TestDataSourceConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Mock
    GeneralService generalService;

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        categoryService = new CategoryServiceImpl(jdbcTemplate, generalService);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into category (name,image) values ('Food','Food.jpg')");
        jdbcTemplate.update("insert into category (name,image) values ('History','History.jpg')");
        jdbcTemplate.update("insert into category (name,image) values ('Culture','Culture.jpg')");
        jdbcTemplate.update("insert into category (name,image) values ('Night Tour','Night Tour.jpg')");
        jdbcTemplate.update("insert into category (name,image) values ('Art','Art.jpg')");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllCategory() throws Exception {
        Assert.assertEquals(5, categoryService.findAllCategory().size());
    }

    @Test
    public void createCategory() throws Exception {
        jdbcTemplate.update("delete from category");
        Category category = new Category(1,"test","test.jpg");
        String[] x = new String[]{"test.jpg"};
        List<String> temp = new ArrayList<>();
        temp.add("test.jpg");

        when(generalService.convertBase64toImageAndChangeName(x)).thenReturn(temp);
        categoryService.createCategory(category);
        Assert.assertEquals(1, categoryService.findAllCategory().size());
    }

    @Test(expected = Exception.class)
    public void createCategory2() throws Exception {
        jdbcTemplate.update("delete from category");
        Category category = new Category();
        categoryService.createCategory(category);
        Assert.assertEquals(1, categoryService.findAllCategory().size());
    }
}
