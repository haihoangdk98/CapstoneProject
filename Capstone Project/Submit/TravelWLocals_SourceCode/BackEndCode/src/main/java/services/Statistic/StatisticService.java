package services.Statistic;

import entities.Statistic;

import java.util.List;

public interface StatisticService {
    List<Statistic> getStatisticCompletedTrip() throws Exception;

    List<Statistic> getStatisticTotalRevenue() throws Exception;
}
