package entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Statistic {
    private long year;
    private long month;
    private int trips;
    private double revenue;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate from;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate to;

    public Statistic() {
    }

    public Statistic(long year, long month, int trips, double revenue, LocalDate from, LocalDate to) {
        this.year = year;
        this.month = month;
        this.trips = trips;
        this.revenue = revenue;
        this.from = from;
        this.to = to;
    }

    public Statistic(long year, long month, int trips) {
        this.year = year;
        this.month = month;
        this.trips = trips;
    }

    public Statistic(long year, long month, double revenue) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public long getMonth() {
        return month;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public int getTrips() {
        return trips;
    }

    public void setTrips(int trips) {
        this.trips = trips;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}
