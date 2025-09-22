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


/**
 * Represents the creation of a customer entity with associated attributes such as
 * address, description, email, name, phone, and shipping details.
 * <p>
 * This record is designed to be used in systems handling customer information,
 * particularly in the context of JSON serialization and deserialization
 * using the Jackson library.
 * <p>
 * The fields in this record include:
 * - `address`: An {@link Address} object representing the customer's address.
 * - `description`: A brief description or additional information about the customer.
 * - `email`: The email address of the customer.
 * - `name`: The name or identifier of the customer.
 * - `phone`: The phone number associated with the customer.
 * - `shipping`: A {@link Shipping} object specifying the shipping details for the customer.
 * <p>
 * This record adheres to immutability principles and ensures compatibility with
 * JSON handling through annotations:
 * - {@link JsonInclude.Include.NON_NULL}: Ensures that only non-null fields are included in the serialized JSON.
 * - {@link JsonIgnoreProperties}: Ignores unknown properties during deserialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateCustomer(
        @JsonProperty("address") Address address,
        @JsonProperty("description") String description,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("phone") String phone,
        @JsonProperty("shipping") Shipping shipping
) {
}
