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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryCodeTest {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CountryCodeTest.class);

    /**
     * Tests for the `toJson` method in the `CountryCode` class.
     * The `toJson` method returns the string value of the country code.
     */

    @Test
    void toJson_ShouldReturnValidISOAlpha2Code_WhenCountryCodeIsValid() {
        LOG.info("CountryCodeTest#toJson_ShouldReturnValidISOAlpha2Code_WhenCountryCodeIsValid - start");
        String validCode = "US";
        CountryCode countryCode = new CountryCode(validCode);
        String json = countryCode.toJson();
        assertEquals("US", json, "The toJson method should return the valid ISO 3166-1 alpha-2 country code.");
    }

    @Test
    void toJson_ShouldReturnNormalizedCode_WhenCountryCodeIsLowerCase() {
        LOG.info("CountryCodeTest#toJson_ShouldReturnNormalizedCode_WhenCountryCodeIsLowerCase - start");
        String lowerCaseCode = "us";
        CountryCode countryCode = new CountryCode(lowerCaseCode);
        String json = countryCode.toJson();
        assertEquals("US", json, "The toJson method should return the normalized uppercase ISO 3166-1 alpha-2 country code.");
    }

    @Test
    void toJson_ShouldReturnNormalizedCode_WhenCountryCodeIsMixedCase() {
        LOG.info("CountryCodeTest#toJson_ShouldReturnNormalizedCode_WhenCountryCodeIsMixedCase - start");
        String mixedCaseCode = "uS";
        CountryCode countryCode = new CountryCode(mixedCaseCode);
        String json = countryCode.toJson();
        assertEquals("US", json, "The toJson method should return the normalized uppercase ISO 3166-1 alpha-2 country code.");
    }

    @Test
    void constructor_ShouldThrowException_WhenCountryCodeIsNull() {
        System.out.println("CountryCodeTest#constructor_ShouldThrowException_WhenCountryCodeIsNull - start");
        String invalidCode = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new CountryCode(invalidCode));
        assertEquals("Country code cannot be null or blank", exception.getMessage(), "The constructor should throw an exception for null input.");
    }

    @Test
    void constructor_ShouldThrowException_WhenCountryCodeIsBlank() {
        System.out.println("CountryCodeTest#constructor_ShouldThrowException_WhenCountryCodeIsBlank - start");
        String invalidCode = "  ";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new CountryCode(invalidCode));
        assertEquals("Country code cannot be null or blank", exception.getMessage(), "The constructor should throw an exception for blank input.");
    }

    @Test
    void constructor_ShouldThrowException_WhenCountryCodeIsInvalidLength() {
        System.out.println("CountryCodeTest#constructor_ShouldThrowException_WhenCountryCodeIsInvalidLength - start");
        String invalidCode = "USA";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new CountryCode(invalidCode));
        assertEquals("Invalid ISO 3166-1 alpha-2 country code: USA", exception.getMessage(), "The constructor should throw an exception for input with invalid length.");
    }

    @Test
    void constructor_ShouldThrowException_WhenCountryCodeIsNotISOAlpha2() {
        System.out.println("CountryCodeTest#constructor_ShouldThrowException_WhenCountryCodeIsNotISOAlpha2 - start");
        String invalidCode = "ZZ";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new CountryCode(invalidCode));
        assertEquals("Invalid ISO 3166-1 alpha-2 country code: ZZ", exception.getMessage(), "The constructor should throw an exception for non-ISO 3166-1 alpha-2 codes.");
    }
}