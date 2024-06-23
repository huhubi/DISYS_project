package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.model.Customer;
import com.fhtechnikum.project.project.model.Station;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class MainMethode {
    public static void main(String[] args) {
        CustomerData customerData = new CustomerData();
        Customer customer = customerData.getCustomerById(1);

        Station s1 = new Station("Station 1", 100L, 1);
        Station s5 = new Station("Station 1", 200L, 1);
        Station s2 = new Station("Station 2", 200L, 1);
        Station s3 = new Station("Station 3", 300L, 1);
        customer.setStations(Arrays.asList(s1, s2, s3,s5));

        if (customer != null) {
            PdfGenerator pdfGenerator = new PdfGenerator();
            pdfGenerator.generatePdf(customer);
        } else {
            System.out.println("Kein Kunde mit der angegebenen ID gefunden.");
        }
    }
}