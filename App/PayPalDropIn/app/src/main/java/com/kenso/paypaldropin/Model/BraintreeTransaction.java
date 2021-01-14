package com.kenso.paypaldropin.Model;

public class BraintreeTransaction {
    private boolean success;
    private Transaction transaction;

    public BraintreeTransaction() {
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isSuccess() {
        return success;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
