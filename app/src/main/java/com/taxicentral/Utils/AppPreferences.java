package com.taxicentral.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MMFA-MANISH on 11/9/2015.
 */
public class AppPreferences {
    public static final String PREFERENCES = "taxi_central";
    public static final String DRIVER_ID = "driver_id";
    public static final String REQUEST_FROM_APP = "request_from_app";
    public static final String REQUEST_FROM_CORPORATE = "request_from_corporate";
    public static final String REQUEST_FROM_TAXI_COMPANY = "request_from_taxi_company";
    public static final String REQUEST_FROM_WEB_PORTAL = "request_from_web_portal";
    public static final String DIALOG = "dialog";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String RADIUS = "radius";
    public static final String TRIP_ID = "trip_id";
    public static final String PERMISSION = "permisiion";
    public static final String DEVICEID = "device_id";
    public static final String ACTIVITY = "activity";
    public static final String LATLNGS = "latlngs";
    public static final String DISTANCE_TIME = "distance_time";
    public static final String START_TIME = "start_time";
    public static final String ARRIVED_TIME = "arrived_time";
    public static final String END_TIME = "end_time";
    public static final String AVL_CREDIT = "avl_credit";


    public static void setAvlCredit(Context context, float time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat(AVL_CREDIT, time);
        editor.commit();
    }

    public static Float getAvlCredit(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getFloat(AVL_CREDIT, 0);
    }

    ///////////////////////

    public static void setStartTime(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(START_TIME, time);
        editor.commit();
    }

    public static String getStartTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(START_TIME, "");
    }

    ///////////////////////
    public static void setArrivedTime(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(ARRIVED_TIME, time);
        editor.commit();
    }

    public static String getArrivedTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(ARRIVED_TIME, "");
    }

    ///////////////////////
    public static void setEndTime(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(END_TIME, time);
        editor.commit();
    }

    public static String getEndTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(END_TIME, "");
    }

    ///////////////////////

    public static void setDistanceTime(Context context, String distance_time) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DISTANCE_TIME, distance_time);
        editor.commit();
    }

    public static String getDistanceTime(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(DISTANCE_TIME, "");
    }
    ///////////////////////////////////

    public static void setDirections(Context context, JSONArray points) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(LATLNGS, points.toString());
        editor.commit();
    }

    public static String getDirections(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(LATLNGS, "");
    }

    ///////////////////////

    public static void setActivity(Context context, String className) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACTIVITY, className);
        editor.commit();
    }

    public static String getActivity(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(ACTIVITY, "");
    }
    ///////////////////////////////////

    public static void setDeviceid(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEVICEID, id);
        editor.commit();
    }

    public static String getDeviceid(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(DEVICEID, "");
    }
///////////////////////////////////

    public static void setPermission(Context context, int id) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PERMISSION, id);
        editor.commit();
    }

    public static int getPermission(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getInt(PERMISSION, 0);
    }

    ////////////////////////////////////////////////////////////

    public static void setTripId(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TRIP_ID, id);
        editor.commit();
    }

    public static String getTripId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getString(TRIP_ID, "");
    }
///////////////////////////////////

    public static void setRadiusLatitude(Context context, double latitude) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LATITUDE, Double.doubleToRawLongBits(latitude));
        editor.commit();
    }

    public static Double getRadiusLatitude(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return Double.longBitsToDouble(pereference.getLong(LATITUDE, Double.doubleToLongBits(0.0)));
    }
///////////////////////////////////

    public static void setRadiusLongitude(Context context, double longitude) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.commit();
    }

    public static Double getRadiusLongitude(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return Double.longBitsToDouble(pereference.getLong(LONGITUDE, Double.doubleToLongBits(0.0)));
    }

    //////////////////////////////////////
    public static void setRadius(Context context, Double radius) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(RADIUS, Double.doubleToRawLongBits(radius));
        editor.commit();
    }

    public static Double getRadius(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return Double.longBitsToDouble(pereference.getLong(RADIUS, Double.doubleToLongBits(0.0)));
    }
    ///////////////////////////////////////////////

    public static int getDriverId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getInt(DRIVER_ID, 0);
    }

    public static void setDriverId(Context context, int id) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(DRIVER_ID, id);
        editor.commit();
    }
////////////////////////////////////////////

    public static Boolean getRequestFromApp(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getBoolean(REQUEST_FROM_APP, false);
    }

    public static void setRequestFromApp(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REQUEST_FROM_APP, value);
        editor.commit();
    }
//////////////////////////////////////////////////////

    public static Boolean getRequestFromCorporate(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getBoolean(REQUEST_FROM_CORPORATE, false);
    }

    public static void setRequestFromCorporate(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REQUEST_FROM_CORPORATE, value);
        editor.commit();
    }
    //////////////////////////////////////

    public static Boolean getRequestFromWebPortal(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getBoolean(REQUEST_FROM_WEB_PORTAL, false);
    }

    public static void setRequestFromWebPortal(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REQUEST_FROM_WEB_PORTAL, value);
        editor.commit();
    }
////////////////////////////////////

    public static Boolean getRequestFromTaxiCompany(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getBoolean(REQUEST_FROM_TAXI_COMPANY, false);
    }

    public static void setRequestFromTaxiCompany(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REQUEST_FROM_TAXI_COMPANY, value);
        editor.commit();
    }
/////////////////////////////////////

    public static Boolean isShowDialog(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                PREFERENCES, 0);
        return pereference.getBoolean(DIALOG, false);
    }

    public static void setShowDialog(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DIALOG, value);
        editor.commit();
    }
///////////////////////////////////
}
