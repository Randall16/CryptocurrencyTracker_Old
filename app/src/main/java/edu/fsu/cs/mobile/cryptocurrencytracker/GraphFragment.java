package edu.fsu.cs.mobile.cryptocurrencytracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class GraphFragment extends Fragment {

    private GraphView graph;
    private LineGraphSeries<DataPoint> series7D, series30D, series365D;
    private Cryptocurrency localCrypto;
    private RadioButton _7DRadioButton, _30DRadioButton, _365DRadioButton;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graph = view.findViewById(R.id.graph);
        _7DRadioButton = view.findViewById(R.id.rb_7d);
        _30DRadioButton = view.findViewById(R.id.rb_30D);
        _365DRadioButton = view.findViewById(R.id.rb_1Y);

        graph.getGridLabelRenderer().setVerticalAxisTitle("Price");


        _7DRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graph.removeAllSeries();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(8);
                graph.addSeries(series7D);
            }
        });

        _30DRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graph.removeAllSeries();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(31);
                graph.addSeries(series30D);
            }
        });

        _365DRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graph.removeAllSeries();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(370);
                graph.addSeries(series365D);
            }
        });


        return view;
    }

    public void refresh() {
        graph.removeAllSeries();
        localCrypto = ((MainActivity)getActivity()).selectedCrypto;
        initSeries();

        graph.setTitle(localCrypto.getName() + " Graph");
        _365DRadioButton.setChecked(true);

        graph.removeAllSeries();
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(370);
        graph.addSeries(series365D);

    }

    private void initSeries() {
        series7D = new LineGraphSeries<>();
        series30D = new LineGraphSeries<>();
        series365D = new LineGraphSeries<>();
        int counter1 = 0, counter2 = 0;

        for(int i = 0; i < 366; i++) {
            double y = localCrypto.getPastPrice(365 - i);

            series365D.appendData(new DataPoint(i,y), true, 366);

            if(i >= 366-30)
                series30D.appendData(new DataPoint(counter1++,y), true, 31);

            if( i >= 366-7)
                series7D.appendData(new DataPoint(counter2++,y), true, 8);
        }

        series365D.appendData(new DataPoint(366, localCrypto.getCurrentPrice()), true , 366);
        series30D.appendData(new DataPoint(31, localCrypto.getCurrentPrice()), true , 31);
        series7D.appendData(new DataPoint(8, localCrypto.getCurrentPrice()), true , 8);
    }

    public void invalidate() {
        graph.setTitle("Graph");
        graph.removeAllSeries();
    }

}
