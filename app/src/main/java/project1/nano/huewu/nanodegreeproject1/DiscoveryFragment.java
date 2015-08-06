package project1.nano.huewu.nanodegreeproject1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import project1.nano.huewu.nanodegreeproject1.data.MockMovieDataStorage;
import project1.nano.huewu.nanodegreeproject1.events.MovieClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryFragment extends Fragment implements
        MockMovieDataStorage.MoiveDataChangeListener {

    private static final String TAG = "DiscoveryFragment";
    private static final int TYPE_POPULAR = 1001;
    private static final int TYPE_HIGHEST_RATINGS = 1002;
    private static final int NO_TYPE = -1;

    private int mSortType = NO_TYPE;

    public DiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated");
        setHasOptionsMenu(true);

        if (view != null) {

            //set data change listener.
            getDataStorage(getActivity()).addChangeListener(this);

            final RecyclerView recyclerView
                    = (RecyclerView) view.findViewById(R.id.grid_movies);
            final int numberOfcols = measureNumberOfColumns();
            recyclerView.setHasFixedSize(true);

            if (recyclerView.getAdapter() == null) {
                //set Empty View.
                initMovieAdpaterOrEmptyView();
            } else {
                final MovieAdapter adapter = (MovieAdapter) recyclerView.getAdapter();
                adapter.setNumberOfColumns(numberOfcols);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_by_popularity:
                intMovieDataStorage(TYPE_POPULAR);
                return true;
            case R.id.sort_by_ratings:
                intMovieDataStorage(TYPE_HIGHEST_RATINGS);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChange() {

        final RecyclerView recyclerView = getRecyclerView();
        if (recyclerView== null) {
            //View is either not ready or destroyed.
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MovieAdapter adapter = (MovieAdapter) recyclerView.getAdapter();
                //TODO if there is no video, show empty screen.
                if (adapter == null) {
                    initMovieAdpaterOrEmptyView();
                } else {
                    updateMovieAdapter(adapter);
                }
            }
        });
    }

    private void showEmptyView() {
        Log.d(TAG, "Show EmptyView");
        getView().findViewById(R.id.empty_list).setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        Log.d(TAG, "Hide EmptyView");
        getView().findViewById(R.id.empty_list).setVisibility(View.GONE);
    }

    private RecyclerView getRecyclerView() {
        final View view = getView();
        if (view == null) {
            return null;
        } else {
            return (RecyclerView) view.findViewById(R.id.grid_movies);
        }
    }

    private void initMovieAdpaterOrEmptyView() {

        final RecyclerView recyclerView = getRecyclerView();

        if (recyclerView != null) {
            Log.d(TAG, "init Movie Adapter");

            final MovieData[] list = getDataStorage(getActivity()).getMovies();

            if (list.length > 0) {
                final MovieAdapter adapter = new MovieAdapter(list);
                recyclerView.setLayoutManager(
                        new GridLayoutManager(getActivity(), measureNumberOfColumns()));

                adapter.setMovieClickListener((MovieClickListener) getActivity());
                adapter.setNumberOfColumns(measureNumberOfColumns());
                recyclerView.setAdapter(adapter);
                hideEmptyView();
            } else {
                showEmptyView();
            }
        }
    }

    private void updateMovieAdapter(final MovieAdapter adapter) {

        Log.d(TAG, "update Movie Adapter");

        final MovieData[] list
                = MockMovieDataStorage.getPopularInstance(getActivity()).getMovies();
        final int currentMovieNumber = adapter.getItemCount();

        if (currentMovieNumber < list.length) {
            adapter.addMovies(Arrays.copyOfRange(list, currentMovieNumber, list.length));
        }
    }

    private int measureNumberOfColumns() {
        Point size = new Point();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);
        //TODO get min requirement width for single column in dp unit.
        return size.x / 500;
    }

    private void intMovieDataStorage(final int type) {

        final Context context = getActivity();
        if (context == null) {
            Log.e(TAG, "Activity is not set yet");
        } else if (mSortType != type) {
            getDataStorage(getActivity()).removeChangeListener(this);
            mSortType = type;
            getDataStorage(getActivity()).addChangeListener(this);
            initMovieAdpaterOrEmptyView();
            saveSortPreference();
        }
    }

    private void saveSortPreference() {
        SharedPreferences preference
                = getActivity().getSharedPreferences("sort_preference", 0);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt("sort", mSortType);
        editor.apply();
    }

    private MockMovieDataStorage getDataStorage(final Context context) {

        switch (mSortType) {
            case TYPE_POPULAR:
                return MockMovieDataStorage.getPopularInstance(context);
            case TYPE_HIGHEST_RATINGS:
                return MockMovieDataStorage.getHighRatingsInstance(context);
            default:    //no type
                mSortType = context.getSharedPreferences("sort_preference",0)
                        .getInt("sort", TYPE_POPULAR);
                return getDataStorage(context);
        }
    }
}
