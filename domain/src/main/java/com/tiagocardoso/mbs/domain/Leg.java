package com.tiagocardoso.mbs.domain;

public class Leg {

    private int legNumber;
    private Selection selection;

    public Leg() {};

    public Leg(int legNumber, Selection selection) {
        this.legNumber = legNumber;
        this.selection = selection;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public Selection getSelection() {
        return selection;
    }

}
