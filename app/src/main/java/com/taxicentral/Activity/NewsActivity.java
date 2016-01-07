package com.taxicentral.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.taxicentral.Adapter.NewsAdapter;
import com.taxicentral.Model.NewsModel;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    //aa
    ListView news_listview;
    NewsAdapter newsAdapter;
    List<NewsModel> modelsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        news_listview = (ListView) findViewById(R.id.news_listview);
        modelsList = new ArrayList<NewsModel>();

        newsAdapter = new NewsAdapter(NewsActivity.this, modelsList);
        news_listview.setAdapter(newsAdapter);

        news_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsActivity.this, NewsContentActivity.class);
                intent.putExtra("news", modelsList.get(position));
                startActivity(intent);
            }
        });

        new getNewsTask().execute();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private class getNewsTask extends AsyncTask<Void,Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", AppPreferences.getDriverId(NewsActivity.this));
                String json = serviceHandler.makeServiceCall(AppConstants.NEWS, ServiceHandler.POST,jsonObject);
                if(json != null){
                    JSONObject jsonObj = new JSONObject(json);
                    if(jsonObj.getString("status").equalsIgnoreCase("200")){
                        JSONArray jsonArray = jsonObj.getJSONArray("result");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            NewsModel news = new NewsModel();
                            news.setTitle(object.getString("title"));
                            news.setContent(object.getString("discription"));
                            news.setImage(object.getString("newsImage"));
                            modelsList.add(news);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newsAdapter.updateResult(modelsList);
                                }
                            });
                        }
                        return "200";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

    }
}