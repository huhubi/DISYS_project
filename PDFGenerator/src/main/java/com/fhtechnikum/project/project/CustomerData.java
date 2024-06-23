package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.model.Customer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class CustomerData {

    private static final String URL = "jdbc:postgresql://localhost:30001/customerdb";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";


    public Customer getCustomerById(int id) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "SELECT first_name, last_name FROM customer WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                return new Customer(id, firstName, lastName);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}