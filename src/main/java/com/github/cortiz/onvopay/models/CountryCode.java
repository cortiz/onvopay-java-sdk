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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a country code compliant with the ISO 3166-1 alpha-2 standard.
 * This class validates the provided country code value during instantiation
 * and ensures it adheres to the required format.
 * <br/>
 * The input value is standardized to uppercase and verified against
 * known ISO 3166-1 alpha-2 country codes as defined by the {@link Locale#getISOCountries()} method.
 * <br/>
 * Immutable and JSON-serializable, the class can be used as a lightweight
 * representation of a country's ISO code.
 */
public record CountryCode(String value) {

    private static final Set<String> ISO_ALPHA2 =
            Arrays.stream(Locale.getISOCountries()).collect(Collectors.toUnmodifiableSet());

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public CountryCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Country code cannot be null or blank");
        }
        String normalized = value.toUpperCase(Locale.ROOT);
        if (normalized.length() != 2 || !ISO_ALPHA2.contains(normalized)) {
            throw new IllegalArgumentException("Invalid ISO 3166-1 alpha-2 country code: " + value);
        }
        value = normalized;
    }

    @JsonValue
    public String toJson() {
        return value;
    }
}
