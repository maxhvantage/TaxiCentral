package com.taxicentral.Tabs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    public static CorporateAccountAdapter corporateAccountAdapter;
    public CorporateFragment corporateFragmentinstance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_corporate, container, false);

        corporateFragmentinstance = this;
        corporateListview = (ExpandableListView) view.findViewById(R.id.trip_lv);

        tripHistoryList = new HashMap<String, ArrayList<Trip>>();
        monthList = new ArrayList<String>();



        if(Function.isOnline(getActivity())){
            new CorporateAccountTask().execute();
        }else{
            Snackbar.make(getView().findViewById(android.R.id.content), getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
        }
        corporateAccountAdapter = new CorporateAccountAdapter(getActivity(), monthList, tripHistoryList);
        corporateListview.setAdapter(corporateAccountAdapter);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        corporateListview.setIndicatorBounds(width - GetDipsFromPixel(55), width - GetDipsFromPixel(25));

        return view;
    }

    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public class CorporateAccountTask extends AsyncTask<Void, Void, ArrayList<Trip>> {

        ArrayList<Trip> tripList;
        ArrayList<String> monthArrayList;
        public CorporateAccountTask(){

            monthArrayList = new ArrayList<String>();
        }

        @Override
        protected ArrayList<Trip> doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject object = new JSONObject();
                object.put("driverId", AppPreferences.getDriverId(getActivity()));
                String json = serviceHandler.makeServiceCall(AppConstants.ACC_STATE_CORPORATE, ServiceHandler.POST, object);
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
                                JSONObject jsonObjMonthList = jsonObj.getJSONObject(monthArrayList.get(j));
                                JSONArray jsonArrayinside = jsonObjMonthList.getJSONArray("result");
                                tripList = new ArrayList<>();
                                for(int k=0; k<jsonArrayinside.length(); k++){
                                    JSONObject object1 = jsonArrayinside.getJSONObject(k);
                                    Trip trip = new Trip();
                                    trip.setFare("Trip "+(k+1)+" - "+getString(R.string.currency)+object1.getString("trip"));
                                    tripList.add(trip);
                                }
                                Trip trip = new Trip();
                                trip.setFare("Total " + monthArrayList.get(j) + " - " + getString(R.string.currency) + jsonObjMonthList.getInt("amount"));
                                tripList.add(trip);
                                Trip trip1 = new Trip();
                                trip1.setFare("");
                                tripList.add(trip1);
                             //   String totalAmount = jsonObjMonthList.getString("amount");

                                Log.d("test", "Total "+monthArrayList.get(j)+" "+getString(R.string.currency)+jsonObjMonthList.getInt("amount"));

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