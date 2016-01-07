package com.taxicentral.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by MAX on 11/6/2015.
 */
public class CorporateAccountAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<Trip>> _listDataChild;

    public CorporateAccountAdapter(Context context, ArrayList<String> listDataHeader,
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
            convertView = infalInflater.inflate(R.layout.corporate_account_list, null);
        }

        TextView txtName = (TextView) convertView
                .findViewById(R.id.trip_name);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }



}