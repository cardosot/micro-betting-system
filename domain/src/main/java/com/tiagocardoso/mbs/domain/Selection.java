package com.tiagocardoso.mbs.domain;

public class Selection {

    private String id;
    private String marketId;
    private String eventId;
    private double stake;

    public Selection() {};

    public Selection(String id, String marketId, String eventId, double stake) {
        this.id = id;
        this.marketId = marketId;
        this.eventId = eventId;
        this.stake = stake;
    }

    public String getId() {
        return id;
    }

    public String getMarketId() {
        return marketId;
    }

    public String getEventId() {
        return eventId;
    }

    public double getStake() {
        return stake;
    }
}
