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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cortiz.onvopay.models.CreateClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that require a real secret key provided via environment variables.
 * <p>
 * Set ONVOPAY_SECRET_KEY to a value like onvo_test_xxx or onvo_live_xxx to enable these tests.
 * These tests make live HTTP requests to httpbin.org to validate headers are sent as expected.
 */
@EnabledIfEnvironmentVariable(named = "ONVOPAY_SECRET_KEY", matches = "onvo_(live|test)_.*")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OnvoClientAPIIT {
    private static final Logger LOG = LoggerFactory.getLogger(OnvoClientAPIIT.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static boolean online;
    private static String onvKey;

    private String testClientId;

    @BeforeAll
    static void setUp() {
        LOG.info("OnvoClientAPIIT@BeforeAll - checking connectivity to https://api.onvopay.com");
        try {
            online = InetAddress.getByName("https://api.onvopay.com").isReachable(2000);
        } catch (IOException e) {
            online = false;
        }
        LOG.info("OnvoPayIT@BeforeAll - online={}", online);
        onvKey = System.getenv().get("ONVOPAY_SECRET_KEY");
        assertNotNull(onvKey, "The ONVOPAY_SECRET_KEY environment variable must be set");
        assertFalse(onvKey.isEmpty(), "The ONVOPAY_SECRET_KEY environment variable must be set");
    }


    @AfterAll
    static void afterAll() {
        LOG.info("OnvoPayIT@AfterAll - tests finished");
    }


    @Test
    @Order(1)
    void createClientFromEnvAndEchoAuthHeader() throws Exception {
        LOG.info("Creating a new client with email");
        var api = new OnvoPayAPIClient(onvKey);
        var newClient = api.clients()
                .createClient(new CreateClient(null, "Test User", "asd@email.com", "Test Subject 1", "+50688880000", null));
        LOG.info("New client created: {}", newClient);
        assertEquals("asd@email.com", newClient.email());
        assertNotNull(newClient.id(), "The client id should not be null");
        assertEquals("+50688880000", newClient.phone());
        assertNull(newClient.shipping());
        testClientId = newClient.id();
        LOG.info("Client created successfully with id {}", newClient.id());
    }

    @Test
    @Order(2)
    void getClientById() throws Exception {
        assertNotNull(testClientId);
        assertFalse(testClientId.isEmpty());
        var api = new OnvoPayAPIClient(onvKey);
        var optionalClient = api.clients().getClient(testClientId);
        assertNotNull(optionalClient, "The client id should not be null");
        assertFalse(optionalClient.isEmpty(), "The client should not be empty");
        var customer = optionalClient.get();
        assertEquals("asd@email.com", customer.email());
        assertNotNull(customer.id(), "The client id should not be null");
        assertEquals("+50688880000", customer.phone());
        assertEquals(testClientId, customer.id());
        assertNull(customer.shipping());
    }


    @Test
    @Order(3)
    void getClientsByEmail() throws Exception {
        var api = new OnvoPayAPIClient(onvKey);
        var optionalPaginatedResponse = api.clients().getClientsByEmail("asd@email.com");
        assertNotNull(optionalPaginatedResponse, "The client id should not be null");
        assertFalse(optionalPaginatedResponse.isEmpty(), "The client should not be empty");
        var paginatedResponse = optionalPaginatedResponse.get();
        assertFalse(paginatedResponse.data().isEmpty(), "The client should not be empty");
        var customer = paginatedResponse.data().getFirst();
        assertEquals("asd@email.com", customer.email());
        assertNotNull(customer.id(), "The client id should not be null");
        assertEquals("+50688880000", customer.phone());
        assertEquals(testClientId, customer.id());
        assertNull(customer.shipping());
    }

    @Test
    @Order(4)
    void getClients() throws Exception {
        var api = new OnvoPayAPIClient(onvKey);
        var optionalPaginatedResponse = api.clients().getClients(1, null, null);
        assertNotNull(optionalPaginatedResponse, "The client id should not be null");
        assertFalse(optionalPaginatedResponse.isEmpty(), "The client should not be empty");
        var paginatedResponse = optionalPaginatedResponse.get();
        assertFalse(paginatedResponse.data().isEmpty(), "The client should not be empty");
        var customer = paginatedResponse.data().getFirst();
        assertEquals("asd@email.com", customer.email());
        assertNotNull(customer.id(), "The client id should not be null");
        assertEquals("+50688880000", customer.phone());
        assertEquals(testClientId, customer.id());
        assertNull(customer.shipping());
    }
}
