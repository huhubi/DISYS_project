package org.example.fuelstationui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainViewController {

    @FXML
    private TextField customerIdField;

    @FXML
    private Label statusLabel;

    @FXML
    protected void handleGenerateInvoice() {
        String customerId = customerIdField.getText();
        if (customerId.isEmpty()) {
            showAlert("Customer ID cannot be empty.");
            return;
        }
        generateInvoice(customerId);
    }

    private void generateInvoice(String customerId) {
        try {
            URL url = new URL("http://localhost:8080/invoices/" + customerId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                statusLabel.setText("Invoice generation started.");
            } else {
                statusLabel.setText("Failed to start invoice generation.");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}