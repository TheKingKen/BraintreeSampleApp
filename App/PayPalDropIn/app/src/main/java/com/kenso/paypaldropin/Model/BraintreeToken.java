package com.kenso.paypaldropin.Model;

public class BraintreeToken {
    private String clientToken;
    private boolean success;

    public BraintreeToken() {

    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getClientToken() {
        return clientToken;
    }

    public boolean isSuccess() {
        return success;
    }
}
