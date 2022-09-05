package com.example.machinetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Bundle bundle = getIntent().getExtras();
        //Extract the data…
        String image = bundle.getString("IMAGE");
        String title = bundle.getString("TITLE");
        String summary = bundle.getString("SUMMARY");
        String rating = bundle.getString("RATING");
        String genres = bundle.getString("GENRES");

        Picasso.get()
                .load(image)
//                    .placeholder(R.drawable.user_placeholder)
//                    .error(R.drawable.user_placeholder_error)
                .into((ImageView) findViewById(R.id.imgView));

        TextView txtTitle = findViewById(R.id.title);
        txtTitle.setText(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_COMPACT));

       // TextView txtSummary = findViewById(R.id.summary);
        //txtSummary.setText(summary);
        WebView webViewSummary = (WebView) findViewById(R.id.summary);

        webViewSummary.loadDataWithBaseURL(null, summary, "text/html", "utf-8", null);


        TextView txtRating = findViewById(R.id.rating);
        txtRating.setText("Rating : "+rating);


        TextView txtGenre = findViewById(R.id.genres);
        txtGenre.setText("Genres : "+genres);


    }
}