package edu.fsu.cs.mobile.cryptocurrencytracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;



public class ConverterFragment extends Fragment {

    private Cryptocurrency localCrypto;

    private EditText cryptoEditText, domesticEditText;
    private TextView cryptoTextView, domesticTextView;


    public ConverterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        cryptoEditText = view.findViewById(R.id.et_crypto);
        domesticEditText = view.findViewById(R.id.et_domestic);
        cryptoTextView = view.findViewById(R.id.tv_crypto);
        domesticTextView = view.findViewById(R.id.tv_domestic);



        cryptoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(cryptoEditText.hasFocus() && !cryptoEditText.getText().toString().equals("") ) {

                    double temp = Double.valueOf(cryptoEditText.getText().toString()
                            .replaceAll(",",""));

                    temp *= localCrypto.getCurrentPrice();
                    domesticEditText.setText(String.format("%,.2f",temp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        domesticEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(domesticEditText.hasFocus() && !domesticEditText.getText().toString().equals("") ) {

                    double temp = Double.valueOf(domesticEditText.getText().toString()
                            .replaceAll(",",""));

                    temp /= localCrypto.getCurrentPrice();
                    cryptoEditText.setText(String.format("%,.4f",temp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return view;
    }

    public void refresh() {
        localCrypto = ( (MainActivity)getActivity() ).selectedCrypto;

        cryptoTextView.setText(localCrypto.getName());
        domesticTextView.setText(localCrypto.getDomesticCurrency());

        cryptoEditText.setHint("Enter " + localCrypto.getName());
        domesticEditText.setHint("Enter " + localCrypto.getDomesticCurrency());

        if(cryptoEditText.hasFocus())
            cryptoEditText.setText(cryptoEditText.getText().toString());
        else if(domesticEditText.hasFocus())
            domesticEditText.setText(domesticEditText.getText().toString());

    }

}
