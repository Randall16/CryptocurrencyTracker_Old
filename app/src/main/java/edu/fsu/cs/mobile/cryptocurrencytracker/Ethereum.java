package edu.fsu.cs.mobile.cryptocurrencytracker;

import android.content.Context;
import android.os.Handler;


public class Ethereum extends Cryptocurrency {

    public Ethereum(Context context) {
        super(context);

        this.name = "Ethereum";
        this.ticker = "ETH";
        fetchAll();
    }

    public static final String wikiURL = "https://en.wikipedia.org/wiki/Ethereum";
}
