package com.taxicentral.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taxicentral.Model.NotificationModel;
import com.taxicentral.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMFA-MANISH on 11/7/2015.
 */
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    List<NotificationModel> modelList = new ArrayList<NotificationModel>();

    public NotificationAdapter(Context context, List<NotificationModel> modelList){
        this.context = context;
        this.modelList = modelList;
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

        NotificationModel model = modelList.get(position);
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.notification_list, null);


        TextView header = (TextView) convertView.findViewById(R.id.heading);
        TextView description = (TextView) convertView.findViewById(R.id.description);

        header.setText(model.getHeader());
        description.setText(model.getDescription());

        return convertView;
    }
}
