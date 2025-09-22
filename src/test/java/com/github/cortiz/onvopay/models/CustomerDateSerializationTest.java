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

// Java
package com.github.cortiz.onvopay.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDateTest {

    @Test
    void deserializeDates_WithUtcZ_Succeeds() throws Exception {
        System.out.println("CustomerDateTest#deserializeDates_WithUtcZ_Succeeds - start");
        String json = """
                {
                  "createdAt": "2022-06-12T21:21:10.587Z",
                  "lastTransactionAt": null,
                  "updatedAt": "2022-06-12T21:21:10.587Z"
                }
                """;

        Customer customer = mapper().readValue(json, Customer.class);

        Instant expected = Instant.parse("2022-06-12T21:21:10.587Z");
        assertNotNull(customer);
        assertEquals(expected, customer.createdAt());
        assertNull(customer.lastTransactionAt(), "lastTransactionAt should be null when JSON has null");
        assertEquals(expected, customer.updatedAt());
    }

    private static ObjectMapper mapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void deserializeDates_WithOffset_NormalizesToUtcInstant() throws Exception {
        System.out.println("CustomerDateTest#deserializeDates_WithOffset_NormalizesToUtcInstant - start");
        String json = """
                {
                  "createdAt": "2022-06-12T16:21:10.587-05:00",
                  "updatedAt": "2022-06-12T23:21:10.587+02:00"
                }
                """;

        Customer customer = mapper().readValue(json, Customer.class);

        Instant expectedCreated = OffsetDateTime.parse("2022-06-12T16:21:10.587-05:00")
                .withOffsetSameInstant(ZoneOffset.UTC).toInstant(); // 21:21:10.587Z
        Instant expectedUpdated = OffsetDateTime.parse("2022-06-12T23:21:10.587+02:00")
                .withOffsetSameInstant(ZoneOffset.UTC).toInstant(); // 21:21:10.587Z

        assertEquals(expectedCreated, customer.createdAt());
        assertEquals(expectedUpdated, customer.updatedAt());
    }

    @Test
    void serializeDates_UseIso8601UtcWithMillis() throws Exception {
        System.out.println("CustomerDateTest#serializeDates_UseIso8601UtcWithMillis - start");
        Instant instant = Instant.parse("2022-06-12T21:21:10.587Z");

        Customer customer = new Customer(
                null,        // id
                null,        // address
                null,        // amountSpent
                null,        // description
                instant,     // createdAt
                null,        // email
                null,        // lastTransactionAt
                null,        // mode
                null,        // name
                null,        // phone
                null,        // shipping
                null,        // transactionsCount
                instant      // updatedAt
        );

        String json = mapper().writeValueAsString(customer);

        // Expect exact ISO-8601 with milliseconds and 'Z' (UTC)
        assertTrue(json.contains("\"createdAt\":\"2022-06-12T21:21:10.587Z\""),
                   "createdAt should be serialized as ISO-8601 UTC with milliseconds");
        assertTrue(json.contains("\"updatedAt\":\"2022-06-12T21:21:10.587Z\""),
                   "updatedAt should be serialized as ISO-8601 UTC with milliseconds");
        // Ensure no numeric timestamps
        assertFalse(json.matches(".*\"createdAt\"\\s*:\\s*\\d+.*"),
                    "Dates must not be serialized as numeric timestamps");
    }
}
