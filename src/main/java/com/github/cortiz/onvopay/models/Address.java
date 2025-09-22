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

package com.github.cortiz.onvopay.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a postal address with associated details such as city, country,
 * street lines, postal code, and state. This class is designed for use in
 * systems requiring structured address representations, particularly in the context
 * of JSON serialization and deserialization using the Jackson library.
 * <p>
 * This record adheres to immutability principles and ensures non-null
 * serialization of included fields via the {@link JsonInclude.Include.NON_NULL} annotation.
 * Unknown properties during deserialization are ignored as specified by the
 * {@link JsonIgnoreProperties} annotation.
 * <p>
 * The fields within the address include:
 * - `city`: The name of the city.
 * - `country`: The country name or code associated with the address.
 * - `line1`: The first line of the street address, typically used for the primary address.
 * - `line2`: The second line of the street address, often used for apartment or suite numbers.
 * - `postalCode`: The postal or ZIP code associated with the address.
 * - `state`: The state, province, or region of the address.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Address(
        @JsonProperty("city") String city,
        @JsonProperty("country") String country,
        @JsonProperty("line1") String line1,
        @JsonProperty("line2") String line2,
        @JsonProperty("postalCode") String postalCode,
        @JsonProperty("state") String state
) {
}