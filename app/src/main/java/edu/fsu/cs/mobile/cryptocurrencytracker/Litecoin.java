package edu.fsu.cs.mobile.cryptocurrencytracker;

import android.content.Context;

public class Litecoin extends Cryptocurrency {

    public Litecoin(Context context) {
        super(context);

        this.name = "Litecoin";
        this.ticker = "LTC";
        fetchAll();

    }

    public static final String wikiURL = "https://en.wikipedia.org/wiki/Litecoin";
}
