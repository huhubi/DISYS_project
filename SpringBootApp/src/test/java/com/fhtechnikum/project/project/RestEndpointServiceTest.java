package com.fhtechnikum.project.project;

import com.fhtechnikum.project.RestEndpointService;
import com.fhtechnikum.project.rabbitmq.RabbitMQService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestEndpointServiceTest {

    private final RabbitMQService rabbitMQService = mock(RabbitMQService.class);
    private final RestEndpointService restEndpointService = new RestEndpointService(rabbitMQService);
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    void isCustomerIdValid_returnsTrue_whenCustomerIdIsValid() {
        String validCustomerId = "12345";
        assertTrue(restEndpointService.isCustomerIdValid(validCustomerId));
    }

    @Test
    void isCustomerIdValid_returnsFalse_whenCustomerIdIsInvalid() {
        String invalidCustomerId = "abcde";
        assertFalse(restEndpointService.isCustomerIdValid(invalidCustomerId));
    }

    @Test
    void publishDataGatheringJob_publishesMessage_whenCalled() {
        String customerId = "12345";
        restEndpointService.publishDataGatheringJob(customerId);
        verify(rabbitMQService, times(1)).publishMessage(customerId);
    }

    @Test
    void downloadInvoice_returnsFalse_whenInvoiceDoesNotExist() {
        String customerId = "abcde";
        assertFalse(restEndpointService.downloadInvoice(customerId, response));
    }
}