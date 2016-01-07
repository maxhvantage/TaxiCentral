package com.taxicentral.Tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

    ArrayAdapter<String> myPaymentAdapter;
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
                String json = serviceHandler.makeServiceCall(AppConstants.NEWS, ServiceHandler.POST,jsonObject);
                if(json != null){
                    JSONObject jsonObj = new JSONObject(json);
                    if(jsonObj.getString("status").equalsIgnoreCase("200")){
                        JSONArray jsonArray = jsonObj.getJSONArray("result");
                        String[] month = {"Jan", "Fab", "Mar", "Apr", "May", "Jun"};
                        for(int i=0; i<6; i++){
//                            JSONObject object = jsonArray.getJSONObject(i);
                            itemList.add(month[i]+" $ 2000 - Paid");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myPaymentAdapter.notifyDataSetChanged();

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