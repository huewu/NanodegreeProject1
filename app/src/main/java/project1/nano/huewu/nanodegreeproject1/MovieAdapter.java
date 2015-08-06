package project1.nano.huewu.nanodegreeproject1;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;

import project1.nano.huewu.nanodegreeproject1.events.MovieClickListener;

/**
 * Created by chansuk on 2015. 7. 22..
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = "MovieAdapter";
    private static final Point mSize = new Point();
    private static final float POSTER_RATIO = 41.f / 27.f;

    private int mNumberOfColumns = 1;
    private final ArrayList<MovieData> mMovieDataList;
    private MovieClickListener mMovieClickListener = null;

    public void setNumberOfColumns(int numberOfColumns) {
        mNumberOfColumns = numberOfColumns;
    }

    public MovieAdapter(MovieData[] movies) {
        mMovieDataList = new ArrayList<>(movies.length * 2);
        mMovieDataList.addAll(Arrays.asList(movies));
        setHasStableIds(true);

        Log.d(TAG, "MovieAdapter is set with " + movies.length + " movies");
    }

    @Override
    public long getItemId(int position) {
        return mMovieDataList.get(position).id;
    }

    public void addMovies(MovieData[] movies) {

        final int currentIndex = mMovieDataList.size();
        mMovieDataList.addAll(Arrays.asList(movies));

        if (hasObservers()) {
            notifyItemRangeChanged(currentIndex, movies.length - 1);
            Log.d(TAG, "" + movies.length + " movies are added to the adapter. " +
                    "Total: " + mMovieDataList.size() + " movies");
        }
    }

    public void addMovie(MovieData movie) {
        mMovieDataList.add(movie);
    }

    public void setMovieClickListener(MovieClickListener listener) {
        mMovieClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_movie, parent, false);
        final MovieViewHolder holder = new MovieViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovieClickListener != null) {
                    MovieData movie = mMovieDataList.get(holder.getAdapterPosition());
                    mMovieClickListener.onMovieClick(movie);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final ImageView view = holder.mImageView;
        final MovieData movieData = mMovieDataList.get(position);
        Glide.with(view.getContext()).load(movieData.getPosterUri())
                .error(android.R.drawable.ic_dialog_alert)
                .centerCrop().into(view);

        Log.d(TAG, "Bind View :" + view.getWidth()+ ", " + view.getHeight());
    }

    @Override
    public int getItemCount() {
        return mMovieDataList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.grid_item_movie_image);
            final Activity act = (Activity) itemView.getContext();

            Display display = act.getWindowManager().getDefaultDisplay();
            display.getSize(mSize);

            final int width = mSize.x / mNumberOfColumns;
            final int height = (int) (width * POSTER_RATIO);
            mImageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            Log.d(TAG, "Create View :" + width + ", " + height);
        }
    }
}
