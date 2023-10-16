package com.altynbekova.aikamtask.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "message"})
public class ErrorOutput {
    private static final String TYPE = "error";
    private String message;

    public ErrorOutput(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return TYPE;
    }
}