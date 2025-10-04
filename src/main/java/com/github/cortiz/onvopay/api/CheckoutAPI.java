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
import com.github.cortiz.onvopay.models.CheckoutSession;
import com.github.cortiz.onvopay.models.PaginatedResponse;
import com.github.cortiz.onvopay.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The CheckoutAPI class extends OnvoAPI to provide functionality for interacting
 * with the checkout-related endpoints of the OnvoPay API. It includes methods
 * for creating and managing checkout sessions, specifically handling one-time
 * payment links via the provided API endpoints. This class ensures proper
 * serialization and deserialization of data and provides error handling mechanisms.
 */
public class CheckoutAPI extends OnvoAPI {

    private static final String CHECKOUT_PATH = "/checkout/sessions/one-time-link";
    private static final String EXPIRE_CHECKOUT_PATH = "/checkout/sessions/%s/expire";
    private static final Logger log = LoggerFactory.getLogger(CheckoutAPI.class);

    public CheckoutAPI(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * Creates a new checkout session by sending the provided {@code CheckoutSession} object
     * to the OnvoPay API. This method handles JSON serialization of the request body and
     * parsing of the response body into a {@code CheckoutSession} object.
     * If the operation is successful, it returns an {@code Optional<CheckoutSession>} containing the session details.
     * If an error occurs during the process, an {@code OnvoPayException} is thrown.
     *
     * @param checkoutSession the {@code CheckoutSession} object to be created; must not be null
     * @return an {@code Optional<CheckoutSession>} containing the created checkout session,
     * or {@code Optional.empty()} if the input is null or a failure occurs during serialization
     * @throws OnvoPayException if an HTTP error or other unexpected error occurs while communicating with the API
     */
    public Optional<CheckoutSession> createCheckoutSession(CheckoutSession checkoutSession) throws OnvoPayException {
        if (checkoutSession == null) {
            log.error("Checkout session is null");
            return Optional.empty();
        }
        try {
            var body = writeBody(checkoutSession);
            if (body.isEmpty()) {
                log.error("Unable to serialize checkout session");
                return Optional.empty();
            }
            var response = httpClient.post(CHECKOUT_PATH, body.get());
            if (response.statusCode() != 201) {
                throw handleError(response);
            }
            return readBody(response.body(), CheckoutSession.class);
        } catch (HttpClientException ex) {
            log.error("Error on Http Request", ex);
            throw new OnvoPayException(ex.getMessage());
        }
    }

    /**
     * Expires the checkout session associated with the provided session ID by calling the OnvoPay API.
     * If the session ID is null or blank, the method logs an error and returns an empty {@code Optional}.
     * If the operation is successful, it returns an {@code Optional<CheckoutSession>} containing the updated session.
     * If an error occurs, an {@code OnvoPayException} is thrown.
     *
     * @param sessionId the ID of the checkout session to expire; must not be null or blank
     * @return an {@code Optional<CheckoutSession>} containing the expired session, or {@code Optional.empty()} if the session ID is invalid
     * @throws OnvoPayException if an HTTP error or another unexpected error occurs during the request
     */
    public Optional<CheckoutSession> expire(String sessionId) throws OnvoPayException {
        if (sessionId == null || sessionId.isBlank()) {
            log.error("Checkout session Id is null");
            return Optional.empty();
        }
        try {

            var url = String.format(EXPIRE_CHECKOUT_PATH, sessionId);
            var response = httpClient.post(CHECKOUT_PATH, null);
            if (response.statusCode() != 201) {
                throw handleError(response);
            }
            return readBody(response.body(), CheckoutSession.class);
        } catch (HttpClientException ex) {
            log.error("Error on Http Request", ex);
            throw new OnvoPayException(ex.getMessage());
        }
    }


    /**
     * Retrieves a paginated list of checkout sessions from the OnvoPay API.
     * This method sends a GET request to the specified endpoint and parses
     * the response body into an Optional containing a {@code PaginatedResponse<CheckoutSession>}.
     * If the API call is unsuccessful or an error occurs during processing, an {@code OnvoPayException} is thrown.
     *
     * @return an {@code Optional<PaginatedResponse<CheckoutSession>>} containing the paginated response
     * with the list of checkout sessions; the optional will be empty if the response body cannot
     * be parsed.
     * @throws OnvoPayException if an HTTP error or another unexpected error occurs during the request.
     */
    public Optional<PaginatedResponse<CheckoutSession>> getCheckoutSessions() throws OnvoPayException {

        try {
            var response = httpClient.get(CHECKOUT_PATH);
            if (response.statusCode() != 200) {
                throw handleError(response);
            }
            TypeReference<PaginatedResponse<CheckoutSession>> typeRef = new TypeReference<>() {
            };
            return Optional.of(readBody(response.body(), typeRef).orElseThrow(() -> new OnvoPayException("Unable to read client response")));
        } catch (HttpClientException e) {
            log.error("Error on Http Request", e);
            throw new OnvoPayException(e);
        }
    }

}
