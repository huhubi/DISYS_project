package org.example.fuelstationui;


import org.example.fuelstationui.model.Invoice;
import org.example.fuelstationui.service.InvoiceGeneratorService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Controller for the Invoice Generator JavaFX App.
 */
public class InvoiceGeneratorController {
    private static final String BASE_URL = "http://localhost:8080/invoices/";
    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();
    private static final InvoiceGeneratorService invoiceGeneratorService = InvoiceGeneratorService.getInstance();

    @FXML
    private TextField customerIdField;

    @FXML
    private  TableView<Invoice> invoiceTable;

public void initialize() {
    invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<Invoice, String> customerIdColumn = new TableColumn<>("Customer ID");
    customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

    TableColumn<Invoice, Button> viewInvoiceColumn = new TableColumn<>("View Invoice");
    viewInvoiceColumn.setCellValueFactory(new PropertyValueFactory<>("viewInvoiceButton"));

    invoiceTable.getColumns().setAll(customerIdColumn, viewInvoiceColumn);
    invoiceTable.setItems(invoices);
}

    /**
     * Generates an invoice for the customer ID entered in the text field.
     */


    @FXML
    protected void onClickGenerateInvoice() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            String localFilePath = "src/main/resources/files/invoice/" + customerId + ".pdf";
            File pdfFile = new File(localFilePath);
            if (pdfFile.exists()) {
                System.out.println("Invoice already exists for customer ID: " + customerId);
                if (!isInvoiceInTable(customerId)) {
                    invoiceTable.getItems().add(new Invoice(customerId, createViewInvoiceButton(customerId)));
                }
                showAlert(Alert.AlertType.INFORMATION, "Information", "Invoice already exists!");
            } else {
                try {
                    URL url = new URL(BASE_URL + customerId);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.getResponseCode();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                        // wait 5 seconds for the invoice to be generated
                        Thread.sleep(5000);
                        if (invoiceGeneratorService.getResponseGETRequest(customerId).responseCode() == HttpURLConnection.HTTP_OK) {
                            System.out.println("Invoice generated for customer ID: " + customerId);
                            invoiceTable.getItems().add(new Invoice(customerId, createViewInvoiceButton(customerId)));
                            showAlert(Alert.AlertType.CONFIRMATION, "Information", "Invoice generated!");
                        } else {
                            System.out.println("Invoice generation failed for customer ID: " + customerId);
                            showAlert(Alert.AlertType.ERROR, "Error", "Invoice generation failed!");
                        }
                    } else {
                        System.out.println("Invoice generation failed for customer ID: " + customerId);
                        showAlert(Alert.AlertType.ERROR, "Error", "Invoice generation failed!");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Invoice generation failed due to an error!");
                }
            }
        }
    }
    /**
     * Generates the invoice file locally.
     * @param customerId The customer ID for which the invoice should be generated.
     * @param localFilePath The local file path where the invoice should be saved.
     * @throws IOException
     * @throws InterruptedException
     */
    private void generateInvoice(String customerId, String localFilePath) throws IOException, InterruptedException {
        // Simulate invoice generation and saving
        File pdfFile = new File(localFilePath);
        if (!pdfFile.exists()) {
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(("Invoice for customer ID: " + customerId).getBytes());
            }
            Thread.sleep(1000); // Simulate delay for generating the invoice
        }
    }

    /**
     * Checks if the invoice is already in the table.
     * @param customerId The customer ID to check.
     * @return true if the invoice is in the table, false otherwise.
     */
    private boolean isInvoiceInTable(String customerId) {
        return invoiceTable.getItems().stream().anyMatch(invoice -> invoice.getCustomerId().equals(customerId));
    }

    /**
     * Creates a button that opens the invoice for the given customer ID.
     * @param customerId The customer ID for which the invoice should be opened.
     * @return The button that opens the invoice.
     */
    private Button createViewInvoiceButton(String customerId) {
        Button viewInvoiceButton = new Button("View");
        viewInvoiceButton.setOnAction(e -> viewInvoice(customerId));
        return viewInvoiceButton;
    }

    /**
     * Opens the invoice for the given customer ID.
     *
     * @param customerId The customer ID for which the invoice should be opened.
     * @return
     */
    private boolean viewInvoice(String customerId) {
        String localFilePath = "src/main/resources/files/invoice/" + customerId + ".pdf";
        File pdfFile = new File(localFilePath);
        if (pdfFile.exists()) {
            try {
                Desktop.getDesktop().open(pdfFile);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Invoice not found!");
        }
        return false;
    }

    /**
     * Shows an alert with the specified parameters.
     * @param alertType The type of alert.
     * @param title The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}