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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class OnvoPayAPIClientTest {


    @Test
    public void testConstructor_WithInvalidBaseUrl() {

        assertThrows(IllegalArgumentException.class, () -> new OnvoPayAPIClient("", "secretKey"));

        assertThrows(IllegalArgumentException.class, () -> new OnvoPayAPIClient(null, "secretKey"));

        assertThrows(IllegalArgumentException.class, () -> new OnvoPayAPIClient("not-a-uri", "secretKey"));
    }

    @Test
    public void testConstructor_WithInvalidSecretKey() {

        assertThrows(IllegalArgumentException.class, () -> new OnvoPayAPIClient("https://example.com", ""));

        assertThrows(IllegalArgumentException.class, () -> new OnvoPayAPIClient("https://example.com", null));

        assertThrows(IllegalArgumentException.class, () -> new OnvoPayAPIClient("https://example.com", "not_prefix"));
    }

    @Test
    public void testConstructor_Valid() {
        new OnvoPayAPIClient("onvo_live_OK");
        new OnvoPayAPIClient("onvo_test_OK");
        new OnvoPayAPIClient("https://api.onvopay.com/v1", "onvo_live_OK");
        new OnvoPayAPIClient("https://api.onvopay.com/v1", "onvo_test_OK");
    }
}
