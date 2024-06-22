package com.fhtechnikum.project.project;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a station central database.
 * <i> <br>
 * CREATE TABLE IF NOT EXISTS station (<br>
 *    id SERIAL PRIMARY KEY,<br>
 *    db_url VARCHAR(255) NOT NULL,<br>
 *    lat REAL NOT NULL,<br>
 *    lng REAL NOT NULL<br>
 * ); </i>
 */

@Data
@NoArgsConstructor
public class StationModel {
    private Long id;
    private String dbUrl;
    private Double lat;
    private Double lng;

    public String toCsv() {
        return id + "," + dbUrl + "," + lat + "," + lng;
    }
}

