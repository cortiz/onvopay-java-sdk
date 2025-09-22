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

import com.github.cortiz.onvopay.exceptions.HttpClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A wrapper class around java.net.http.HttpClient providing simplified HTTP request
 * handling with support for default headers, a base URI, and request timeouts.
 * Instances of this class are immutable once created.
 * <p>
 * The class provides methods for performing various HTTP operations such as GET,
 * POST, PUT, DELETE, and PATCH.
 * <p>
 * This class uses a Builder pattern for instantiation, allowing customization
 * such as setting default headers, base URI, and request timeout.
 */
public class HttpClient {

    private final java.net.http.HttpClient httpClient;
    private final URI baseUri;
    private final Map<String, String> defaultHeaders;
    private final Duration requestTimeout;

    private HttpClient(java.net.http.HttpClient httpClient, URI baseUri, Map<String, String> defaultHeaders, Duration requestTimeout) {
        this.httpClient = httpClient;
        this.baseUri = baseUri;
        this.defaultHeaders = defaultHeaders;
        this.requestTimeout = requestTimeout;
    }

    // Public API methods returning HttpResponse<String>
    public HttpResponse<String> get(String path) throws HttpClientException {
        return get(path, null);
    }

    public HttpResponse<String> get(String path, Map<String, String> extraHeaders) throws HttpClientException {
        HttpRequest request = requestBuilder("GET", path, null, extraHeaders);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("I/O error during GET request to " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("Request interrupted during GET to " + request.uri(), e);
        }
    }

    // Internal helpers
    private HttpRequest requestBuilder(String method, String path, String body, Map<String, String> extraHeaders) throws HttpClientException {
        Objects.requireNonNull(method, "method");
        URI uri = resolve(path);
        HttpRequest.Builder b = HttpRequest.newBuilder(uri);
        if (requestTimeout != null) {
            b.timeout(requestTimeout);
        }
        // merge headers (extra overrides default)
        Map<String, String> merged = new HashMap<>(defaultHeaders);
        if (extraHeaders != null) {
            for (Map.Entry<String, String> e: extraHeaders.entrySet()) {
                if (e.getKey() != null && !e.getKey().isBlank() && e.getValue() != null) {
                    merged.put(e.getKey(), e.getValue());
                }
            }
        }
        for (Map.Entry<String, String> e: merged.entrySet()) {
            b.header(e.getKey(), e.getValue());
        }

        HttpRequest.BodyPublisher publisher = (body == null)
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body);

        return b.method(method, publisher).build();
    }

    private URI resolve(String path) throws HttpClientException {
        if (path == null) {
            if (baseUri == null) {
                throw new HttpClientException("Path must not be null when baseUri is not set");
            }
            return baseUri;
        }
        try {
            URI candidate = new URI(path);
            if (candidate.isAbsolute()) {
                return candidate;
            }
        } catch (URISyntaxException ignored) {
            // treat as relative
        }
        if (baseUri == null) {
            throw new HttpClientException("Relative path provided but baseUri is not configured");
        }
        String base = baseUri.toString();
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return URI.create(base + "/" + path);
        }
        if (base.endsWith("/") && path.startsWith("/")) {
            return URI.create(base + path.substring(1));
        }
        return URI.create(base + path);
    }

    public HttpResponse<String> delete(String path) throws HttpClientException {
        return delete(path, null);
    }

    public HttpResponse<String> delete(String path, Map<String, String> extraHeaders) throws HttpClientException {
        HttpRequest request = requestBuilder("DELETE", path, null, extraHeaders);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("I/O error during DELETE request to " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("Request interrupted during DELETE to " + request.uri(), e);
        }
    }

    public HttpResponse<String> post(String path, String body) throws HttpClientException {
        return post(path, body, null);
    }

    public HttpResponse<String> post(String path, String body, Map<String, String> extraHeaders) throws HttpClientException {
        HttpRequest request = requestBuilder("POST", path, body, extraHeaders);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("I/O error during POST request to " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("Request interrupted during POST to " + request.uri(), e);
        }
    }

    public HttpResponse<String> put(String path, String body) throws HttpClientException {
        return put(path, body, null);
    }

    public HttpResponse<String> put(String path, String body, Map<String, String> extraHeaders) throws HttpClientException {
        HttpRequest request = requestBuilder("PUT", path, body, extraHeaders);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("I/O error during PUT request to " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("Request interrupted during PUT to " + request.uri(), e);
        }
    }

    public HttpResponse<String> patch(String path, String body) throws HttpClientException {
        return patch(path, body, null);
    }

    public HttpResponse<String> patch(String path, String body, Map<String, String> extraHeaders) throws HttpClientException {
        HttpRequest request = requestBuilder("PATCH", path, body, extraHeaders);
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpClientException("I/O error during PATCH request to " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("Request interrupted during PATCH to " + request.uri(), e);
        }
    }

    public static class Builder {
        private final Map<String, String> defaultHeaders = new HashMap<>();
        private java.net.http.HttpClient httpClient;
        private URI baseUri;
        private Duration requestTimeout;

        public Builder() {
        }

        public Builder httpClient(java.net.http.HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder baseUri(String baseUri) {
            try {
                this.baseUri = baseUri == null ? null : new URI(baseUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid base URI: " + baseUri, e);
            }
            return this;
        }

        public Builder baseUri(URI baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public Builder defaultHeaders(Map<String, String> headers) {
            if (headers != null) {
                headers.forEach(this::defaultHeader);
            }
            return this;
        }

        public Builder defaultHeader(String name, String value) {
            if (name != null && !name.isBlank()) {
                if (value != null) {
                    defaultHeaders.put(name, value);
                }
            }
            return this;
        }

        public Builder requestTimeout(Duration timeout) {
            this.requestTimeout = timeout;
            return this;
        }

        public HttpClient build() {
            java.net.http.HttpClient httpClient = this.httpClient != null ? this.httpClient : java.net.http.HttpClient.newHttpClient();
            URI base = this.baseUri;
            Map<String, String> headers = Collections.unmodifiableMap(new HashMap<>(defaultHeaders));
            return new HttpClient(httpClient, base, headers, requestTimeout);
        }
    }
}
