package project1.nano.huewu.nanodegreeproject1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chansuk on 2015. 7. 22..
 */
public class MovieData implements Parcelable {

    public final int id;
    public final String backdrop_path;
    public final String title;
    public final String poster_path;
    public final float vote_average;
    public final String release_date;
    public final String overview;

    public MovieData(
            int id, String backdrop_path, String title, String poster_path,
            float vote_average, String release_date, String overview) {
        this.id = id;
        this.backdrop_path = backdrop_path;
        this.title = title;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.overview = overview;
    }

    protected MovieData(Parcel in) {
        id = in.readInt();
        backdrop_path = in.readString();
        title = in.readString();
        poster_path = in.readString();
        vote_average = in.readFloat();
        release_date = in.readString();
        overview = in.readString();
    }

    public String getPosterUri() {
        return "http://image.tmdb.org/t/p/w300" + poster_path;
    }

    public String getBackdropUri() {
        return "http://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(backdrop_path);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeFloat(vote_average);
        dest.writeString(release_date);
        dest.writeString(overview);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
