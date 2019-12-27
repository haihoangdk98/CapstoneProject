package services.Location;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import winter.findGuider.TestDataSourceConfig;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceImplTest {

    @InjectMocks
    LocationServiceImpl locationService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        locationService = new LocationServiceImpl(jdbcTemplate);
        config.cleanTestDb(jdbcTemplate);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void showAllLocation() throws Exception {
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (2,'Vietnam','Hanoi','Ho Tay')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (3,'Vietnam','Hanoi','Pho Co')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (4,'Vietnam','Hanoi','Trang Tien')");
        Assert.assertEquals(4, locationService.showAllLocation().size());
    }

    @Test
    public void createLocation() throws Exception {
        locationService.createLocation("vietnam", "hanoi", "Hoan Kiem");
        Assert.assertEquals(1, locationService.showAllLocation().size());
    }
}