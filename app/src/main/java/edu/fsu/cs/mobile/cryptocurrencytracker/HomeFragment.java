package edu.fsu.cs.mobile.cryptocurrencytracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView oneCryptoTextView, oneDayEditText, sevenDayEditText, thirtyDayEditText, yearEditText;
    private ImageView logo;

    private Cryptocurrency localCryptocurrency;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get handles to widgets
        oneCryptoTextView = view.findViewById(R.id.tv_oneCryptocurrency);
        oneDayEditText = view.findViewById(R.id.tv_24H);
        sevenDayEditText = view.findViewById(R.id.tv_7D);
        thirtyDayEditText = view.findViewById(R.id.tv_30D);
        yearEditText = view.findViewById(R.id.tv_1Y);
        logo = view.findViewById(R.id.iv_cryptoIcon);



        return view;
    }

    public void refresh() {
        localCryptocurrency = ( (MainActivity) getActivity() ).selectedCrypto;
        oneCryptoTextView.setText("One " + localCryptocurrency.getName() + " = " + getSymb() +
                String.format("%,.2f", localCryptocurrency.getCurrentPrice()));

        setTextView(oneDayEditText, "24H", localCryptocurrency.getDayChange());
        setTextView(sevenDayEditText, "7D", localCryptocurrency.getWeekChange());
        setTextView(thirtyDayEditText, "30D", localCryptocurrency.getMonthChange());
        setTextView(yearEditText, "1Y", localCryptocurrency.getYearChange());

        if(localCryptocurrency.getTicker().equals("BTC"))
            logo.setImageResource(R.drawable.bitcoin_image);
        else if(localCryptocurrency.getTicker().equals("ETH"))
            logo.setImageResource(R.drawable.ethereum_image);
        else if(localCryptocurrency.getTicker().equals("LTC"))
            logo.setImageResource(R.drawable.litecoin_image);
    }

    public void invalidate() {
        oneCryptoTextView.setText("one = ");
        setTextView(oneDayEditText, "24H", 0);
        setTextView(sevenDayEditText, "7D", 0);
        setTextView(thirtyDayEditText, "30D", 0);
        setTextView(yearEditText, "1Y", 0);
    }

    private void setTextView(TextView tv, String interval, double pChange) {
        if(pChange > 0)
            tv.setTextColor(getResources().getColor(R.color.stockGreen));
        else if(pChange < 0)
            tv.setTextColor(getResources().getColor(R.color.stockRed));
        else
            tv.setTextColor(getResources().getColor(R.color.stockBlack));

        tv.setText(interval + ": " + String.format("%.2f", pChange) + "%");
        logo.setImageResource(0);
    }

    private String getSymb() {
        if(localCryptocurrency.getDomesticCurrency().equals("USD"))
            return "$";
        else if(localCryptocurrency.getDomesticCurrency().equals("GBP"))
            return "£";
        else if(localCryptocurrency.getDomesticCurrency().equals("EUR"))
            return "€";
        else if(localCryptocurrency.getDomesticCurrency().equals("CAD"))
            return "C$";
        else if(localCryptocurrency.getDomesticCurrency().equals("CNY"))
            return "¥";
        else
            return "$";
    }


}
