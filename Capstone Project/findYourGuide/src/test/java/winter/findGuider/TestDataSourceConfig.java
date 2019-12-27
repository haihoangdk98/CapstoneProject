package winter.findGuider;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class TestDataSourceConfig {

    public DataSource setupDatasource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/test");
        dataSource.setUsername("postgres");
        dataSource.setPassword("korangar");
        return dataSource;
    }

    public void cleanTestDb(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("delete from refund");
        jdbcTemplate.update("delete from review");
        jdbcTemplate.update("delete from trip");
        jdbcTemplate.update("delete from transaction");
        jdbcTemplate.update("delete from plan");
        jdbcTemplate.update("delete from favoritepost");
        jdbcTemplate.update("delete from post");
        jdbcTemplate.update("delete from travelerreviews");
        jdbcTemplate.update("delete from contract");
        jdbcTemplate.update("delete from contract_detail");
        jdbcTemplate.update("delete from guider");
        jdbcTemplate.update("delete from traveler");
        jdbcTemplate.update("delete from locations");
        jdbcTemplate.update("delete from category");
        jdbcTemplate.update("delete from notification");
        jdbcTemplate.update("delete from chatmessage");
        jdbcTemplate.update("delete from feedback");
        jdbcTemplate.update("delete from account");
    }
}
