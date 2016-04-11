package com.taxicentral.Tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAX on 11/7/2015.
 */
public class MyAccountFragment extends Fragment {

    public static ArrayAdapter<String> myPaymentAdapter;
    List<String> itemList;
    ListView myPaymentListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        myPaymentListView = (ListView) view.findViewById(R.id.payment_listView);

        itemList = new ArrayList<String>();
        myPaymentAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemList);
        myPaymentListView.setAdapter(myPaymentAdapter);

        if(Function.isOnline(getActivity())) {
            new getMyAccountTask().execute();
        }else{
//            Snackbar.make(view.findViewById(android.R.id.content), getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
        }

        return view;
    }



    private class getMyAccountTask extends AsyncTask<Void,Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", AppPreferences.getDriverId(getActivity()));
                String json = serviceHandler.makeServiceCall(AppConstants.ACC_STATE_MYACCOUNT, ServiceHandler.POST,jsonObject);
                if(json != null){
                    JSONObject jsonObj = new JSONObject(json);
                    if(jsonObj.getString("status").equalsIgnoreCase("200")){
                        List<String> monthList = new ArrayList<String>();
                        JSONArray jsonArray = jsonObj.getJSONArray("month");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            monthList.add(jsonObject1.getString("name"));
                        }
                        JSONArray jsonArray1 = jsonObj.getJSONArray("result");
                        for(int j=0; j<jsonArray1.length(); j++){
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);

                            int amount = 200;

                           //     month = jsonObject1.getInt(monthList.get(j));


                            String type = jsonObject1.getString("type");
                            itemList.add(monthList.get(j)+" - $"+amount+" - "+ type);

                        }

                        return "200";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myPaymentAdapter.notifyDataSetChanged();
        }
    }
}