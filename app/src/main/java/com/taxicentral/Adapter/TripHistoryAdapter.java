package com.taxicentral.Adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;


/**
 * Created by MAX on 11/6/2015.
 */
public class TripHistoryAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<Trip>> _listDataChild;

    public TripHistoryAdapter(Context context, ArrayList<String> listDataHeader,
                               HashMap<String, ArrayList<Trip>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public void updateResult(ArrayList<String> listDataHeader,
                              HashMap<String, ArrayList<Trip>> listChildData) {
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        // Triggers the list update

        notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String header = _listDataHeader.get(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.triphistory_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.header_tv);

        lblListHeader.setText(header);



        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        Trip trip = (Trip)getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.triphistory_list, null);
        }

        ImageView userImage = (ImageView) convertView.findViewById(R.id.user_image);
        TextView txtName = (TextView) convertView
                .findViewById(R.id.trip_name);
        TextView txtAddress = (TextView) convertView
                .findViewById(R.id.trip_address);
        TextView txtDate = (TextView) convertView
                .findViewById(R.id.trip_date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null ;
        try {
            date = formatter.parse(trip.getDate());
            //date = formatter.parse("2015-02-25 13:06");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yyy");
        String setDate =sdf.format(date.getTime());
        SimpleDateFormat sdf1=new SimpleDateFormat("hh:mm aa");
        String setTime =sdf1.format(date.getTime());

        txtName.setText(trip.getCustomerName() +" "+_context.getString(R.string.trip_id)+" "+ trip.getId());
        txtAddress.setText(trip.getSourceAddress());
        txtDate.setText(setDate+" "+ setTime);
        Picasso.with(_context)
                .load(trip.getCustomerImage())
                .error(R.drawable.ic_action_user)
                .resize(200, 200)
                .into(userImage);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }



}