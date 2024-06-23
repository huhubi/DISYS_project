package com.fhtechnikum.project.project;

import com.fhtechnikum.project.project.model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CustomerDataTest {

    private CustomerData customerData;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        customerData = new CustomerData();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        // Mock DriverManager
        MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class);
        mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(connection);
    }

    @Test
    public void testGetCustomerById() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("first_name")).thenReturn("Max");
        when(resultSet.getString("last_name")).thenReturn("Mustermann");

        Customer customer = customerData.getCustomerById(1);
        assertEquals(1, customer.getId());
        assertEquals("Max", customer.getFirstName());
        assertEquals("Mustermann", customer.getLastName());
    }


}
