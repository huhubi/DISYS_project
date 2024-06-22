package com.fhtechnikum.project.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Invoice {
    private long invoiceNumber;
    private long customerId;
    private LocalDate invoiceDate;
    private long totalKwh;
    private List<Station> stations = new ArrayList<>();

    public String toCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append(invoiceNumber).append(",");
        csv.append(customerId).append(",");
        csv.append(invoiceDate).append(",");
        csv.append(totalKwh).append("\n");

        for (Station station : stations) {
            csv.append(station.toCsv()).append("\n");
        }

        return csv.toString();
    }
}
