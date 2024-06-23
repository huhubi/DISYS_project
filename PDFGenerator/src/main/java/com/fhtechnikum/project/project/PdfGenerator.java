package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.model.Customer;
import com.fhtechnikum.project.project.model.Station;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class PdfGenerator {
    public void generatePdf(Customer customer) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("../Frontend/FuelStationUI/src/main/resources/files/invoice/" + customer.getId() + ".pdf"));

            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Paragraph header = new Paragraph("Rechnung", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            Paragraph date = new Paragraph(dtf.format(now), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            Font customerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Paragraph customerInfo = new Paragraph("Kunde:", customerFont);
            customerInfo.setSpacingBefore(20);
            document.add(customerInfo);

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            document.add(new Paragraph("Vorname: " + customer.getFirstName(), infoFont));
            document.add(new Paragraph("Nachname: " + customer.getLastName(), infoFont));


            double grandTotal = 0.0;

            List<Station> stations = customer.getStations();
            stations.sort(Comparator.comparing(Station::getStation));

            Map<String, List<Station>> stationMap = new LinkedHashMap<>();
            for (Station station : stations) {
                stationMap.computeIfAbsent(station.getStation(), k -> new ArrayList<>()).add(station);
            }

            for (Map.Entry<String, List<Station>> entry : stationMap.entrySet()) {
                String stationName = entry.getKey();
                List<Station> stationList = entry.getValue();

                Paragraph stationHeader = new Paragraph("Station: " + stationName, customerFont);
                stationHeader.setSpacingBefore(20);
                document.add(stationHeader);

                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);

                PdfPCell c1 = new PdfPCell(new Phrase("Position", customerFont));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Kwh", customerFont));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase("Kosten", customerFont));
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c1);

                table.setHeaderRows(1);

                Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
                double stationTotal = 0.0;

                int positionCounter = 1;
                for (Station station : stationList) {
                    table.addCell(new PdfPCell(new Phrase("Position " + positionCounter++, cellFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(station.getTotalKwh()), cellFont)));

                    double cost = calculateCost(station.getTotalKwh());
                    stationTotal += cost;
                    table.addCell(new PdfPCell(new Phrase(String.format("%.2f €", cost), cellFont)));
                }

                document.add(table);

                Paragraph stationTotalParagraph = new Paragraph("Gesamtkosten für " + stationName + ": " + String.format("%.2f €", stationTotal), infoFont);
                stationTotalParagraph.setAlignment(Element.ALIGN_RIGHT);
                stationTotalParagraph.setSpacingBefore(10);
                document.add(stationTotalParagraph);

                grandTotal += stationTotal;
            }

            Paragraph grandTotalParagraph = new Paragraph("Gesamtkosten: " + String.format("%.2f €", grandTotal), customerFont);
            grandTotalParagraph.setAlignment(Element.ALIGN_RIGHT);
            grandTotalParagraph.setSpacingBefore(20);
            document.add(grandTotalParagraph);

            Paragraph footer = new Paragraph("Vielen Dank für Ihren Einkauf!", dateFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculateCost(Long totalKwh) {
        double costPerKwh = 0.30;
        return totalKwh * costPerKwh;
    }
}
