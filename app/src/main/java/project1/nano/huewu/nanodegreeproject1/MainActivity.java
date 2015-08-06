package project1.nano.huewu.nanodegreeproject1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import project1.nano.huewu.nanodegreeproject1.data.MockMovieDataStorage;
import project1.nano.huewu.nanodegreeproject1.events.MovieClickListener;
import project1.nano.huewu.nanodegreeproject1.service.MovieDataFetcherService;

public class MainActivity extends AppCompatActivity
        implements MovieClickListener, FragmentManager.OnBackStackChangedListener {

    private NewtorkStateChangeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mReceiver = new NewtorkStateChangeReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MockMovieDataStorage.getPopularInstance(this).isEmpty()) {
            requestFetchPopularMovieData();
        }

        final IntentFilter filter= new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onMovieClick(MovieData movie) {
        Intent intent = DetailActivity.getLaunchIntent(this, movie);
        startActivity(intent);
    }

    @Override
    public void onBackStackChanged() {

    }

    public void requestFetchPopularMovieData() {
        Intent intent = new Intent(this, MovieDataFetcherService.class);
        intent.putExtra("sort", "popularity");
        this.startService(intent);
    }

    public void requestFetchHighRatingMovieData() {
        Intent intent = new Intent(this, MovieDataFetcherService.class);
        intent.putExtra("sort", "vote_average");
        this.startService(intent);
    }

    private final class NewtorkStateChangeReceiver extends BroadcastReceiver {

        private Snackbar mSnackbar;

        public NewtorkStateChangeReceiver(){
            mSnackbar = Snackbar
                    .make(findViewById(R.id.fragment), R.string.no_network, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestFetchPopularMovieData();
                        }
                    });
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork == null || !activeNetwork.isAvailable()) {
                mSnackbar.show();
            } else if (activeNetwork.isConnectedOrConnecting()) {
                requestFetchPopularMovieData();
                mSnackbar.dismiss();
            }
        }
    }
}
