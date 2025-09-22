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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Represents a customer in the system with details such as contact information,
 * address, transaction history, and associated metadata.
 * <br/>
 * This class uses the Jackson library for JSON serialization and deserialization.
 * Fields are mapped to their corresponding JSON properties, making it suitable
 * for data exchange with APIs.
 * <br/>
 * The class is immutable and ensures non-null included fields via the
 * {@link JsonInclude.Include.NON_NULL} annotation. Unknown properties during
 * deserialization are ignored using the {@link JsonIgnoreProperties} annotation.
 * <br/>
 * Fields like timestamps (e.g., createdAt, lastTransactionAt, updatedAt) are formatted
 * in ISO 8601 format with UTC timezone for consistent handling of date and time across systems.
 * <br/>
 * Key Features:
 * - `id`: Unique identifier of the customer as a string.
 * - `address`: Physical address of the customer, represented by the {@link Address} record.
 * - `amountSpent`: Total amount spent by the customer, captured as a long value.
 * - `description`: A brief description or comments about the customer.
 * - `createdAt`: The timestamp indicating when the customer record was created.
 * - `email`: Email address for the customer, stored as a string.
 * - `lastTransactionAt`: Timestamp of the most recent transaction by the customer.
 * - `mode`: Operational mode or type associated with the customer.
 * - `name`: Full name of the customer as a string.
 * - `phone`: Customer's phone number as a string.
 * - `shipping`: Shipping details associated with the customer, represented by the {@link Shipping} record.
 * - `transactionsCount`: Total count of transactions made by the customer as an integer.
 * - `updatedAt`: Timestamp indicating the last update made to the customer record.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Customer(
        @JsonProperty("id") String id,
        @JsonProperty("address") Address address,
        @JsonProperty("amountSpent") Long amountSpent,
        @JsonProperty("description") String description,
        @JsonProperty("createdAt")
        Instant createdAt,
        @JsonProperty("email") String email,
        @JsonProperty("lastTransactionAt")
        Instant lastTransactionAt,
        @JsonProperty("mode") String mode,
        @JsonProperty("name") String name,
        @JsonProperty("phone") String phone,
        @JsonProperty("shipping") Shipping shipping,
        @JsonProperty("transactionsCount") Integer transactionsCount,
        @JsonProperty("updatedAt")
        Instant updatedAt
) {
}
