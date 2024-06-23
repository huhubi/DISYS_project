package com.fhtechnikum.project.project.model;

public class Station {
    public String station;
    public Long totalKwh;
    public int customerId;

    public Station(String station, Long totalKwh, int customerId) {
        this.station = station;
        this.totalKwh = totalKwh;
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
    public Long getTotalKwh() {
        return totalKwh;
    }

    public void setTotalKwh(Long totalKwh) {
        this.totalKwh = totalKwh;
    }
}
