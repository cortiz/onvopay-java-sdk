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

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlBuilderTest {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UrlBuilderTest.class);

    @Test
    void buildsWithBaseOnly() {
        LOG.info("UrlBuilderTest#buildsWithBaseOnly - start");
        String url = UrlBuilder.from("https://api.example.com").build();
        assertEquals("https://api.example.com", url);
    }

    @Test
    void appendsSinglePathSegment() {
        LOG.info("UrlBuilderTest#appendsSinglePathSegment - start");
        String url = UrlBuilder.from("https://api.example.com").addPathSegment("customers").build();
        assertEquals("https://api.example.com/customers", url);
    }

    @Test
    void appendsMultiplePathSegmentsAndNormalizesSlashes() {
        LOG.info("UrlBuilderTest#appendsMultiplePathSegmentsAndNormalizesSlashes - start");
        String url = UrlBuilder.from("https://api.example.com/")
                .addPathSegment("/v1/", "/customers/", "123 ")
                .build();
        // space must be %20 in path
        assertEquals("https://api.example.com/v1/customers/123%20", url);
    }

    @Test
    void preservesBasePath() {
        LOG.info("UrlBuilderTest#preservesBasePath - start");
        String url = UrlBuilder.from("https://api.example.com/base")
                .addPathSegment("v1", "customers")
                .build();
        assertEquals("https://api.example.com/base/v1/customers", url);
    }

    @Test
    void encodesPathSpecialCharacters() {
        LOG.info("UrlBuilderTest#encodesPathSpecialCharacters - start");
        String url = UrlBuilder.from("https://api.example.com")
                .addPathSegment("a b", "c+d", "x/y")
                .build();
        // space => %20, '+' encoded, '/' inside segment encoded
        assertEquals("https://api.example.com/a%20b/c%2Bd/x%2Fy", url);
    }

    @Test
    void addsQueryParamsAndEncodesValues() {
        LOG.info("UrlBuilderTest#addsQueryParamsAndEncodesValues - start");
        String url = UrlBuilder.from("https://api.example.com")
                .addPathSegment("search")
                .addQueryParam("q", "foo bar")
                .addQueryParam("lang", "en-US")
                .build();
        assertEquals("https://api.example.com/search?q=foo+bar&lang=en-US", url);
    }

    @Test
    void allowsRepeatedQueryKeysAndKeyOnly() {
        LOG.info("UrlBuilderTest#allowsRepeatedQueryKeysAndKeyOnly - start");
        String url = UrlBuilder.from("https://api.example.com")
                .addQueryParam("flag", null)
                .addQueryParam("id", "1")
                .addQueryParam("id", "2")
                .build();
        assertEquals("https://api.example.com?flag&id=1&id=2", url);
    }

    @Test
    void addQueryParamsFromMap() {
        LOG.info("UrlBuilderTest#addQueryParamsFromMap - start");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("a", "1");
        params.put("b", "2");
        String url = UrlBuilder.from("https://api.example.com")
                .addQueryParams(params)
                .build();
        assertEquals("https://api.example.com?a=1&b=2", url);
    }

    @Test
    void buildUriReturnsUriObject() {
        LOG.info("UrlBuilderTest#buildUriReturnsUriObject - start");
        URI uri = UrlBuilder.from("https://api.example.com")
                .addPathSegment("v1")
                .addQueryParam("x", "y")
                .buildUri();
        assertEquals("https", uri.getScheme());
        assertEquals("api.example.com", uri.getHost());
        assertEquals("/v1", uri.getPath());
        assertEquals("x=y", uri.getQuery());
    }

    @Test
    void invalidBaseUrlThrows() {
        LOG.info("UrlBuilderTest#invalidBaseUrlThrows - start");
        assertThrows(IllegalArgumentException.class, () -> UrlBuilder.from("not a url"));
    }
}
