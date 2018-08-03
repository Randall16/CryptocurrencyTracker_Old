package edu.fsu.cs.mobile.cryptocurrencytracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private WebView wikiWebView;


    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        wikiWebView = view.findViewById(R.id.wv_wiki);
        wikiWebView.setWebViewClient(new WebViewClient());


        wikiWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });



        wikiWebView.loadUrl(Bitcoin.wikiURL);



        return view;
    }

    public void refresh(int selection) {

        if(selection == 0)
            wikiWebView.loadUrl(Bitcoin.wikiURL);
        else if(selection == 1)
            wikiWebView.loadUrl(Ethereum.wikiURL);
        else if(selection == 2)
            wikiWebView.loadUrl(Litecoin.wikiURL);

    }

}
