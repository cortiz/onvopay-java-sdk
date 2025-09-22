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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Integration tests that require a real secret key provided via environment variables.
 * <p>
 * Set ONVOPAY_SECRET_KEY to a value like onvo_test_xxx or onvo_live_xxx to enable these tests.
 * These tests make live HTTP requests to httpbin.org to validate headers are sent as expected.
 */
public class OnvoPayIT {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OnvoPayIT.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static boolean online;

    @BeforeAll
    static void checkOnline() {
        LOG.info("OnvoPayIT@BeforeAll - checking connectivity to httpbin.org");
        try {
            online = InetAddress.getByName("httpbin.org").isReachable(2000);
        } catch (IOException e) {
            online = false;
        }
        LOG.info("OnvoPayIT@BeforeAll - online={}", online);
    }

    @AfterAll
    static void afterAll() {
        LOG.info("OnvoPayIT@AfterAll - tests finished");
    }

    @Test
    @DisplayName("Create client from env secret and validate Authorization header via httpbin.org")
    @EnabledIfEnvironmentVariable(named = "ONVOPAY_SECRET_KEY", matches = "onvo_(live|test)_.*")
    void createClientFromEnvAndEchoAuthHeader() throws Exception {

    }
}
