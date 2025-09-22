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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A custom deserializer that converts JSON input into a {@code List<String>}.
 * This deserializer supports the following JSON structures:
 * <p>
 * - Single string value: Converts the string into a singleton list containing
 * the string.
 * - Array of values: Converts each element of the array into a string and
 * aggregates them into a list.
 * - Null values: Returns an empty list.
 * - Other JSON types: Attempts to parse the value as an object, converts it
 * to a string representation, and encapsulates it into a singleton list.
 * <p>
 * This deserializer can handle scenarios where JSON input can vary in type
 * but needs to be normalized into a list of strings.
 */
public class MessageToListDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            String text = p.getText();
            return text == null ? Collections.emptyList() : Collections.singletonList(text);
        }
        if (t == JsonToken.START_ARRAY) {
            List<String> out = new ArrayList<>();
            while (p.nextToken() != JsonToken.END_ARRAY) {
                if (p.currentToken() == JsonToken.VALUE_STRING) {
                    out.add(p.getText());
                } else {
                    // Convert non-string items to their string representation
                    out.add(p.readValueAs(Object.class) + "");
                }
            }
            return out;
        }
        if (t == JsonToken.VALUE_NULL) {
            return Collections.emptyList();
        }
        // Fallback: try to read as generic object and stringify
        Object any = p.readValueAs(Object.class);
        return any == null ? Collections.emptyList() : Collections.singletonList(String.valueOf(any));
    }
}
