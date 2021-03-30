package com.tiagocardoso.mbs.domain;

import java.util.List;

public class Bet {

    private long betId = System.currentTimeMillis();
    private String accountId;
    private List<Leg> legs;
    private BetType betType;

    public long getBetId() {
        return betId;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public BetType getBetType() {
        return betType;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return "betId: " + betId + "| accountId: " + accountId;
    }
}
