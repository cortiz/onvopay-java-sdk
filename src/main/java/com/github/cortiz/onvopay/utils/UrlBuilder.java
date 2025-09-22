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

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.github.cortiz.onvopay.utils.Validations.isNullOrEmpty;
import static com.github.cortiz.onvopay.utils.Validations.isValidUrl;

/**
 * A small, dependency-free URL builder that safely concatenates path segments and query parameters
 * to a base URL.
 */
public final class UrlBuilder {

    private final URI base;
    private final List<String> pathSegments = new ArrayList<>();
    private final Map<String, List<String>> queryParams = new LinkedHashMap<>();

    private UrlBuilder(String baseUrl) {
        if (!isValidUrl(baseUrl)) {
            throw new IllegalArgumentException("Invalid base URL.");
        }
        this.base = URI.create(baseUrl);
    }

    public static UrlBuilder from(String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    /**
     * Adds one or more path segments to the URL being constructed. Leading and trailing slashes in
     * provided segments are trimmed. Null or empty segments are ignored.
     *
     * @param segments the path segments to add to the URL; may include one or more strings, and any
     *                 null or empty segments will be ignored
     * @return the current {@code UrlBuilder} instance for method chaining
     */
    public UrlBuilder addPathSegment(String... segments) {
        if (segments == null) return this;
        for (String s: segments) {
            if (!isNullOrEmpty(s)) {
                // Normalize to remove any leading/trailing slashes for segments
                String normalized = trimSlashes(s);
                if (!normalized.isEmpty()) {
                    pathSegments.add(normalized);
                }
            }
        }
        return this;
    }

    private static String trimSlashes(String s) {
        int start = 0;
        int end = s.length();
        while (start < end && s.charAt(start) == '/') start++;
        while (end > start && s.charAt(end - 1) == '/') end--;
        return s.substring(start, end);
    }

    /**
     * Adds multiple query parameters to the URL being constructed. Each key-value pair in the provided map
     * is added as a separate query parameter. If the map is null, no action is taken. If a value in the map
     * is null, the parameter is added as a key only (no "=value"). Multiple calls with the same key will
     * preserve order and allow repeated parameters.
     *
     * @param params a map containing query parameter keys and their corresponding values; may be null
     * @return the current {@code UrlBuilder} instance for method chaining
     */
    public UrlBuilder addQueryParams(Map<String, String> params) {
        if (params == null) return this;
        for (Map.Entry<String, String> e: params.entrySet()) {
            addQueryParam(e.getKey(), e.getValue());
        }
        return this;
    }

    /**
     * Adds a query parameter. If value is null, the parameter will be added as a key only (no =value).
     * Multiple calls with the same key will preserve order and create repeated parameters.
     */
    public UrlBuilder addQueryParam(String key, String value) {
        if (isNullOrEmpty(key)) return this;
        queryParams.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        return this;
    }

    /**
     * Builds the final URL as a string.
     *
     * @return the constructed URL in string form
     */
    public String build() {
        return buildUri().toString();
    }

    /**
     * Constructs a URI object based on the current state of the builder. The method assembles
     * the full URI by combining the scheme, authority, path segments, query parameters, and
     * fragment, ensuring proper encoding and handling of special cases such as empty segments
     * and trailing slashes.
     *
     * @return a {@code URI} object representing the constructed URI
     */
    public URI buildUri() {
        // Build the full URL string manually to avoid double-encoding of percent characters
        StringBuilder full = new StringBuilder();
        String scheme = base.getScheme();
        String authority = base.getRawAuthority();
        full.append(scheme).append("://").append(authority);

        String basePath = Objects.toString(base.getRawPath(), "");
        if (basePath.equals("/")) basePath = "";
        basePath = removeTrailingSlash(basePath);
        if (!isNullOrEmpty(basePath)) {
            full.append(basePath);
        }
        for (String seg: pathSegments) {
            if (full.isEmpty() || full.charAt(full.length() - 1) != '/') {
                full.append('/');
            } else if (full.charAt(full.length() - 1) == '/' && seg.isEmpty()) {
                // skip empty segments that would produce double slashes
                continue;
            }
            full.append(encodePathSegment(seg));
        }

        String existingQuery = base.getRawQuery();
        String newQuery = buildQueryString();
        if (!isNullOrEmpty(existingQuery) || !isNullOrEmpty(newQuery)) {
            full.append('?');
            if (!isNullOrEmpty(existingQuery)) {
                full.append(existingQuery);
            }
            if (!isNullOrEmpty(existingQuery) && !isNullOrEmpty(newQuery)) {
                full.append('&');
            }
            if (!isNullOrEmpty(newQuery)) {
                full.append(newQuery);
            }
        }

        String fragment = base.getRawFragment();
        if (!isNullOrEmpty(fragment)) {
            full.append('#').append(fragment);
        }
        return URI.create(full.toString());
    }

    private static String removeTrailingSlash(String s) {
        if (isNullOrEmpty(s)) return s;
        if (s.endsWith("/")) return s.substring(0, s.length() - 1);
        return s;
    }

    private static String encodePathSegment(String segment) {
        // Percent-encode all non-unreserved characters in the segment.
        // Unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
        StringBuilder sb = new StringBuilder(segment.length() * 2);
        for (int i = 0; i < segment.length(); i++) {
            char c = segment.charAt(i);
            if (isUnreserved(c)) {
                sb.append(c);
            } else {
                byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                for (byte b: bytes) {
                    sb.append('%');
                    String hex = Integer.toHexString(b & 0xFF).toUpperCase();
                    if (hex.length() == 1) sb.append('0');
                    sb.append(hex);
                }
            }
        }
        return sb.toString();
    }

    private String buildQueryString() {
        StringBuilder qs = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, List<String>> e: queryParams.entrySet()) {
            String key = e.getKey();
            List<String> values = e.getValue();
            if (values == null || values.isEmpty()) {
                if (!first) qs.append('&');
                qs.append(encodeQueryComponent(key));
                first = false;
            } else {
                for (String v: values) {
                    if (!first) qs.append('&');
                    qs.append(encodeQueryComponent(key));
                    if (v != null) {
                        qs.append('=');
                        qs.append(encodeQueryComponent(v));
                    }
                    first = false;
                }
            }
        }
        return qs.toString();
    }

    private static boolean isUnreserved(char c) {
        return (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z') ||
                (c >= '0' && c <= '9') ||
                c == '-' || c == '.' || c == '_' || c == '~';
    }

    private static String encodeQueryComponent(String s) {
        // For query, application/x-www-form-urlencoded is acceptable, but we'll keep + for space.
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
