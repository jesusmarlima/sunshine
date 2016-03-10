package com.example.jesusmarlima.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jesusmarlima.sunshine.app.util.ForecastAPIConnect;
import com.example.jesusmarlima.sunshine.app.util.ForecastJSONUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayList<String> listaForeCast = null;
    View rootView;
    ListView forecastListView;
    ArrayAdapter<String> mForeCastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return doRefresh();
        } else if (id == R.id.action_settings){
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        } if( id == R.id.action_map){
            openPreferedLocationMap();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferedLocationMap(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager())!= null){
            startActivity(intent);
        } else {
            System.out.print("Error");
        }


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean doRefresh(){

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        String[] foreCastList = new String[0];
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String location = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
            String unitSystem = prefs.getString(getString(R.string.pref_unit_key),"metric");
            String[] settings = new String[]{location,unitSystem};
            foreCastList = fetchWeatherTask.execute(settings).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        listaForeCast = new ArrayList<String>();

        for (String forecast:foreCastList){
            listaForeCast.add(forecast);
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        try {
            if (listaForeCast == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String location = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
                String unitSystem = prefs.getString(getString(R.string.pref_unit_key),"metric");

                String[] foreCastList = fetchWeatherTask.execute(new String[]{location, unitSystem}).get();
                listaForeCast = new ArrayList<String>();

                for (String forecast:foreCastList){
                    listaForeCast.add(forecast);
                }
            }

            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mForeCastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,listaForeCast );
            forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
            forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent();
                    intent.putExtra(intent.EXTRA_TEXT,mForeCastAdapter.getItem(position));
                    intent.setClass(getActivity(),DetailActivity.class);
                    startActivity(intent);
                }
            });
            forecastListView.setAdapter(mForeCastAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        return rootView;




    }


    public class FetchWeatherTask extends AsyncTask<String , Void , String[]> {

        private final String  LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            ;
            if(strings != null){
                mForeCastAdapter.clear();
                for (String dayForecastStr: strings){
                    mForeCastAdapter.add(dayForecastStr);
                }
            }
        }

        @Override
        protected String[] doInBackground(String[] params) {

            ForecastAPIConnect connect = new ForecastAPIConnect();
            ForecastJSONUtil util = new  ForecastJSONUtil();

            try {
                return util.getWeatherDataFromJson (connect.readFromAPI(params),7);
            } catch (JSONException e) {
                Log.v(LOG_TAG, "FetchWeatherTask " + e);
                e.printStackTrace();
            }
            return  null;

        }
    }


}
