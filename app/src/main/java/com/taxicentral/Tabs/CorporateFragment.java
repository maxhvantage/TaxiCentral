package com.taxicentral.Tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.taxicentral.Adapter.CorporateAccountAdapter;
import com.taxicentral.Adapter.TripHistoryAdapter;
import com.taxicentral.Model.NewsModel;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MAX on 11/7/2015.
 */
public class CorporateFragment extends Fragment {


    ExpandableListView corporateListview;
    HashMap<String, ArrayList<Trip>> tripHistoryList;
    ArrayList<String> monthList;
    CorporateAccountAdapter corporateAccountAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_corporate, container, false);

        corporateListview = (ExpandableListView) view.findViewById(R.id.trip_lv);

        tripHistoryList = new HashMap<String, ArrayList<Trip>>();
        monthList = new ArrayList<String>();

        if(Function.isOnline(getActivity())){
            new CorporateAccountTask("","").execute();
        }else{
            Snackbar.make(view.findViewById(android.R.id.content), getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
        }

        corporateAccountAdapter = new CorporateAccountAdapter(getActivity(), monthList, tripHistoryList);
        corporateListview.setAdapter(corporateAccountAdapter);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        corporateListview.setIndicatorBounds(width - GetDipsFromPixel(55), width - GetDipsFromPixel(18));

        return view;
    }

    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private class CorporateAccountTask extends AsyncTask<Void, Void, ArrayList<Trip>> {

        ArrayList<Trip> tripList;
        ArrayList<String> monthArrayList;
        String fromdate, todate;
        public CorporateAccountTask(String fromdate, String todate){
            this.fromdate = fromdate;
            this.todate = todate;
            monthArrayList = new ArrayList<String>();
        }

        @Override
        protected ArrayList<Trip> doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject object = new JSONObject();
                object.put("driverId", AppPreferences.getDriverId(getActivity()));
                object.put("fromDate", fromdate);
                object.put("toDate", todate);
                String json = serviceHandler.makeServiceCall(AppConstants.TRIPHISTORY, ServiceHandler.POST, object);
                if(json != null){
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        JSONArray monthArray = jsonObject.getJSONArray("month");
                        for(int a=0; a<monthArray.length(); a++){
                            JSONObject monthObj = monthArray.getJSONObject(a);
                            monthArrayList.add(monthObj.getString("name"));
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        for(int i=0; i<jsonArray.length(); i++){

                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            for(int j=0; j<jsonObj.length(); j++){
                                JSONArray jsonArray2 = jsonObj.getJSONArray(monthArrayList.get(j));

                                tripList = new ArrayList<>();
                                for(int k=0; k<jsonArray2.length(); k++){
                                    JSONObject object1 = jsonArray2.getJSONObject(k);
                                    Trip trip = new Trip();
                                    trip.setId(Long.parseLong(object1.getString("id")));
                                    trip.setDate(object1.getString("tripdate"));
                                    trip.setTripType(object1.getString("tripType"));
                                    trip.setCustomerName(object1.getString("userName"));
                                    trip.setCustomerImage(object1.getString("userImage"));
                                    trip.setSourceAddress(object1.getString("sourceaddress"));
                                    trip.setDestinationAddress(object1.getString("destinationaddress"));
                                    trip.setSourceLatitude(new Double(object1.getString("sourcelatitute")));
                                    trip.setSourcelogitude(new Double(object1.getString("sourcelongitude")));
                                    trip.setDestinationLatitude(new Double(object1.getString("destination_lat")));
                                    trip.setDestinationLogitude(new Double(object1.getString("destination_lon")));
                                    tripList.add(trip);
                                }
                                monthList.add(monthArrayList.get(j));
                                tripHistoryList.put(monthList.get(j),tripList);
                            }

                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tripList;
        }

        @Override
        protected void onPostExecute(ArrayList<Trip> trips) {
            super.onPostExecute(trips);
            corporateAccountAdapter.updateResult(monthList, tripHistoryList);
            /*if(btn_filter != null){
                btn_filter.setClickable(true);
            }*/
        }
    }
}