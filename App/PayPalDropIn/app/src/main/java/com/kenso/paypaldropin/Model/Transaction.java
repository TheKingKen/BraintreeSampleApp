package com.kenso.paypaldropin.Model;

public class Transaction {
    private String id, status, amount, merchantAccountId;

    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }

    public String getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setMerchantAccountId(String merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }
}
