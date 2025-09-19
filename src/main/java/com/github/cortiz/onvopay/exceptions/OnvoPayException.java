package com.github.cortiz.onvopay.exceptions;

import java.util.List;

public class OnvoPayException extends Exception {
    
    private final int statusCode;
    private final String apiCode;
    private final List<String> messages;
    private final String error;

    public OnvoPayException(int statusCode, String apiCode, List<String> messages, String error) {
        super(formatMessage(statusCode, apiCode, messages, error));
        this.statusCode = statusCode;
        this.apiCode = apiCode;
        this.messages = messages;
        this.error = error;
    }

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
