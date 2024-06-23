package com.fhtechnikum.project.project;

import com.fhtechnikum.project.RestEndpoint;
import com.fhtechnikum.project.RestEndpointService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestEndpointTest {

    private final RestEndpointService restEndpointService = mock(RestEndpointService.class);
    private final RestEndpoint restEndpoint = new RestEndpoint(restEndpointService);
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    void getInvoice_returnsOk_whenInvoiceIsAvailable() {
        String customerId = "validCustomerId";
        when(restEndpointService.downloadInvoice(customerId, response)).thenReturn(true);

        ResponseEntity<Void> result = restEndpoint.getInvoice(customerId, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(restEndpointService, times(1)).downloadInvoice(customerId, response);
    }

    @Test
    void getInvoice_returnsNotFound_whenInvoiceIsNotAvailable() {
        String customerId = "invalidCustomerId";
        when(restEndpointService.downloadInvoice(customerId, response)).thenReturn(false);

        ResponseEntity<Void> result = restEndpoint.getInvoice(customerId, response);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(restEndpointService, times(1)).downloadInvoice(customerId, response);
    }

    @Test
    void startDataGathering_returnsAccepted_whenCustomerIdIsValid() {
        String customerId = "validCustomerId";
        when(restEndpointService.isCustomerIdValid(customerId)).thenReturn(true);

        ResponseEntity<Void> result = restEndpoint.startDataGathering(customerId);

        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        verify(restEndpointService, times(1)).publishDataGatheringJob(customerId);
    }

    @Test
    void startDataGathering_returnsBadRequest_whenCustomerIdIsInvalid() {
        String customerId = "invalidCustomerId";
        when(restEndpointService.isCustomerIdValid(customerId)).thenReturn(false);

        ResponseEntity<Void> result = restEndpoint.startDataGathering(customerId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(restEndpointService, times(0)).publishDataGatheringJob(customerId);
    }
}