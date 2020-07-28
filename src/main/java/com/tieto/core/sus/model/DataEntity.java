package com.tieto.core.sus.model;

import java.util.Objects;

public class DataEntity {
    private String msisdn;
    private String accountId;
    private String status;

    public DataEntity(String msisdn, String accountId, String status) {
        this.msisdn = msisdn;
        this.accountId = accountId;
        this.status = status;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataEntity that = (DataEntity) o;
        return Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(accountId, that.accountId) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msisdn, accountId, status);
    }

    @Override
    public String toString() {
        return "DataEntity{" +
                "msisdn='" + msisdn + '\'' +
                ", accountId='" + accountId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
