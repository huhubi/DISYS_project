package com.fhtechnikum.project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * This class is responsible for the REST API. <br>
 * There are two API routes: <br>
 *
 *<ul>
 *  <li>Description:</li>
 *  <li>/invoices/customer-id [POST]</li>
 *  <li>Starts data gathering job</li>
 *  <li>/invoices/customer-id [GET]</li>
 *  <li>Returns invoice PDF with download link and creation time</li>
 *  <li>Returns 404 Not Found, if it’s not available</li>
 *</ul>
 */
@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class RestEndpoint {
    private final RestEndpointService restEndpointService;

    /**
     * Returns invoice PDF with download link and creation time
     * Returns 404 Not Found, if it’s not available
     * @param customerId customer id
     * @param response HTTP response
     * @return invoice PDF with download link and creation time
     */
    @GetMapping("/{customer-id}")
    public ResponseEntity<Void> getInvoice(@PathVariable("customer-id") String customerId, HttpServletResponse response) {
        if (restEndpointService.downloadInvoice(customerId, response)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Starts data gathering job
     * @param customerId customer id
     * @return 202 Accepted
     */
    @PostMapping("/{customer-id}")
    public ResponseEntity<Void> startDataGathering(@PathVariable("customer-id") String customerId) {
        if(!restEndpointService.isCustomerIdValid(customerId)) {
            return ResponseEntity.badRequest().build();
        }
        restEndpointService.publishDataGatheringJob(customerId);
        return ResponseEntity.accepted().build();
    }
}

