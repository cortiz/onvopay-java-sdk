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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.cortiz.onvopay.utils.MessageToListDeserializer;

import java.util.List;

/**
 * Represents an error response returned from an API call, containing details
 * about the error such as status code, error type, and messages. This record
 * is specifically designed for JSON serialization and deserialization using
 * the Jackson library.
 * <p>
 * The class includes the following fields:
 * - `statusCode`: The HTTP status code associated with the error response.
 * - `error`: A brief description of the error type, such as "Bad Request" or "Internal Server Error".
 * - `messages`: A list of detailed error messages providing additional context about the issue. This field
 * is deserialized using a custom deserializer (`MessageToListDeserializer`) to handle varied JSON formats.
 * <p>
 * Serialization:
 * - Fields with `null` values will be excluded from the JSON output, as governed by the {@link JsonInclude.Include.NON_NULL} annotation.
 * <p>
 * Deserialization:
 * - Unknown properties in the JSON payload are ignored during deserialization due to the
 * {@link JsonIgnoreProperties(ignoreUnknown = true)} annotation.
 * <p>
 * The `messages` field supports inputs in multiple formats, such as a single string, an array of strings,
 * or other types, and normalizes these into a list of strings for consistent usage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OnvoErrorResponse(
        @JsonProperty("statusCode") int statusCode,
        @JsonProperty("error") String error,
        @JsonProperty("message")
        @JsonDeserialize(using = MessageToListDeserializer.class)
        List<String> messages
) {
}
