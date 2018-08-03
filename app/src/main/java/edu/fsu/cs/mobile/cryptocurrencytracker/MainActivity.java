package edu.fsu.cs.mobile.cryptocurrencytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Cryptocurrency selectedCrypto;
    private Cryptocurrency cryptoArray[];

    public Spinner cryptoSelectionSpinner;

    private HomeFragment homeFragment;
    private GraphFragment graphFragment;
    private ConverterFragment converterFragment;
    private InfoFragment infoFragment;

    private ProgressBar loading;
    private ImageButton updateButton;
    public Toolbar toolbar;
    public TabLayout tabLayout;
    public ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cryptoArray = new Cryptocurrency[]{null, null, null};
        selectedCrypto = null;
        homeFragment = new HomeFragment();
        graphFragment = new GraphFragment();
        converterFragment = new ConverterFragment();
        infoFragment = new InfoFragment();
        initSpinner();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loading = findViewById(R.id.progressBar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3); // hours of pain!!!

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        LocalBroadcastManager.getInstance(this).registerReceiver(cryptoReciever,
                new IntentFilter("complete"));

        LocalBroadcastManager.getInstance(this).registerReceiver(singleReciever,
                new IntentFilter("singleFetch"));

        updateButton = findViewById(R.id.ib_refresh);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                selectedCrypto.refreshCurrentPrice();
            }
        });

    }

    private void loadCryptocurrency(int selection) {
        if(selection == 0 && cryptoArray[selection] == null) {
            selectedCrypto = new Bitcoin(this);
        }
        else if(selection == 1 && cryptoArray[selection] == null) {
            selectedCrypto = new Ethereum(this);
        }
        else if(selection == 2 && cryptoArray[selection] == null) {
            selectedCrypto = new Litecoin(this);
        }
        else {
            selectedCrypto = cryptoArray[selection];
        }


        if(cryptoArray[selection] == null){
            cryptoArray[selection] = selectedCrypto;
        }
        else {
            refreshAllFragments();
            loading.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshAllFragments() {
        homeFragment.refresh();
        graphFragment.refresh();
        converterFragment.refresh();
    }

    private void invalidateAllFragments() {
        homeFragment.invalidate();
        graphFragment.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.opt1:
                PreLoadDialog p = new PreLoadDialog();
                p.show(getSupportFragmentManager(), "pl");
                return true;
            case R.id.opt2:
                DomesticCurrencyDialog d = new DomesticCurrencyDialog();
                d.show(getSupportFragmentManager(),"dd");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver cryptoReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!intent.getBooleanExtra("isValid", false)) {
                cryptoArray[cryptoSelectionSpinner.getSelectedItemPosition()] = null;
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                invalidateAllFragments();

            }
            else {
                refreshAllFragments();
            }

            loading.setVisibility(View.INVISIBLE);
        }
    };

    private BroadcastReceiver singleReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!intent.getBooleanExtra("isValid", false)) {
                cryptoArray[cryptoSelectionSpinner.getSelectedItemPosition()] = null;
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                invalidateAllFragments();
            }
            else {
                refreshAllFragments();
            }

            loading.setVisibility(View.INVISIBLE);

        }
    };



    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_graph);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_exchange);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_info);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(homeFragment, "Home");
        adapter.addFrag(graphFragment, "Graph");
        adapter.addFrag(converterFragment, "Calculator");
        adapter.addFrag(infoFragment, "Information");
        viewPager.setAdapter(adapter);
    }

    //FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }



        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }


    }

    private void initSpinner() {
        cryptoSelectionSpinner = findViewById(R.id.s_cryptoSelection);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.cryptocurrenciesArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cryptoSelectionSpinner.setAdapter(adapter);
        int select = getSharedPreferences("userPrefs", 0).getInt("preload", 0);
        cryptoSelectionSpinner.setSelection(select);

        cryptoSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loading.setVisibility(View.VISIBLE);
                loadCryptocurrency(i);
                infoFragment.refresh(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // Unregister broadcastReceiver
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cryptoReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(singleReciever);
        super.onDestroy();
    }
}
