package services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import winter.findGuider.TestDataSourceConfig;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GeneralServiceImplTest {

    @InjectMocks
    private GeneralServiceImpl generalService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        generalService = new GeneralServiceImpl(jdbcTemplate);
        config.cleanTestDb(jdbcTemplate);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createSqlArray() {
        List<String> test = new ArrayList<>();
        test.add("1");
        test.add("2");
        test.add("3");
        test.add("4");
        test.add("5");
        Assert.assertEquals("{\"1\",\"2\",\"3\",\"4\",\"5\"}", generalService.createSqlArray(test).toString());
    }

    @Test(expected = NullPointerException.class)
    public void createSqlArray2() {
        Assert.assertEquals("{\"1\",\"2\",\"3\",\"4\",\"5\"}", generalService.createSqlArray(null).toString());
    }

    @Test
    public void checkForNull() {
        List<String> testList = new ArrayList<>();
        testList.add("1");
        testList.add("2");
        testList.add("3");
        String[] result = new String[]{"1", "2", "3"};
        Assert.assertEquals(result, generalService.checkForNull(generalService.createSqlArray(testList)));
    }

    @Test
    public void checkForNull2() {
        String[] result = new String[]{"unknown"};
        Assert.assertEquals(result, generalService.checkForNull(null));
    }

    @Test
    public void convertBase64toImageAndChangeNameWithException() {

        String[] base64Array = new String[]{"testdatabase"};

        generalService.convertBase64toImageAndChangeName(base64Array);
    }

    @Test
    public void convertBase64toImageAndChangeName() {

        String[] base64Array = new String[]{"jpg:dataimage,testdatabase"};

        generalService.convertBase64toImageAndChangeName(base64Array);
    }
}