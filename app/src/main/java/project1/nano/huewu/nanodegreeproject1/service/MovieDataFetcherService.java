package project1.nano.huewu.nanodegreeproject1.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import project1.nano.huewu.nanodegreeproject1.MovieData;
import project1.nano.huewu.nanodegreeproject1.R;
import project1.nano.huewu.nanodegreeproject1.data.MockMovieDataStorage;

/**
 * Created by chansuk on 2015. 7. 23..
 */
public final class MovieDataFetcherService extends IntentService {

    private static final String TAG = "MovieDataFetcher";
    private static final int MAX_PAGE = 5;

    private final OkHttpClient mOkHttpClient;
    private static double sLastUpdateTimeStamp = 0;

    private String API_KEY;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MovieDataFetcherService() {
        super(TAG);
        mOkHttpClient = new OkHttpClient();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        API_KEY = getApplicationContext().getString(R.string.api_key);

        if ("YOUR_API_KEY".equals(API_KEY)) {
            Log.e(TAG, "set your API key as 'api_key' string resource in strings.xml");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (shouldRequestMovieList()) {
            sLastUpdateTimeStamp = System.currentTimeMillis();
            requestPopularMovieList(1);
            requestHighRatingMovieList(1);
        }
    }

    private boolean shouldRequestMovieList() {
        //TODO add some logic and options.

        //if wi-fi is connected and power is plugged.
        //if update was happen a while ago
        final double updateMargin
                = System.currentTimeMillis() - sLastUpdateTimeStamp;

        return (updateMargin > calculateMinUpdateMaring());
    }

    private double calculateMinUpdateMaring() {
        return 1000 * 60 * 5; //default 5 min
    }

    private void requestHighRatingMovieList(final int pageNo) {
        requestMovieListInternal(pageNo, "vote_average",
                MockMovieDataStorage.getHighRatingsInstance(this));
    }

    private void requestPopularMovieList(final int pageNo) {
        requestMovieListInternal(pageNo, "popularity",
                MockMovieDataStorage.getPopularInstance(this));
    }

    private void requestMovieListInternal(final int pageNo, final String sort,
                                          final MockMovieDataStorage storage) {

        new FetchRequest(pageNo, sort, storage).request();
    }

    class FetchRequest implements Callback {

        private MockMovieDataStorage mStorage;
        private int mPageNo;
        private String mSort;

        FetchRequest(int pageNo, String sort, MockMovieDataStorage storage) {
            mStorage = storage;
            mPageNo = pageNo;
            mSort = sort;
        }

        @Override
        public void onFailure(Request request, IOException e) {
            sLastUpdateTimeStamp = 0;
        }

        @Override
        public void onResponse(Response response) throws IOException {
            //TODO parse json data from server.
            sLastUpdateTimeStamp = System.currentTimeMillis();

            final String jsonStr = response.body().string();
            Log.d(TAG, "result: " + jsonStr);
            Gson gson = new Gson();
            TMDBResponse result = gson.fromJson(jsonStr, TMDBResponse.class);

            mStorage.beginTransaction();
            int addedCount = mStorage.copyorUpdate(result.results);
            mStorage.endTransaction();

            if (addedCount > 0 && result.page < MAX_PAGE) {
                mPageNo++;
                request();
            }
        }

        public void request() {
            final Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/discover/" +
                            "movie?sort_by=" + mSort + ".desc" +
                            "&api_key=" + API_KEY +
                            "&page=" + mPageNo).build();

            mOkHttpClient.newCall(request).enqueue(this);
        }
    }

    static class TMDBResponse {
        private final int page;
        private final MovieData[] results;

        public TMDBResponse(int page, MovieData[] results) {
            this.page = page;
            this.results = results;
        }
    }
}
