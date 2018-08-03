package edu.fsu.cs.mobile.cryptocurrencytracker;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreLoadDialog extends DialogFragment {

    private static final String choices[] = {"Bitcoin (BTC)", "Ethereum (ETH)", "Litecoin (LTC)"};
    private int selection;

    public PreLoadDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose a Preload Cryptocurrency");
        builder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selection = i;
                SharedPreferences sp = getActivity().getSharedPreferences("userPrefs", 0);
                SharedPreferences.Editor ed = sp.edit();
                ed.putInt("preload", selection);
                ed.commit();
                Toast.makeText(getActivity(), "Preload set", Toast.LENGTH_SHORT).show();

                getDialog().dismiss();
            }
        });

        return builder.show();

    }
}
