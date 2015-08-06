package project1.nano.huewu.nanodegreeproject1.data;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;

import java.util.ArrayList;

import project1.nano.huewu.nanodegreeproject1.DiscoveryFragment;
import project1.nano.huewu.nanodegreeproject1.MovieData;

/**
 * Created by chansuk on 15. 7. 31..
 */
public final class MockMovieDataStorage {

    private static MockMovieDataStorage sPopularInstance;
    private static MockMovieDataStorage sHighRatingsInstance;

    private final ArrayList<MovieData> mMovieTable;
    private final SparseArray<MovieData> mMovieIndex;
    private MoiveDataChangeListener mListener;
    private Handler mHandler;

    public final static synchronized MockMovieDataStorage getPopularInstance(Context context) {

        if (sPopularInstance == null) {
            sPopularInstance = new MockMovieDataStorage(context.getApplicationContext());
        }

        return sPopularInstance;
    }

    public final static synchronized MockMovieDataStorage getHighRatingsInstance(Context context) {

        if (sHighRatingsInstance == null) {
            sHighRatingsInstance = new MockMovieDataStorage(context.getApplicationContext());
        }

        return sHighRatingsInstance;
    }

    public synchronized int copyorUpdate(MovieData[] movies) {

        int result = 0;

        if (movies != null) {

            for (MovieData m : movies) {
                final MovieData existingMovie = mMovieIndex.get(m.id);
                if (existingMovie == null) {
                    mMovieIndex.put(m.id, m);
                    mMovieTable.add(m);
                    ++result;
                }
            }
        }

        if (mListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onChange();
                }
            });
        }

        return result;
    }

    public MovieData[] getMovies() {
        final int size = mMovieTable.size();
        final MovieData[] list = new MovieData[size];
        return mMovieTable.toArray(list);
    }

    public void addChangeListener(MoiveDataChangeListener listener) {
        //TODO manage a list of listeners
        mListener = listener;
    }

    public void removeChangeListener(MoiveDataChangeListener listener) {
        //TODO manage a list of listeners
        mListener = null;
    }

    public void beginTransaction() {}

    public void endTransaction() {}

    public boolean isEmpty() {
        return mMovieTable.size() == 0;
    }


    public interface MoiveDataChangeListener {
        void onChange();
    }

    private MockMovieDataStorage(Context applicationContext) {
        mMovieTable = new ArrayList<>(30);
        mMovieIndex = new SparseArray<>(30);
        mHandler = new Handler(applicationContext.getMainLooper());
    }
}
