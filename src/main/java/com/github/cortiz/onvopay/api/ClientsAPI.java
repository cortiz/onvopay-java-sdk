/*
 * Copyright (c) 2025, Carlos Ortiz
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *  following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *  following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *      products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.github.cortiz.onvopay.api;

import com.github.cortiz.onvopay.exceptions.HttpClientException;
import com.github.cortiz.onvopay.exceptions.OnvoPayException;
import com.github.cortiz.onvopay.models.CreateCustomer;
import com.github.cortiz.onvopay.models.Customer;
import com.github.cortiz.onvopay.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ClientsAPI class is responsible for managing customer client operations in the context
 * of interacting with the Onvo API. It extends the OnvoAPI base class to provide functionality
 * for creating and managing client-related entities through HTTP requests.
 * <p>
 * This class encapsulates methods that simplify interactions with the API, such as creating
 * customer clients. It utilizes the HTTP client and JSON serialization/deserialization
 * utilities provided by its parent class.
 */
public class ClientsAPI extends OnvoAPI {

    private static final String CREATE_CLIENT_ENDPOINT = "/customers";
    private static final Logger log = LoggerFactory.getLogger(ClientsAPI.class);

    public ClientsAPI(HttpClient httpClient) {
        super(httpClient);
    }


    /**
     * Creates a new customer client by sending the required customer data to the API endpoint.
     * If the operation is successful, the newly created customer is returned. Otherwise, an
     * exception is thrown if an issue occurs during the creation process or network request.
     *
     * @param customer the {@link CreateCustomer} object containing the data of the customer
     *                 to be created, including details such as name, email, address, and phone.
     * @return a {@link Customer} object representing the newly created customer retrieved from
     * the API response.
     * @throws OnvoPayException if there is an error in serialization, HTTP communication, or
     *                          if the API returns an error response.
     */
    public Customer createClient(CreateCustomer customer) throws OnvoPayException {
        if (customer == null) {
            log.error("Customer is null");
            throw new IllegalArgumentException("Customer cannot be null");
        }
        var customerJsonBody = writeBody(customer);
        if (customerJsonBody.isEmpty()) {
            log.error("Unable to serialize customer");
            throw new OnvoPayException("Customer is empty");
        }
        log.debug("Creating client with body: {}", customerJsonBody.get());
        try {
            var response = httpClient.post(CREATE_CLIENT_ENDPOINT, customerJsonBody.get());
            if (response.statusCode() != 201) {
                throw handleError(response);
            }
            log.info("Client created successfully");
            return readBody(response.body(), Customer.class).orElseThrow(() -> new OnvoPayException("Unable to read client response"));
        } catch (HttpClientException e) {
            log.error("Error on Http Request", e);
            throw new OnvoPayException(e.getMessage());
        }
    }


}
