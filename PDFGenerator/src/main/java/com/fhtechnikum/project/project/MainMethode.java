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

        Station s1 = new Station("Station 1", 100L);
        Station s5 = new Station("Station 1", 200L);
        Station s2 = new Station("Station 2", 200L);
        Station s3 = new Station("Station 3", 300L);
        customer.setStations(Arrays.asList(s1, s2, s3,s5));

        CustomerData customerData2 = new CustomerData();
        Customer customer2 = customerData2.getCustomerById(2);

         s1 = new Station("Station 1", 100L);
         Station s7 = new Station("Station 1", 200L);
         Station s4 = new Station("Station 1", 300L);
         Station s6 = new Station("Station 1", 20L);
         s5 = new Station("Station 1", 40L);
         s2 = new Station("Station 2", 250L);
         s3 = new Station("Station 3", 500L);
        customer2.setStations(Arrays.asList(s1, s2, s3,s5,s4,s6,s7));

        CustomerData customerData3 = new CustomerData();
        Customer customer3 = customerData3.getCustomerById(3);

         s1 = new Station("Station 1", 1000L);
         s5 = new Station("Station 1", 500L);
         s2 = new Station("Station 2", 300L);
         s3 = new Station("Station 3", 400L);
        customer3.setStations(Arrays.asList(s1, s2, s3,s5));

        if (customer != null) {
            PdfGenerator pdfGenerator = new PdfGenerator();
            pdfGenerator.generatePdf(customer);
            pdfGenerator.generatePdf(customer2);
            pdfGenerator.generatePdf(customer3);

        } else {
            System.out.println("Kein Kunde mit der angegebenen ID gefunden.");
        }
    }
}