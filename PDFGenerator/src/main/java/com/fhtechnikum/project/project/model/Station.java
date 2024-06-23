package com.fhtechnikum.project.project.model;

public class Station {
    public String station;
    public Long totalKwh;

    public Station(String station, Long totalKwh) {
        this.station = station;
        this.totalKwh = totalKwh;
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
