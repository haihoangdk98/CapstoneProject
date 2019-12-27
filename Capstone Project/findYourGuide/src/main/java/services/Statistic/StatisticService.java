package services.Statistic;

import entities.Statistic;

import java.time.LocalDate;
import java.util.List;

public interface StatisticService {
    List<Statistic> getStatisticCompletedTrip(LocalDate from, LocalDate to) throws Exception;

    List<Statistic> getStatisticTotalRevenue(LocalDate from, LocalDate to) throws Exception;

    List<Statistic> getStatisticGuiderRevenue(LocalDate from, LocalDate to, long guider_id) throws Exception;
}
