package com.tiagocardoso.mbs.domain;

public class OperationResult {

    public enum Result {
        FAILURE,
        SUCCESS
    }

    private Result result;

    private String message;

    public OperationResult() {

    }

    public OperationResult(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Result getResult() {
        return result;
    }
}
