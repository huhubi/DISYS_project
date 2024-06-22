package com.fhtechnikum.project.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Station {
    private String url;
    private long totalKwh;

    public String toCsv() {
        return url + "," + totalKwh;
    }
}
