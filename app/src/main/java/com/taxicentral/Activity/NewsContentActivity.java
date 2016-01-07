package com.taxicentral.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxicentral.Model.NewsModel;
import com.taxicentral.R;

public class NewsContentActivity extends AppCompatActivity {

    TextView title, discription;
    ImageView newsImage;
    NewsModel news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        title = (TextView) findViewById(R.id.title);
        discription = (TextView) findViewById(R.id.discription);
        newsImage = (ImageView) findViewById(R.id.news_image);

        news = getIntent().getParcelableExtra("news");

        title.setText(news.getTitle());
        discription.setText(news.getContent());

        Picasso.with(this)
                .load(news.getImage())
                .error(R.drawable.ic_action_picture)
                .resize(200, 200)
                .into(newsImage);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}
