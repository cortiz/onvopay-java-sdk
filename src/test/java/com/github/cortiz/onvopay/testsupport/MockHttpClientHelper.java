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

package com.github.cortiz.onvopay.testsupport;

import com.github.cortiz.onvopay.utils.HttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Test-only helper to create a com.github.cortiz.onvopay.utils.HttpClient wired with
 * a mocked java.net.http.HttpClient. This allows unit tests to simulate HTTP responses
 * without performing real network I/O.
 */
public final class MockHttpClientHelper {

    private MockHttpClientHelper() {
    }

    /**
     * Creates an onvopay HttpClient configured with a mocked underlying Java HttpClient
     * that uses the provided responder to generate responses.
     */
    public static HttpClient createClient(Responder responder, String baseUri, Map<String, String> defaultHeaders) {
        java.net.http.HttpClient mock = new MockJavaHttpClient(responder);
        HttpClient.Builder b = new HttpClient.Builder().httpClient(mock);
        if (baseUri != null) b.baseUri(baseUri);
        if (defaultHeaders != null) b.defaultHeaders(defaultHeaders);
        return b.build();
    }

    @FunctionalInterface
    public interface Responder {
        HttpResponse<String> respond(HttpRequest request) throws java.io.IOException, InterruptedException;
    }

    /**
     * Minimal implementation of java.net.http.HttpClient for testing.
     */
    public static final class MockJavaHttpClient extends java.net.http.HttpClient {
        private final Responder responder;

        public MockJavaHttpClient(Responder responder) {
            this.responder = responder;
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.of(Duration.ofSeconds(5));
        }

        @Override
        public java.net.http.HttpClient.Redirect followRedirects() {
            return java.net.http.HttpClient.Redirect.NEVER;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            return null;
        }

        @Override
        public SSLParameters sslParameters() {
            return new SSLParameters();
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public java.net.http.HttpClient.Version version() {
            return java.net.http.HttpClient.Version.HTTP_1_1;
        }

        @Override
        public Optional<Executor> executor() {
            return Optional.empty();
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
                throws java.io.IOException, InterruptedException {
            @SuppressWarnings("unchecked")
            HttpResponse<T> resp = (HttpResponse<T>) responder.respond(request);
            return resp;
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            try {
                @SuppressWarnings("unchecked")
                HttpResponse<T> resp = (HttpResponse<T>) responder.respond(request);
                return CompletableFuture.completedFuture(resp);
            } catch (Exception e) {
                CompletableFuture<HttpResponse<T>> cf = new CompletableFuture<>();
                cf.completeExceptionally(e);
                return cf;
            }
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            return sendAsync(request, responseBodyHandler);
        }
    }

    /**
     * Simple HttpResponse implementation for tests.
     */
    public record SimpleHttpResponse(int statusCode, String body, HttpHeaders headers, HttpRequest request, URI uri) implements HttpResponse<String> {
        public SimpleHttpResponse(int statusCode, String body, Map<String, List<String>> headers, HttpRequest request, URI uri) {
            this(statusCode, body, HttpHeaders.of(headers != null ? headers : Map.of(), (k, v) -> true), request, uri);

        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public java.net.http.HttpClient.Version version() {
            return java.net.http.HttpClient.Version.HTTP_1_1;
        }
    }
}
