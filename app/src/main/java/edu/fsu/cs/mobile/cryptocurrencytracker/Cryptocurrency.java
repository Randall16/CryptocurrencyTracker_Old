package edu.fsu.cs.mobile.cryptocurrencytracker;

/*
*   This class is the heart of the application. It lays the blueprint for all other
*   cryptocurrencies to be derived from. It contains methods that connect to and parse the
*   alphavantage API.
*
*   In total each Cryptocurrency object will make three calls to Alphavantage API.
*   The fetch methods retrieves a JSONObject containing the various prices. After the fetch is
*   complete we then parse the JSONObject to extract the specific prices that we are interested in.
*
*/

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Cryptocurrency {

    protected String name, ticker, domesticCurrency;
    protected double currentPrice, dayChange, weekChange, monthChange, yearChange;
    protected double pastPrices[];


    private JsonObjectRequest jsonReqCurrentPrice, jsonReqYesterdayPrice, jsonReqPastPrices;
    private final RequestQueue volleyQueue;
    private Context context;
    private boolean isValid;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static int ApiKeyCounter = 0;


    private static final String APIKEY1 = "WAN2BVJP7NWGW0F4";
    private static final String APIKEY2 = "TLRSM7S9ERHVNCND";
    private static final String APIKEY3 = "F6HY4K5YMHNQ771W";
    private static final String APIKEY4 = "Y904EATGC02QB6XQ";
    private static final String APIKEY5 = "2WXZL3AZ9787A4MT";
    private static final String APIKEY6 = "0MOLB2YOX405TMXZ";
    private static final String APIKEY7 = "WUVL52YQB0CW2BME";
    private static final String APIKEY8 = "U1WD93DX1IJ52J2X";
    private static final String APIKEY9 = "F2PPPFYGDN9CTPGU";



    // constructor
    public Cryptocurrency(Context c) {
        pastPrices = new double[365];   // initializing array
        volleyQueue = Volley.newRequestQueue(c);  // initializing the volleyQueue
        context = c;
        isValid = false;

        domesticCurrency = c.getSharedPreferences("userPrefs", 0)
                .getString("domestic ticker", "USD");
    }

    protected void fetchCurrentPrice() {

        String url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_" +
                "currency=" + ticker + "&to_currency=" + domesticCurrency + "&apikey=" + getApiKey();

        Log.v("tester", url);

        jsonReqCurrentPrice = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseCurrentPriceFetch(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendBroadCast();
            }
        });

        volleyQueue.add(jsonReqCurrentPrice);
    }

    private void parseCurrentPriceFetch(JSONObject response) {

        if(!response.has("Realtime Currency Exchange Rate")) {
            isValid = false;
            sendBroadCast();
            return;
        }

        String temp = "0";

        try {
            temp = ( response.getJSONObject("Realtime Currency Exchange Rate") )
                    .getString("5. Exchange Rate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.currentPrice = Double.valueOf(temp);
    }

    protected void fetchYesterdayPrice() {

        String temp = getApiKey();
        //Log.v("tester", temp);


        String url = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_INTRADAY&symbol="
            + ticker + "&market=" + domesticCurrency + "&apikey=" + temp;

        Log.v("tester", url);

        jsonReqYesterdayPrice = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseYesterdayPriceFetch(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        volleyQueue.add(jsonReqYesterdayPrice);
    }

    private void parseYesterdayPriceFetch(JSONObject response) {

        if(!response.has("Time Series (Digital Currency Intraday)")){
            return;
        }

        // Local variable declarations
        String dateTime, dateKey, answer;
        String holder[];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        try {   // necessary try/catch for json exception
            // dateTime will hold the last time and date the API was updated
            dateTime = (response.getJSONObject("Meta Data")).getString("7. Last Refreshed");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // we just want the time of the last refresh so split string at space
        holder = dateTime.split(" ");
        // dateKey will be the date and time of the value that we are looking for. This is yesterday's
        // price so subtract one day from dateTime variable and then reapend the time.
        dateKey = dateFormat.format(calendar.getTime()) + ' ' + holder[1];
        dateKey = dateKey.replaceAll("/", "-"); // reformatting for alphavantage API

        try {
            //
            answer = ((response.getJSONObject("Time Series (Digital Currency Intraday)"))
                    .getJSONObject(dateKey)).getString("1a. price (" + domesticCurrency + ")");
        } catch (JSONException e) {
            e.printStackTrace();
            answer = "-1";
        }

        pastPrices[0] = Double.valueOf(answer); // converting to double
    }

    protected void fetchPastPrices() {

        String url = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" +
            ticker + "&market=" + domesticCurrency + "&apikey=" + getApiKey();


        jsonReqPastPrices = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parsePastPricesFetch(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                return;
            }
        });

        volleyQueue.add(jsonReqPastPrices);
    }



    //Function below will parse the closing price of the cryptocurrency for the past 365 days
    private void parsePastPricesFetch(JSONObject response) {

        // check for API failure
        if(!response.has("Time Series (Digital Currency Daily)")) {
            isValid = false;
            sendBroadCast();
            return;
        }

        // local variable declarations
        String dateKey;
        Calendar calendar = Calendar.getInstance();
        JSONObject allPrices = null;

        try {   // necessary try/catch
            // The Jsonobject we are interested in our nested withing Time Series...
            allPrices = response.getJSONObject("Time Series (Digital Currency Daily)");
        } catch (JSONException e) {
            sendBroadCast();
            e.printStackTrace();
        }

        // i in the for loop below will represent days. Each cycle through the loop we will subtract
        // from today's calendar date.
        for(int i = 1; i <= 365; i++) {

            String holder;
            calendar = Calendar.getInstance();  // reset calendar
            calendar.add(Calendar.DATE, -i);    // subtract days from current calendar date
            dateKey = dateFormat.format(calendar.getTime());
            dateKey = dateKey.replaceAll("/", "-"); // reformat for alphavantage API

            try { // necessary try/catch
                holder = (allPrices.getJSONObject(dateKey))
                        .getString("4a. close (" + domesticCurrency + ')');
            } catch (JSONException e) {
                e.printStackTrace();
                holder = "-1";
            }

            this.pastPrices[i-1] = Double.valueOf(holder);
        }

        isValid = true;
        calculatePercentChanges();
        sendBroadCast();
    }

    private void calculatePercentChanges() {
        // Using percent change formula to store cryptocurrencies change over week,
        // month and year time periods.
        dayChange = ( (currentPrice - pastPrices[0]) / pastPrices[0] ) * 100;
        weekChange = ( (currentPrice - pastPrices[6]) / pastPrices[6] ) * 100;
        monthChange = ( (currentPrice - pastPrices[29]) / pastPrices[29] ) * 100;
        yearChange = ( (currentPrice - pastPrices[364]) / pastPrices[364] ) * 100;
    }

    private void sendBroadCast() {
        Intent intent = new Intent("complete");
        intent.putExtra("isValid", isValid);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected void fetchAll() {

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchCurrentPrice();



            }
        }, 500);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchPastPrices();
            }
        }, 1000);
    }

    private void singleFetchandParse() {

        String url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_" +
                "currency=" + ticker + "&to_currency=" + domesticCurrency + "&apikey=" + getApiKey();

        jsonReqCurrentPrice = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(!response.has("Realtime Currency Exchange Rate")) {
                    isValid = false;
                    Intent intent = new Intent("singleFetch");
                    intent.putExtra("isValid", isValid);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    return;
                }

                String temp = "0";

                try {
                    temp = ( response.getJSONObject("Realtime Currency Exchange Rate") )
                            .getString("5. Exchange Rate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                currentPrice = Double.valueOf(temp);
                calculatePercentChanges();

                isValid = true;
                Intent intent = new Intent("singleFetch");
                intent.putExtra("isValid", isValid);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isValid = false;
                Intent intent = new Intent("singleFetch");
                intent.putExtra("isValid", isValid);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        volleyQueue.add(jsonReqCurrentPrice);
    }

    public void refreshCurrentPrice() {
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                singleFetchandParse();

            }
        }, 500);
    }

    private static String getApiKey() {

        if(++ApiKeyCounter > 9)
            ApiKeyCounter = 1;

        switch(ApiKeyCounter){
            case 1:
                return APIKEY1;
            case 2:
                return APIKEY2;
            case 3:
                return APIKEY3;
            case 4:
                return APIKEY4;
            case 5:
                return APIKEY5;
            case 6:
                return APIKEY6;
            case 7:
                return APIKEY7;
            case 8:
                return APIKEY8;
            default:
                return APIKEY9;
        }
    }

    // GET FUNCTIONS
    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getDomesticCurrency() {
        return domesticCurrency;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getDayChange() {
        return dayChange;
    }

    public double getWeekChange() {
        return weekChange;
    }

    public double getMonthChange() {
        return monthChange;
    }

    public double getYearChange() {
        return yearChange;
    }

    public double getPastPrice(int daysAgo) {

        if(daysAgo < 0 || daysAgo > 365) // only storing one years worth of past prices
            return -1;
        else if (daysAgo == 0)
            return currentPrice;
        else
            return pastPrices[daysAgo-1];
    }
}
