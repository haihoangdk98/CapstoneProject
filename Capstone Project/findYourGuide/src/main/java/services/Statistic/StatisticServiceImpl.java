package services.Statistic;

import entities.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StatisticServiceImpl implements StatisticService {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StatisticServiceImpl(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }

    @Override
    public List<Statistic> getStatisticCompletedTrip(LocalDate from, LocalDate to) throws Exception {
        List<Statistic> result;
        String query = "select date_trunc('month', finish_date) as fin_month, count (trip_id) as total_trip " +
                "from trip where status = 'FINISHED' and finish_date between ? and ? " +
                "group by fin_month order by fin_month asc";
        result = jdbcTemplate.query(query, new RowMapper<Statistic>() {
            @Override
            public Statistic mapRow(ResultSet rs, int rowNum) throws SQLException {
                LocalDateTime fin_month = rs.getTimestamp("fin_month").toLocalDateTime();
                return new Statistic(fin_month.getYear(), fin_month.getMonthValue(), rs.getInt("total_trip"));
            }
        }, from, to);
        return result;
    }

    @Override
    public List<Statistic> getStatisticTotalRevenue(LocalDate from, LocalDate to) throws Exception {
        List<Statistic> result;
        String query = "select date_trunc('month', finish_date) as fin_month, sum(fee_paid::float / 100 * 10) as revenue " +
                "from trip where status = 'FINISHED' and finish_date between ? and ? " +
                "group by fin_month order by fin_month asc";
        result = jdbcTemplate.query(query, new RowMapper<Statistic>() {
            @Override
            public Statistic mapRow(ResultSet rs, int rowNum) throws SQLException {
                LocalDateTime fin_month = rs.getTimestamp("fin_month").toLocalDateTime();
                return new Statistic(fin_month.getYear(), fin_month.getMonthValue(), rs.getDouble("revenue"));
            }
        }, from, to);
        return result;
    }

    @Override
    public List<Statistic> getStatisticGuiderRevenue(LocalDate from, LocalDate to, long guider_id) throws Exception {
        List<Statistic> result;
        String query = "select date_trunc('month', finish_date) as fin_month, sum(fee_paid::float / 100 * 90) as revenue " +
                "from trip inner join post on trip.post_id = post.post_id " +
                "where status = 'FINISHED' and finish_date between ? and ? and post.guider_id = ?" +
                "group by fin_month order by fin_month asc";
        result = jdbcTemplate.query(query, new RowMapper<Statistic>() {
            @Override
            public Statistic mapRow(ResultSet rs, int rowNum) throws SQLException {
                LocalDateTime fin_month = rs.getTimestamp("fin_month").toLocalDateTime();
                return new Statistic(fin_month.getYear(), fin_month.getMonthValue(), rs.getDouble("revenue"));
            }
        }, from, to, guider_id);
        return result;
    }
}
