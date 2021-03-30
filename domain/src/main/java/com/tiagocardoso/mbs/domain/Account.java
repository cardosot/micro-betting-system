package com.tiagocardoso.mbs.domain;

public class Account {

    private long accountId = System.currentTimeMillis();
    private String username;
    private double balance;

    public Account() {

    }

    public long getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }
}
