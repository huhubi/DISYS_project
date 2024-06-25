package org.example.fuelstationui.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvoiceTest {
    // Create an instance of Invoice with customerId as "1" and viewInvoiceButton as null
    Invoice invoice = new Invoice("1", null);

    // checks if the returned customerId is equal to the one set in the constructor
    @Test
    void getCustomerId() {
        Assertions.assertEquals("1", invoice.getCustomerId());
    }

    // checks if the returned viewInvoiceButton is null as set in the constructor
    @Test
    void getViewInvoiceButton() {
        Assertions.assertNull(invoice.getViewInvoiceButton());
    }
}