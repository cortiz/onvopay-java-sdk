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

package com.github.cortiz.onvopay.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cortiz.onvopay.testsupport.MockHttpClientHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class HttpClientTest {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HttpClientTest.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static boolean online;

    @BeforeAll
    static void checkOnline() {
        LOG.info("HttpClientTest@BeforeAll - checking connectivity to httpbin.org");
        try {
            online = InetAddress.getByName("httpbin.org").isReachable(2000);
        } catch (IOException e) {
            online = false;
        }
        LOG.info("HttpClientTest@BeforeAll - online={}", online);
    }

    @AfterAll
    static void afterAll() {
        LOG.info("HttpClientTest@AfterAll - tests finished");
    }

    @Test
    @DisplayName("GET with baseUri and header merge against httpbin.org")
    void getWithHeaders() throws Exception {
        LOG.info("HttpClientTest#getWithHeaders - start");
        assumeTrue(online, "Skipping httpbin test when offline");
        HttpClient client = new HttpClient.Builder()
                .baseUri("https://httpbin.org")
                .defaultHeader("X-Default", "foo")
                .requestTimeout(Duration.ofSeconds(10))
                .build();

        Map<String, String> extra = Map.of("X-Extra", "bar");
        HttpResponse<String> resp = client.get("/get?hello=world", extra);
        assertThat(resp.statusCode()).isEqualTo(200);

        JsonNode json = MAPPER.readTree(resp.body());
        assertThat(json.path("args").path("hello").asText()).isEqualTo("world");
        JsonNode headers = json.path("headers");
        assertThat(headers.path("X-Default").asText()).isEqualTo("foo");
        assertThat(headers.path("X-Extra").asText()).isEqualTo("bar");
    }

    @Test
    @DisplayName("POST JSON body echo against httpbin.org")
    void postJson() throws Exception {
        LOG.info("HttpClientTest#postJson - start");
        assumeTrue(online, "Skipping httpbin test when offline");
        HttpClient client = new HttpClient.Builder()
                .baseUri("https://httpbin.org")
                .defaultHeader("Content-Type", "application/json")
                .requestTimeout(Duration.ofSeconds(10))
                .build();

        String body = "{\"name\":\"onvopay\",\"ok\":true}";
        HttpResponse<String> resp = client.post("/post", body);
        assertThat(resp.statusCode()).isEqualTo(200);
        JsonNode json = MAPPER.readTree(resp.body());
        assertThat(json.path("data").asText()).isEqualTo(body);
        assertThat(json.path("headers").path("Content-Type").asText()).contains("application/json");
    }

    @Test
    @DisplayName("PUT/PATCH/DELETE basics against httpbin.org")
    void otherMethods() throws Exception {
        LOG.info("HttpClientTest#otherMethods - start");
        assumeTrue(online, "Skipping httpbin test when offline");
        HttpClient client = new HttpClient.Builder()
                .baseUri("https://httpbin.org")
                .requestTimeout(Duration.ofSeconds(10))
                .build();

        assertThat(client.put("/put", "hello").statusCode()).isEqualTo(200);
        assertThat(client.patch("/patch", "hello").statusCode()).isEqualTo(200);
        assertThat(client.delete("/delete").statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("Absolute URL should bypass baseUri")
    void absoluteUrlBypassesBase() throws Exception {
        LOG.info("HttpClientTest#absoluteUrlBypassesBase - start");
        assumeTrue(online, "Skipping httpbin test when offline");
        HttpClient client = new HttpClient.Builder()
                .baseUri("https://example.invalid/base")
                .requestTimeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> resp = client.get("https://httpbin.org/get");
        assertThat(resp.statusCode()).isEqualTo(200);
        JsonNode json = MAPPER.readTree(resp.body());
        assertThat(json.path("url").asText()).startsWith("https://httpbin.org/get");
    }

    @Test
    @DisplayName("Extra headers override defaults (using mock)")
    void headerOverrideWithMock() throws Exception {
        LOG.info("HttpClientTest#headerOverrideWithMock - start");
        Map<String, String> defaults = Map.of("X-Override", "default", "X-Other", "v1");

        HttpClient client = MockHttpClientHelper.createClient(request -> {
            // Verify the header seen by the underlying request equals the override value
            String hdr = request.headers().firstValue("X-Override").orElse(null);
            String other = request.headers().firstValue("X-Other").orElse(null);
            assertThat(hdr).isEqualTo("extra");
            assertThat(other).isEqualTo("v1"); // preserved

            String body = "ok";
            return new MockHttpClientHelper.SimpleHttpResponse(200, body, Map.of(), request, request.uri());
        }, "https://api.example.com/v1", defaults);

        Map<String, String> extra = Map.of("X-Override", "extra");
        HttpResponse<String> resp = client.get("/path", extra);
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body()).isEqualTo("ok");
    }

    @Test
    @DisplayName("Relative path resolves against baseUri (using mock)")
    void relativePathResolutionWithMock() throws Exception {
        LOG.info("HttpClientTest#relativePathResolutionWithMock - start");
        HttpClient client = MockHttpClientHelper.createClient(request -> {
            // Ensure resolved URI is base + path with single slash
            assertThat(request.uri()).isEqualTo(URI.create("https://api.example.com/v1/items/123"));
            return new MockHttpClientHelper.SimpleHttpResponse(200, "ok", Map.of(), request, request.uri());
        }, "https://api.example.com/v1/", Map.of());

        HttpResponse<String> resp = client.get("items/123");
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body()).isEqualTo("ok");
    }
}
