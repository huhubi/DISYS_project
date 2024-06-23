package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.model.Customer;
import com.fhtechnikum.project.project.model.Station;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PdfGeneratorTest {

    private PdfGenerator pdfGenerator;
    private Customer customer;

    @Before
    public void setUp() {
        pdfGenerator = new PdfGenerator();
        customer = Mockito.mock(Customer.class);

        when(customer.getId()).thenReturn(1);
        when(customer.getFirstName()).thenReturn("Max");
        when(customer.getLastName()).thenReturn("Mustermann");
    }

    @Test
    public void generatePdfWithMultipleStations() {
        Station s1 = new Station("Station 1", 100L);
        Station s2 = new Station("Station 2", 200L);
        Station s3 = new Station("Station 3", 300L);

        when(customer.getStations()).thenReturn(Arrays.asList(s1, s2, s3));

        pdfGenerator.generatePdf(customer);

        File file = new File("../Frontend/FuelStationUI/src/main/resources/files/invoice/1.pdf");
        assertTrue(file.exists());

        file.delete();
    }

    @Test
    public void generatePdfWithNoStations() throws IOException {
        Mockito.when(customer.getStations()).thenReturn(Arrays.asList());

        pdfGenerator.generatePdf(customer);

        File file = new File("../Frontend/FuelStationUI/src/main/resources/files/invoice/1.pdf");
        assertTrue(file.exists());

        String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        assertTrue(!content.contains("Station:"));

        file.delete();
    }

}