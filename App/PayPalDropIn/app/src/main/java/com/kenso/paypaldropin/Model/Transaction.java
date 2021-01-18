package com.kenso.paypaldropin.Model;

public class Transaction {
    private String id, status, amount, merchantAccountId, processorResponseType, processorResponseCode, processorResponseText;

    public String getProcessorResponseType() {
        return processorResponseType;
    }

    public void setProcessorResponseType(String processorResponseType) {
        this.processorResponseType = processorResponseType;
    }

    public String getProcessorResponseCode() {
        return processorResponseCode;
    }

    public void setProcessorResponseCode(String processorResponseCode) {
        this.processorResponseCode = processorResponseCode;
    }

    public String getProcessorResponseText() {
        return processorResponseText;
    }

    public void setProcessorResponseText(String processorResponseText) {
        this.processorResponseText = processorResponseText;
    }

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
