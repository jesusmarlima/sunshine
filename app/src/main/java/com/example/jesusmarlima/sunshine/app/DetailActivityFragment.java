package com.example.jesusmarlima.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String FORECAST_SHARE_HASH_TAG = "#SUNSHINEAPP";
    private String forecast;

    public DetailActivityFragment() {

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent!= null && intent.hasExtra(Intent.EXTRA_TEXT)){
            forecast = intent.getExtras().getString(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.forecast_text)).setText(forecast);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = new ShareActionProvider(getActivity());
        mShareActionProvider.setShareIntent(createShareForecastIntent());
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + FORECAST_SHARE_HASH_TAG);
        return shareIntent;
    }

}
