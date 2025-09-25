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

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.cortiz.onvopay.exceptions.HttpClientException;
import com.github.cortiz.onvopay.exceptions.OnvoPayException;
import com.github.cortiz.onvopay.models.Client;
import com.github.cortiz.onvopay.models.CreateClient;
import com.github.cortiz.onvopay.models.PaginatedResponse;
import com.github.cortiz.onvopay.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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
     * @param customer the {@link CreateClient} object containing the data of the customer
     *                 to be created, including details such as name, email, address, and phone.
     * @return a {@link Client} object representing the newly created customer retrieved from
     * the API response.
     * @throws OnvoPayException if there is an error in serialization, HTTP communication, or
     *                          if the API returns an error response.
     */
    public Client createClient(CreateClient customer) throws OnvoPayException {
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
            return readBody(response.body(), Client.class).orElseThrow(() -> new OnvoPayException("Unable to read client response"));
        } catch (HttpClientException e) {
            log.error("Error on Http Request", e);
            throw new OnvoPayException(e.getMessage());
        }
    }

    /**
     * Retrieves a client with the specified client ID by making a call to the API endpoint.
     * If the client is found, it returns the client data wrapped in an {@code Optional}.
     * If the client does not exist or an error occurs, it handles the response accordingly.
     *
     * @param clientId the unique identifier of the client to be retrieved
     * @return an {@code Optional<Client>} containing the client data if found, or an empty Optional if not found
     * @throws OnvoPayException if an error occurs during the HTTP request or if the API returns an unexpected status code
     */
    public Optional<Client> getClient(String clientId) throws OnvoPayException {
        if (clientId == null || clientId.isBlank()) {
            log.error("Client ID is null");
            return Optional.empty();
        }
        log.info("Getting client with ID: {}", clientId);
        try {
            var response = httpClient.get(CREATE_CLIENT_ENDPOINT + "/" + clientId);
            return switch (response.statusCode()) {
                case 200 -> {
                    log.info("Client {} retrieved successfully", clientId);
                    yield readBody(response.body(), Client.class);
                }
                case 404 -> {
                    log.info("Client with id {} not found", clientId);
                    yield Optional.empty();
                }
                default -> throw handleError(response);
            };
        } catch (HttpClientException e) {
            log.error("Error on Http Request", e);
            throw new OnvoPayException(e.getMessage());
        }
    }

    /**
     * Retrieves a paginated list of clients by their email address by making a request to the API endpoint.
     * The method validates the input email and handles HTTP responses to ensure proper error handling.
     *
     * @param mail the email address of the clients to be retrieved. Must not be null or blank.
     * @return an {@code Optional} containing a {@code PaginatedResponse<Client>} with the list of clients if found,
     * or an empty Optional if the email is null, blank, or no clients are found.
     * @throws OnvoPayException if there is an error in the HTTP communication, an issue with the response
     *                          status code, or if the response body cannot be processed.
     */
    public Optional<PaginatedResponse<Client>> getClientsByEmail(String mail) throws OnvoPayException {
        if (mail == null || mail.isBlank()) {
            log.error("Email is null");
            return Optional.empty();
        }
        try {
            var response = httpClient.get(CREATE_CLIENT_ENDPOINT + "?email=" + mail);
            if (response.statusCode() != 200) {
                throw handleError(response);
            }
            TypeReference<PaginatedResponse<Client>> typeRef = new TypeReference<>() {
            };
            return Optional.of(readBody(response.body(), typeRef).orElseThrow(() -> new OnvoPayException("Unable to read client response")));
        } catch (HttpClientException e) {
            log.error("Error on Http Request", e);
            throw new OnvoPayException(e);
        }
    }


    /**
     * Retrieves a paginated list of clients based on the specified parameters.
     * The method allows filtering clients through optional pagination cursors
     * (endingBefore and startingAfter) while respecting the specified limit.
     *
     * @param limit the maximum number of clients to retrieve per page. Must be greater than 0 and less than or equal to 100.
     */
    public Optional<PaginatedResponse<Client>> getClients(int limit, String endingBefore, String startingAfter) throws OnvoPayException {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (limit > 100) {
            throw new IllegalArgumentException("Limit must be less than 100");
        }
        log.info("Getting clients with limit: {}", limit);
        var params = new StringBuilder("?limit=" + limit);
        if (endingBefore != null && !endingBefore.isBlank()) {
            params.append("&endingBefore=").append(endingBefore);
        }
        if (startingAfter != null && !startingAfter.isBlank()) {
            params.append("&startingAfter=").append(startingAfter);
        }
        log.info("Getting clients with params: {}", params);
        try {
            var response = httpClient.get(CREATE_CLIENT_ENDPOINT + params);
            if (response.statusCode() != 200) {
                throw handleError(response);
            }
            TypeReference<PaginatedResponse<Client>> typeRef = new TypeReference<>() {
            };
            return Optional.of(readBody(response.body(), typeRef).orElseThrow(() -> new OnvoPayException("Unable to read client response")));
        } catch (HttpClientException e) {
            log.error("Error on Http Request", e);
            throw new OnvoPayException(e);
        }
    }
}
