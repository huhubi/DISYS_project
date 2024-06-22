package com.fhtechnikum.project.project;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a charge of a customer.
 * <i> <br>
 *    id SERIAL PRIMARY KEY, <br>
 *    kwh REAL NOT NULL,     <br>
 *    customer_id INTEGER NOT_NULL</i>
 */

@Data
@NoArgsConstructor
public class ChargeModel {

    private Long id;
    private Long kwh;
    private Long customerId;

    public String toCsv() {
        return id + "," + kwh + "," + customerId;
    }
}


