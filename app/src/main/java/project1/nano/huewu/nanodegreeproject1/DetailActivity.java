package project1.nano.huewu.nanodegreeproject1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener {

    private static final String KEY_MOVIE = "KEY_MOVIE";
    private static final String TAG_FRAGMENT = "TAG_FRAGMENT";
    private MovieData mMovieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMovieData = getIntent().getParcelableExtra(KEY_MOVIE);

        if (mMovieData == null) {
            //no movie data, finish self.
            finish();
        } else {
            CollapsingToolbarLayout ctl =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            ctl.setTitle(mMovieData.title);
            bindMovieData(mMovieData);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    private void bindMovieData(MovieData movieData) {
        TextView textView;
        textView = (TextView) findViewById(R.id.movie_synopsis);
        textView.setText(movieData.overview);

        textView = (TextView) findViewById(R.id.movie_release_date);
        textView.setText(movieData.release_date);

        textView = (TextView) findViewById(R.id.movie_user_rating);
        textView.setText(String.valueOf(movieData.vote_average)
                + " " + getString(R.string.max_user_rating));

        textView = (TextView) findViewById(R.id.movie_title);
        textView.setText(movieData.title);

        ImageView iv;
        iv = (ImageView) findViewById(R.id.movie_poster);
        Glide.with(this).load(movieData.getPosterUri())
                .placeholder(new ColorDrawable(getResources().getColor(R.color.lgrey))).fitCenter().into(iv);

        iv = (ImageView) findViewById(R.id.movie_backdrop);
        Glide.with(this).load(mMovieData.getBackdropUri()).into(iv);
    }

    @Override
    public void onBackStackChanged() {

    }

    public static Intent getLaunchIntent(final Context context, final MovieData movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(KEY_MOVIE, movie);
        return intent;
    }
}
