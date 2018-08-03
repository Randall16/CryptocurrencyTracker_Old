package edu.fsu.cs.mobile.cryptocurrencytracker;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class Bitcoin extends Cryptocurrency {

    public Bitcoin(Context context) {
        super(context);

        this.name = "Bitcoin";
        this.ticker = "BTC";
        //this.fetchCurrentPrice();
        //this.fetchPastPrices();

        fetchAll();

    }



    public static final String wikiURL = "https://en.wikipedia.org/wiki/Bitcoin";
}
