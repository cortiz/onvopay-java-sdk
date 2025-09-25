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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.cortiz.onvopay.exceptions.OnvoPayException;
import com.github.cortiz.onvopay.models.OnvoErrorResponse;
import com.github.cortiz.onvopay.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

/**
 * The abstract class OnvoAPI provides a base framework for creating API-specific
 * client implementations that interact with the Onvo API. It provides utility
 * methods for common tasks such as HTTP communication, JSON (de)serialization,
 * and error handling. Subclasses can extend this class to implement specific API
 * functionality.
 */
public abstract class OnvoAPI {

    private final static Logger log = LoggerFactory.getLogger(OnvoAPI.class);
    protected final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    protected OnvoAPI(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    /**
     * Serializes the given entity into a JSON string using an ObjectMapper.
     * If the serialization is successful, an {@code Optional} containing the JSON string is returned.
     * If an error occurs during serialization, an {@code Optional.empty()} is returned.
     *
     * @param entity the object to serialize into JSON
     * @param <T>    the type of the entity to be serialized
     * @return an {@code Optional<String>} containing the serialized JSON string, or {@code Optional.empty()} if an error occurs
     */
    protected <T> Optional<String> writeBody(T entity) {
        try {
            return Optional.of(this.objectMapper.writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            log.error("Error serializing entity to JSON", e);
            return Optional.empty();
        }
    }

    /**
     * Handles error responses from an HTTP request by analyzing the response status code
     * and body, and subsequently constructing an appropriate {@link OnvoPayException}.
     * The method logs error details and attempts to deserialize the response body to
     * acquire specific error information if available.
     *
     * @param response the HTTP response to process, containing the status code and body
     * @return an {@link OnvoPayException} detailing the error, including information such as
     * status code, error code, error messages, and general error description
     */
    protected OnvoPayException handleError(HttpResponse<String> response) {
        log.error("Error creating client expected 201 but got {}", response.statusCode());
        log.debug("Response body: {}", response.body());
        var error = readBody(response.body(), OnvoErrorResponse.class);
        if (error.isEmpty()) {
            log.error("Unable to read onvo error response");
            return new OnvoPayException("Unable to read onvo error response");
        }
        if (error.get().messages() == null || error.get().messages().isEmpty()) {
            log.error("Error messages are empty");
            return new OnvoPayException(error.get().statusCode(), "API_100", List.of(error.get().error()), error.get().error());
        }
        return new OnvoPayException(error.get().statusCode(), "API_100", error.get().messages(), error.get().error());
    }

    /**
     * Deserializes the provided JSON string into an instance of the specified class type.
     * If the deserialization is successful, an {@code Optional} containing the object is returned.
     * If an error occurs during deserialization, an {@code Optional.empty()} is returned.
     *
     * @param body             the JSON string to be deserialized
     * @param expectedResponse the class type to deserialize the JSON string into
     * @param <T>              the type of the object to be returned
     * @return an {@code Optional<T>} containing the deserialized object, or {@code Optional.empty()} if an error occurs
     */
    protected <T> Optional<T> readBody(String body, Class<T> expectedResponse) {
        try {
            return Optional.of(this.objectMapper.readValue(body, expectedResponse));
        } catch (JsonProcessingException e) {
            log.error("Error deserializing response body", e);
            return Optional.empty();
        }
    }

    /**
     * Deserializes the provided JSON string into an instance of the specified type reference.
     * If the deserialization is successful, an {@code Optional} containing the object is returned.
     * If an error occurs during deserialization, an {@code Optional.empty()} is returned.
     *
     * @param body             the JSON string to be deserialized
     * @param expectedResponse the type reference representing the expected type of the deserialized object
     * @param <T>              the type of the object to be deserialized and returned
     * @return an {@code Optional<T>} containing the deserialized object, or {@code Optional.empty()} if an error occurs
     */
    protected <T> Optional<T> readBody(String body, TypeReference<T> expectedResponse) {
        try {
            return Optional.of(this.objectMapper.readValue(body, expectedResponse));
        } catch (JsonProcessingException e) {
            log.error("Error deserializing response body", e);
            return Optional.empty();
        }
    }


}
