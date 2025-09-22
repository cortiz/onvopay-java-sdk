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

package com.github.cortiz.onvopay.exceptions;

import java.util.List;

/**
 * Represents a custom exception specific to the OnvoPay API.
 * This exception is designed to encapsulate detailed error information
 * including status code, API error code, messages, and general error description.
 */
public class OnvoPayException extends Exception {
    
    private final int statusCode;
    private final String apiCode;
    private final List<String> messages;
    private final String error;

    /**
     * Constructs a new OnvoPayException with the specified details about the error.
     * This exception is meant to encapsulate detailed error information such as
     * the HTTP status code, API-specific error code, error messages, and a general
     * error description.
     *
     * @param statusCode the HTTP status code associated with the error
     * @param apiCode    the specific error code returned by the OnvoPay API
     * @param messages   a list of detailed error messages related to the error
     * @param error      a general description of the error
     */
    public OnvoPayException(int statusCode, String apiCode, List<String> messages, String error) {
        super(formatMessage(statusCode, apiCode, messages, error));
        this.statusCode = statusCode;
        this.apiCode = apiCode;
        this.messages = messages;
        this.error = error;
    }

    /**
     * Constructs a new OnvoPayException with the specified error message.
     * The status code, API-specific error code, detailed error messages, and general
     * error description are set to default values (null or 0) with this constructor.
     *
     * @param message the detailed error message describing the exception
     */
    public OnvoPayException(String message) {
        super(message);
        this.statusCode = 0;
        this.apiCode = null;
        this.messages = null;
        this.error = null;
    }

    private static String formatMessage(int statusCode, String apiCode, List<String> messages, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("OnvoPay API Error - ");
        sb.append("Status: ").append(statusCode);
        if (apiCode != null) {
            sb.append(", Code: ").append(apiCode);
        }
        if (error != null) {
            sb.append(", Error: ").append(error);
        }
        if (messages != null && !messages.isEmpty()) {
            sb.append(", Messages: ").append(String.join("; ", messages));
        }
        return sb.toString();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getApiCode() {
        return apiCode;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getError() {
        return error;
    }

    public boolean hasApiError() {
        return apiCode != null;
    }
}
