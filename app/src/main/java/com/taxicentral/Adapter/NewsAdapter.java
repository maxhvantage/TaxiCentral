package com.taxicentral.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxicentral.Model.NewsModel;
import com.taxicentral.Model.NotificationModel;
import com.taxicentral.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMFA-MANISH on 11/7/2015.
 */
public class NewsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    List<NewsModel> modelList = new ArrayList<NewsModel>();

    public NewsAdapter(Context context, List<NewsModel> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    public void updateResult(List<NewsModel> modelList){
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsModel news = modelList.get(position);
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.news_list, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView content = (TextView) convertView.findViewById(R.id.discription);
        ImageView newsImage = (ImageView) convertView.findViewById(R.id.news_image);

        title.setText(news.getTitle());
        content.setText(news.getContent());

        Picasso.with(context)
                .load(news.getImage())
                .error(R.drawable.ic_action_picture)
                .resize(200, 200)
                .into(newsImage);

        return convertView;
    }
}
