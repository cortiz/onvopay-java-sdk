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

package com.github.cortiz.onvopay;

import com.github.cortiz.onvopay.api.ClientsAPI;
import com.github.cortiz.onvopay.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.cortiz.onvopay.utils.Validations.isNullOrEmpty;
import static com.github.cortiz.onvopay.utils.Validations.isValidUrl;

/**
 * A client for accessing the OnvoPay API.
 * <br/>
 * This class provides functionality to authenticate and configure the base URL for
 * interacting with the OnvoPay API. It enforces validation for the base URL and the
 * secret key during initialization to ensure proper configuration.
 * <br/>
 * The base URL for the OnvoPay API defaults to "<a href="https://api.onvopay.com/v1">https://api.onvopay.com/v1</a>" if not
 * explicitly provided. The secret key must start with either "onvo_live_" or "onvo_test_"
 * to indicate production or testing usage, respectively.
 */
public class OnvoPayAPIClient {

    private static final Logger log = LoggerFactory.getLogger(OnvoPayAPIClient.class);
    /**
     * The default base URL for accessing the OnvoPay API.
     * <br/>
     * This URL serves as the entry point for all API requests to the OnvoPay platform.
     * It is used by the {@code OnvoPayAPIClient} to interact with the version 1 of the
     * OnvoPay API. If a different base URL is required (e.g., for testing or development purposes),
     * it can be explicitly specified when creating an instance of {@code OnvoPayAPIClient}.
     * <br/>
     * The value of this constant is validated during the initialization of the client to ensure
     * it is a properly formed and absolute URL.
     * <br/>
     * Default value: {@code "https://api.onvopay.com/v1"}.
     */
    public static final String BASE_URL = "https://api.onvopay.com/v1";
    private final String baseUrl;
    private final String secretKey;
    private final HttpClient httpClient;

    /**
     * Constructs a new instance of the OnvoPayAPIClient with the specified secret key.
     * The base URL is set to a default value.
     *
     * @param secretKey the secret key used for API authentication. Must be non-null, non-empty, and
     *                  start with "onvo_live_" or "onvo_test_" indicating production or testing mode, respectively.
     * @throws IllegalArgumentException if secretKey is null, empty, or does not have the required prefix.
     */
    public OnvoPayAPIClient(String secretKey) {
        this(BASE_URL, secretKey);
    }

    /**
     * Constructs a new instance of the OnvoPayAPIClient with the specified base URL and secret key.
     * <br/>
     * The constructor validates the input parameters to ensure that the base URL is a valid, absolute URL
     * and that the secret key starts with either "onvo_live_" or "onvo_test_", indicating production or
     * testing mode, respectively. If any validation fails, an IllegalArgumentException is thrown.
     *
     * @param baseUrl   the base URL of the OnvoPay API. Must be a non-null, non-empty, valid absolute URL.
     * @param secretKey the secret key used for API authentication. Must be non-null, non-empty, and
     *                  start with "onvo_live_" or "onvo_test_".
     * @throws IllegalArgumentException if baseUrl is null, empty, or invalid, or if secretKey is null,
     *                                  empty, or does not have the required prefix.
     */
    public OnvoPayAPIClient(String baseUrl, String secretKey) {
        log.info("Initializing OnvoPayAPIClient with base URL: {}", baseUrl);
        if (isNullOrEmpty(baseUrl)) {
            throw new IllegalArgumentException("Base URL cannot be null or empty.");
        }
        if (isNullOrEmpty(secretKey)) {
            throw new IllegalArgumentException("Secret key cannot be null or empty.");
        }
        if (!isValidUrl(baseUrl)) {
            throw new IllegalArgumentException("Invalid base URL.");
        }
        if (!(secretKey.startsWith("onvo_live_") || secretKey.startsWith("onvo_test_"))) {
            throw new IllegalArgumentException("Secret Key");
        }
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
        this.httpClient = new HttpClient.Builder()
                .baseUri(this.baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + this.secretKey)
                .build();
    }


    public ClientsAPI clients() {
        return new ClientsAPI(this.httpClient);
    }
}
