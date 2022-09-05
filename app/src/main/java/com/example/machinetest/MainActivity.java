package com.example.machinetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {
    public static String baseUrl = "https://api.tvmaze.com/"; //for testing  shows
    RecyclerView mRecyclerView;
    GenreAdapter mAdapter;
    List<ShowsModel> mList = new ArrayList<>();
    List<String> genres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new GenreAdapter(getApplicationContext(), genres, mList);
        mRecyclerView = findViewById(R.id.rView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
        getClient(getApplicationContext())
                .create(ShowsInterface.class)
                .getShows()
                .enqueue(new Callback<List<ShowsModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ShowsModel>> call, @NonNull Response<List<ShowsModel>> response) {
                        mList = response.body();
                        System.out.println(response);
                        if (mList != null)
                            Toast.makeText(MainActivity.this, "" + mList.size(), Toast.LENGTH_SHORT).show();

                        genres = new ArrayList<>();

                        for (ShowsModel showsModel : mList) {
                            for (String genre : showsModel.genres) {
                                if (!(genres.contains(genre))) {
                                    genres.add(genre);
                                }
                            }
                        }
                        mAdapter = new GenreAdapter(getApplicationContext(), genres, mList);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ShowsModel>> call, @NonNull Throwable t) {

                    }
                });


    }

    static public Retrofit getClient(Context ctx) {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setVersion(1.0)
                .setLenient()
                .create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }

    public interface ShowsInterface {

        @GET("shows")
        Call<List<ShowsModel>> getShows();
    }

    public static class ShowsModel {

        public static class ShowsImageModel {

            private String medium;

            private String original;

            public String getMedium() {
                return medium;
            }

            public void setMedium(String medium) {
                this.medium = medium;
            }

            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }
        }

        public static class ShowRatingModel {

            private float average;

            public float getAverage() {
                return average;
            }

            public void setAverage(float average) {
                this.average = average;
            }
        }


        private String id;

        private String name;

        private String summary;

        private ShowsImageModel image;

        private ShowRatingModel rating;

        private List<String> genres;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public ShowsImageModel getImage() {
            return image;
        }

        public void setImage(ShowsImageModel image) {
            this.image = image;
        }

        public ShowRatingModel getRating() {
            return rating;
        }

        public void setRating(ShowRatingModel rating) {
            this.rating = rating;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }
    }

    static class ShowsAdapter extends RecyclerView.Adapter<ShowsViewHolder> {
        List<ShowsModel> showsList;
        Context context;

        ShowsAdapter(Context mContext, List<ShowsModel> mShowsList) {
            this.showsList = mShowsList;
            this.context = mContext;
        }

        @NonNull
        @Override
        public ShowsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show, parent, false);
            return new ShowsViewHolder(context, view, showsList);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowsViewHolder holder, int position) {
            ShowsModel showsModel = showsList.get(position);
            Picasso.get()
                    .load(showsModel.getImage().medium)
//                    .placeholder(R.drawable.user_placeholder)
//                    .error(R.drawable.user_placeholder_error)
                    .into((ImageView) holder.itemView.findViewById(R.id.imgView));
        }

        @Override
        public int getItemCount() {
            return showsList.size();
        }
    }

    static class ShowsViewHolder extends RecyclerView.ViewHolder {
        public ShowsViewHolder(Context mContext, @NonNull View itemView, List<ShowsModel> shows) {
            super(itemView);

            itemView.setOnClickListener(view -> {
                if (getAdapterPosition() > -1) {
                    ShowsModel showsModel = shows.get(getAdapterPosition());
                    Intent i = new Intent(mContext, DetailActivity.class);

                    //System.out.println(showsModel.name);
                    //Create the bundle
                    Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    bundle.putString("IMAGE", showsModel.getImage().getMedium());
                    bundle.putString("TITLE", showsModel.getName());
                    bundle.putString("SUMMARY", showsModel.getSummary());
                    bundle.putString("RATING", String.valueOf(showsModel.getRating().getAverage()));
                    bundle.putString("GENRES", TextUtils.join(",", showsModel.getGenres()));
                    //Add the bundle to the intent
                    i.putExtras(bundle);
                    view.getContext().startActivity(i);
                }

            });
        }
    }

    static class GenreAdapter extends RecyclerView.Adapter<GenreViewHolder> {
        List<String> genre;
        List<ShowsModel> shows;
        Context context;
        ShowsAdapter mChildAdapter;

        GenreAdapter(Context mContext, List<String> mGenre, List<ShowsModel> mShows) {
            this.genre = mGenre;
            this.shows = mShows;
            this.context = mContext;
        }

        @NonNull
        @Override
        public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre, parent, false);
            return new GenreViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
            String genreModel = genre.get(position);
            TextView tv = holder.itemView.findViewById(R.id.title);
            tv.setText(genreModel);
            RecyclerView rv = holder.itemView.findViewById(R.id.rView);
            List<ShowsModel> genreShows = new ArrayList<>();
            for (ShowsModel showsModel : shows) {
                if (showsModel.genres.contains(genreModel)) {
                    genreShows.add(showsModel);
                }
            }
            mChildAdapter = new ShowsAdapter(context, genreShows);
            rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            rv.setAdapter(mChildAdapter);
        }

        @Override
        public int getItemCount() {
            return genre.size();
        }
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemView.setOnClickListener();
        }
    }

}