package edu.fsu.cs.mobile.cryptocurrencytracker;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class DomesticCurrencyDialog extends DialogFragment {

    private String abriv;
    private static final String [] choices = {"U.S. Dollars", "Euros", "Brittish Pounds",
            "Canadian Dollar", "Chinese Yuan"};



    public DomesticCurrencyDialog() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Domesitc Currency");
        builder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0)
                    abriv = "USD";
                else if(i == 1)
                    abriv = "EUR";
                else if(i == 2)
                    abriv = "GBP";
                else if(i == 3)
                    abriv = "CAD";
                else if(i == 4)
                    abriv = "CNY";

                SharedPreferences sp = getActivity().getSharedPreferences("userPrefs", 0);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("domestic ticker", abriv);
                ed.commit();

                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return builder.show();

    }
}
