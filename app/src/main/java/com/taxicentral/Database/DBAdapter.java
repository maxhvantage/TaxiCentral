package com.taxicentral.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.taxicentral.Model.NotificationModel;
import com.taxicentral.Model.Trip;
import com.taxicentral.Model.User;

import java.util.ArrayList;
import java.util.List;


public class DBAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "taxidriver.db";

    /* TABLE NAME*/
    public static final String TBL_TRIP = "tbl_trip";
    public static final String TBL_USER = "tbl_user";
    public static final String TBL_LOCATION = "tbl_location";
    public static final String TBL_NOTIFICATION = "tbl_notification";
    public static final String TBL_TRIP_HISTORY = "tbl_trip_history";

    /* KEY NAME*/
    public static final String ID = "id";
    public static final String KEY_DRIVER_ID = "driver_id";
    public static final String KEY_TRIP_ID = "trip_id";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_AGREEMENT = "agreement";
    public static final String KEY_FARE = "fare";
    public static final String KEY_DATE = "date";
    public static final String KEY_ADDED_ON = "added_on";
    public static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERIMAGE = "userimage";
    public static final String KEY_EMAILID = "emailid";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_CUSTOMER_NAME = "customerName";
    public static final String KEY_CUSTOMER_IMAGE = "customerImage";
    public static final String KEY_SOURCEADDRESS = "sourceAddress";
    public static final String KEY_DESTINATIONADDRESS = "destinationAddress";
    public static final String KEY_WHERETOCOMETRIP = "whereToComeTrip";
    public static final String KEY_CURRENTLATITUDE = "currentLatitude";
    public static final String KEY_CURRENTLOGITUDE = "currentlogitude";
    public static final String KEY_SOURCELATITUDE = "sourceLatitude";
    public static final String KEY_SOURCELOGITUDE = "sourcelogitude";
    public static final String KEY_DESTINATIONLATITUDE = "destinationLatitude";
    public static final String KEY_DESTINATIONLOGITUDE = "destinationLogitude";
    public static final String KEY_TRIPTYPE = "triptype";
    public static final String KEY_TRAVELTIME = "travel_time";
    public static final String KEY_CORPORATETYPE = "corporate_type";
    public static final String KEY_NOTIFY_HEADING = "notify_heading";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_HISTORY_MONTH = "history_month";
    public static final String KEY_CUSTOMER_RATING = "KEY_CUSTOMER_RATING";

    /*Account Statment*/



    private static final String CREATE_TBL_LOCATION = "create table "
            + TBL_LOCATION + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_DRIVER_ID + " text null, "
            + KEY_CURRENTLATITUDE + " text null, "
            + KEY_CURRENTLOGITUDE + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_USER = "create table "
            + TBL_USER + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_DRIVER_ID + " text null, "
            + KEY_NAME + " text null, "
            + KEY_USERNAME + " text null, "
            + KEY_USERIMAGE + " text null, "
            + KEY_EMAILID + " text null, "
            + KEY_ADDRESS + " text null, "
            + KEY_PHONE + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_TRIP = "create table "
            + TBL_TRIP + " (" + ID
            + " integer primary key, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_DISTANCE + " text null, "
            + KEY_AGREEMENT + " text null, "
            + KEY_FARE + " text null, "
            + KEY_DATE + " text null,"
            + KEY_CUSTOMER_NAME + " text null,"
            + KEY_PHONE + " text null,"
            + KEY_SOURCEADDRESS + " text null,"
            + KEY_DESTINATIONADDRESS + " text null,"
            + KEY_WHERETOCOMETRIP + " text null,"
            + KEY_SOURCELATITUDE + " text null,"
            + KEY_SOURCELOGITUDE + " text null,"
            + KEY_DESTINATIONLATITUDE + " text null,"
            + KEY_DESTINATIONLOGITUDE + " text null,"
            + KEY_TRIPTYPE + " text null,"
            + KEY_TRAVELTIME + " text null,"
            + KEY_CORPORATETYPE + " text null,"
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_TRIP_HISTORY = "create table "
            + TBL_TRIP_HISTORY + " (" + ID
            + " integer primary key, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_DATE + " DATETIME null,"
            + KEY_TRIPTYPE + " text null,"
            + KEY_HISTORY_MONTH + " text null,"
            + KEY_CUSTOMER_NAME + " text null,"
            + KEY_CUSTOMER_RATING + " text null,"
            + KEY_CUSTOMER_IMAGE + " text null,"
            + KEY_SOURCEADDRESS + " text null,"
            + KEY_DESTINATIONADDRESS + " text null,"
            + KEY_SOURCELATITUDE + " text null,"
            + KEY_SOURCELOGITUDE + " text null,"
            + KEY_DESTINATIONLATITUDE + " text null,"
            + KEY_DESTINATIONLOGITUDE + " text null,"
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String CREATE_TBL_NOTIFICATION = "create table "
            + TBL_NOTIFICATION + " (" + ID
            + " integer primary key autoincrement, "
            + KEY_TRIP_ID + " integer null, "
            + KEY_NOTIFY_HEADING + " text null, "
            + KEY_DESCRIPTION + " text null, "
            + KEY_ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ");";

    public DBAdapter(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_TBL_USER);
        db.execSQL(CREATE_TBL_TRIP);
        db.execSQL(CREATE_TBL_LOCATION);
        db.execSQL(CREATE_TBL_NOTIFICATION);
        db.execSQL(CREATE_TBL_TRIP_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String query0 = "DROP TABLE IF EXISTS " + TBL_USER;
        String query1 = "DROP TABLE IF EXISTS " + TBL_TRIP;
        String query2 = "DROP TABLE IF EXISTS " + TBL_LOCATION;
        String query3 = "DROP TABLE IF EXISTS " + TBL_NOTIFICATION;
        String query4 = "DROP TABLE IF EXISTS " + TBL_TRIP_HISTORY;

        db.execSQL(query0);
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);

        onCreate(db);
    }

    // Insert Data
    // TODO Auto-generated method stub

    public long insertUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DRIVER_ID, user.getId());
        initialValues.put(KEY_NAME, user.getName());
        initialValues.put(KEY_USERNAME, user.getUserName());
        initialValues.put(KEY_USERIMAGE, user.getUserImage());
        initialValues.put(KEY_PHONE, user.getPhone());
        initialValues.put(KEY_EMAILID, user.getEmailId());
        initialValues.put(KEY_ADDRESS, user.getAddress());

        long id = db.insert(TBL_USER, null, initialValues);

       // Log.d("insertUser", initialValues.toString());

        db.close();
        return id;
    }

    public long updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, user.getName());
        initialValues.put(KEY_USERNAME, user.getUserName());
        initialValues.put(KEY_USERIMAGE, user.getUserImage());
        initialValues.put(KEY_PHONE, user.getPhone());
        initialValues.put(KEY_EMAILID, user.getEmailId());
        initialValues.put(KEY_ADDRESS, user.getAddress());

        long id = db.update(TBL_USER, initialValues, KEY_DRIVER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        //Log.d("UpdateUser", initialValues.toString());
        db.close();
        return id;
    }

    public long insertLocation(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(ID, user.getId());
        initialValues.put(KEY_CURRENTLATITUDE, user.getCurrentLatitude());
        initialValues.put(KEY_CURRENTLOGITUDE, user.getCurrentLongitude());
        initialValues.put(KEY_ADDED_ON, user.getAddedOn());

        long id = db.insert(TBL_LOCATION, null, initialValues);

        db.close();
        return id;
    }

    public long insertNotification(NotificationModel notification) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, notification.getId());
        initialValues.put(KEY_NOTIFY_HEADING, notification.getHeader());
        initialValues.put(KEY_DESCRIPTION, notification.getDescription());

        long id = db.insert(TBL_NOTIFICATION, null, initialValues);

        db.close();
        return id;
    }

    public long insertTrip(Trip trip) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, trip.getId());
        initialValues.put(KEY_DISTANCE, trip.getDistance());
        initialValues.put(KEY_FARE, trip.getFare());
        initialValues.put(KEY_AGREEMENT, trip.getAgreement());
        initialValues.put(KEY_DATE, trip.getDate());
        initialValues.put(KEY_CUSTOMER_NAME,trip.getCustomerName());
        initialValues.put(KEY_PHONE,trip.getNumber());
        initialValues.put(KEY_SOURCEADDRESS,trip.getSourceAddress());
        initialValues.put(KEY_DESTINATIONADDRESS,trip.getDestinationAddress());
        initialValues.put(KEY_SOURCELATITUDE,trip.getSourceLatitude());
        initialValues.put(KEY_SOURCELOGITUDE,trip.getSourcelogitude());
        initialValues.put(KEY_DESTINATIONLATITUDE,trip.getDestinationLatitude());
        initialValues.put(KEY_DESTINATIONLOGITUDE,trip.getDestinationLogitude());
        initialValues.put(KEY_TRIPTYPE,trip.getTripType());
        initialValues.put(KEY_TRAVELTIME,trip.getTravelTime());
        initialValues.put(KEY_CORPORATETYPE, trip.getCorporateType());

        long id = db.insert(TBL_TRIP, null, initialValues);

        db.close();
        return id;
    }

    public long insertTripHistory(Trip trip) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, trip.getId());
        initialValues.put(KEY_DATE, trip.getDate());
        initialValues.put(KEY_TRIPTYPE,trip.getTripType());
        initialValues.put(KEY_CUSTOMER_NAME,trip.getCustomerName());
        initialValues.put(KEY_CUSTOMER_IMAGE,trip.getCustomerImage());
        initialValues.put(KEY_SOURCEADDRESS,trip.getSourceAddress());
        initialValues.put(KEY_DESTINATIONADDRESS,trip.getDestinationAddress());
        initialValues.put(KEY_SOURCELATITUDE,trip.getSourceLatitude());
        initialValues.put(KEY_SOURCELOGITUDE,trip.getSourcelogitude());
        initialValues.put(KEY_DESTINATIONLATITUDE,trip.getDestinationLatitude());
        initialValues.put(KEY_DESTINATIONLOGITUDE,trip.getDestinationLogitude());
        initialValues.put(KEY_HISTORY_MONTH,trip.getMonth());
        initialValues.put(KEY_CUSTOMER_RATING,trip.getCustomerRating());

        long id = db.insert(TBL_TRIP_HISTORY, null, initialValues);

        db.close();
        return id;
    }


    //TODO GET ALL DATA FROM DATABASE


    public ArrayList<User> getUserList() {

        ArrayList<User> userList = new ArrayList<User>();
        try {
            String query = "select * from " + TBL_USER + ";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return userList;
            } else if (cursor.getCount() == 0) {
                return userList;
            }
            if (cursor.moveToFirst()) {

                do {
                    User user = new User();
                    user.setId(cursor.getLong(cursor.getColumnIndex(KEY_DRIVER_ID)));
                    user.setName(cursor.getString(cursor
                            .getColumnIndex(KEY_NAME)));
                    user.setUserName(cursor.getString(cursor
                            .getColumnIndex(KEY_USERNAME)));
                    user.setUserImage(cursor.getString(cursor
                            .getColumnIndex(KEY_USERIMAGE)));
                    user.setPhone(cursor.getString(cursor
                            .getColumnIndex(KEY_PHONE)));
                    user.setEmailId(cursor.getString(cursor
                            .getColumnIndex(KEY_EMAILID)));
                    user.setAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_ADDRESS)));
                    user.setAddedOn(cursor.getString(cursor
                            .getColumnIndex(KEY_ADDED_ON)));


                    userList.add(user);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;

    }

    public User getUser(int id) {

        User user = new User();
        try {
            String query = "select * from " + TBL_USER +" where "+KEY_DRIVER_ID +" = " + id + ";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return user;
            } else if (cursor.getCount() == 0) {
                return user;
            }
            if (cursor.moveToFirst()) {

                do {
                    //  User user = new User();
                    user.setId(cursor.getLong(cursor.getColumnIndex(KEY_DRIVER_ID)));
                    user.setName(cursor.getString(cursor
                            .getColumnIndex(KEY_NAME)));
                    user.setUserName(cursor.getString(cursor
                            .getColumnIndex(KEY_USERNAME)));
                    user.setUserImage(cursor.getString(cursor
                            .getColumnIndex(KEY_USERIMAGE)));
                    user.setPhone(cursor.getString(cursor
                            .getColumnIndex(KEY_PHONE)));
                    user.setEmailId(cursor.getString(cursor
                            .getColumnIndex(KEY_EMAILID)));
                    user.setAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_ADDRESS)));
                    user.setAddedOn(cursor.getString(cursor
                            .getColumnIndex(KEY_ADDED_ON)));

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;

    }

    public ArrayList<User> getLocation() {

        ArrayList<User> userList = new ArrayList<User>();
        try {
            String query = "select * from " + TBL_LOCATION + ";";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return userList;
            } else if (cursor.getCount() == 0) {
                return userList;
            }
            if (cursor.moveToFirst()) {

                do {
                    User user = new User();
                    user.setId(cursor.getLong(cursor.getColumnIndex(ID)));
                    user.setCurrentLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_CURRENTLATITUDE)));
                    user.setCurrentLongitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_CURRENTLOGITUDE)));
                    user.setAddedOn(cursor.getString(cursor
                            .getColumnIndex(KEY_ADDED_ON)));

                    Log.d("dateAddedOn", cursor.getString(cursor
                            .getColumnIndex(KEY_ADDED_ON)));
                    userList.add(user);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;

    }

    public List<NotificationModel> getAllNotification() {

        List<NotificationModel> notificationList = new ArrayList<NotificationModel>();
        try {
            String query = "select * from " + TBL_NOTIFICATION + " ORDER BY "+ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return notificationList;
            } else if (cursor.getCount() == 0) {
                return notificationList;
            }
            if (cursor.moveToFirst()) {

                do {
                    NotificationModel notification = new NotificationModel();
                    notification.setHeader(cursor.getString(cursor
                            .getColumnIndex(KEY_NOTIFY_HEADING)));
                    notification.setDescription(cursor.getString(cursor
                            .getColumnIndex(KEY_DESCRIPTION)));



                    notificationList.add(notification);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationList;

    }

    public ArrayList<Trip> getTrip() {

        ArrayList<Trip> tripList = new ArrayList<Trip>();
        try {
            String query = "select * from " + TBL_TRIP + " ORDER BY "+KEY_TRIP_ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    Trip trip = new Trip();
                    trip.setId(cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));
                    trip.setDistance(cursor.getString(cursor
                            .getColumnIndex(KEY_DISTANCE)));
                    trip.setAgreement(cursor.getString(cursor
                            .getColumnIndex(KEY_AGREEMENT)));
                    trip.setFare(cursor.getString(cursor
                            .getColumnIndex(KEY_FARE)));
                    trip.setDate(cursor.getString(cursor
                            .getColumnIndex(KEY_DATE)));
                    trip.setCustomerName(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_NAME)));
                    trip.setNumber(cursor.getString(cursor
                            .getColumnIndex(KEY_PHONE)));
                    trip.setSourceAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_SOURCEADDRESS)));
                    trip.setDestinationAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_DESTINATIONADDRESS)));
                    trip.setSourceLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELATITUDE)));
                    trip.setSourcelogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELOGITUDE)));
                    trip.setDestinationLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLATITUDE)));
                    trip.setDestinationLogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLOGITUDE)));
                    trip.setTripType(cursor.getString(cursor
                            .getColumnIndex(KEY_TRIPTYPE)));
                    trip.setTravelTime(cursor.getString(cursor
                            .getColumnIndex(KEY_TRAVELTIME)));
                    trip.setCorporateType(cursor.getInt(cursor.getColumnIndex(KEY_CORPORATETYPE)));


                    tripList.add(trip);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }

    public ArrayList<Integer> getTripId() {

        ArrayList<Integer> tripList = new ArrayList<Integer>();
        try {
            String query = "select * from " + TBL_TRIP + " ORDER BY "+ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    /*Trip trip = new Trip();
                    trip.setId(cursor.getLong(cursor.getColumnIndex(ID)));
                    */

                    tripList.add(cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)));
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }

    public ArrayList<Trip> getTripHistory() {

        ArrayList<Trip> tripList = new ArrayList<Trip>();
        try {
            String query = "select * from " + TBL_TRIP_HISTORY + " ORDER BY "+KEY_TRIP_ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    Trip trip = new Trip();
                    trip.setId(cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));
                    trip.setDate(cursor.getString(cursor
                            .getColumnIndex(KEY_DATE)));
                    trip.setCustomerName(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_NAME)));
                    trip.setCustomerImage(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_IMAGE)));
                    trip.setTripType(cursor.getString(cursor
                            .getColumnIndex(KEY_TRIPTYPE)));
                    trip.setMonth(cursor.getString(cursor
                            .getColumnIndex(KEY_HISTORY_MONTH)));
                    trip.setSourceAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_SOURCEADDRESS)));
                    trip.setDestinationAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_DESTINATIONADDRESS)));
                    trip.setSourceLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELATITUDE)));
                    trip.setSourcelogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELOGITUDE)));
                    trip.setDestinationLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLATITUDE)));
                    trip.setDestinationLogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLOGITUDE)));
                    trip.setCustomerRating(cursor.getFloat(cursor.getColumnIndex(KEY_CUSTOMER_RATING)));


                    tripList.add(trip);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }
    public ArrayList<Trip> getTripHistoryFilter(String fromDate, String toDate) {

        ArrayList<Trip> tripList = new ArrayList<Trip>();
        try {
            String query = "select * from " + TBL_TRIP_HISTORY +" where "+KEY_DATE+" between '"+ fromDate+" 00:00:00"+"' and '"+ toDate +" 23:59:59"+ "' ORDER BY "+KEY_TRIP_ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    Trip trip = new Trip();
                    trip.setId(cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));
                    trip.setDate(cursor.getString(cursor
                            .getColumnIndex(KEY_DATE)));

                    trip.setCustomerName(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_NAME)));
                    trip.setCustomerImage(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_IMAGE)));
                    trip.setTripType(cursor.getString(cursor
                            .getColumnIndex(KEY_TRIPTYPE)));
                    trip.setMonth(cursor.getString(cursor
                            .getColumnIndex(KEY_HISTORY_MONTH)));
                    trip.setSourceAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_SOURCEADDRESS)));
                    trip.setDestinationAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_DESTINATIONADDRESS)));
                    trip.setSourceLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELATITUDE)));
                    trip.setSourcelogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELOGITUDE)));
                    trip.setDestinationLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLATITUDE)));
                    trip.setDestinationLogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLOGITUDE)));
                    trip.setCustomerRating(cursor.getFloat(cursor.getColumnIndex(KEY_CUSTOMER_RATING)));


                    tripList.add(trip);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }
    public ArrayList<Trip> getTripHistoryByMonth(String month) {

        ArrayList<Trip> tripList = new ArrayList<Trip>();
        try {
            String query = "select * from " + TBL_TRIP_HISTORY +" where "+KEY_HISTORY_MONTH+" = '"+month+ "' ORDER BY "+KEY_TRIP_ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    Trip trip = new Trip();
                    trip.setId(cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));

                    trip.setDate(cursor.getString(cursor
                            .getColumnIndex(KEY_DATE)));
                    trip.setCustomerName(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_NAME)));
                    trip.setCustomerImage(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_IMAGE)));
                    trip.setTripType(cursor.getString(cursor
                            .getColumnIndex(KEY_TRIPTYPE)));
                    trip.setMonth(cursor.getString(cursor
                            .getColumnIndex(KEY_HISTORY_MONTH)));
                    trip.setSourceAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_SOURCEADDRESS)));
                    trip.setDestinationAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_DESTINATIONADDRESS)));
                    trip.setSourceLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELATITUDE)));
                    trip.setSourcelogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELOGITUDE)));
                    trip.setDestinationLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLATITUDE)));
                    trip.setDestinationLogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLOGITUDE)));
                    trip.setCustomerRating(cursor.getFloat(cursor.getColumnIndex(KEY_CUSTOMER_RATING)));


                    tripList.add(trip);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }

    public ArrayList<Trip> getTripHistoryByMonthWithDate(String month,String fromDate, String toDate) {

        ArrayList<Trip> tripList = new ArrayList<Trip>();
        try {
            String query = "select * from " + TBL_TRIP_HISTORY +" where "+KEY_HISTORY_MONTH+" = '"+month+"' and "+KEY_DATE+" between '"+ fromDate+" 00:00:00"+"' and '"+ toDate +" 23:59:59"+ "' ORDER BY "+KEY_TRIP_ID+" DESC;";
            System.out.println("query : " + query);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor == null) {
                return tripList;
            } else if (cursor.getCount() == 0) {
                return tripList;
            }
            if (cursor.moveToFirst()) {

                do {
                    Trip trip = new Trip();
                    trip.setId(cursor.getLong(cursor.getColumnIndex(KEY_TRIP_ID)));

                    trip.setDate(cursor.getString(cursor
                            .getColumnIndex(KEY_DATE)));
                    trip.setCustomerName(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_NAME)));
                    trip.setCustomerImage(cursor.getString(cursor
                            .getColumnIndex(KEY_CUSTOMER_IMAGE)));
                    trip.setTripType(cursor.getString(cursor
                            .getColumnIndex(KEY_TRIPTYPE)));
                    trip.setMonth(cursor.getString(cursor
                            .getColumnIndex(KEY_HISTORY_MONTH)));
                    trip.setSourceAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_SOURCEADDRESS)));
                    trip.setDestinationAddress(cursor.getString(cursor
                            .getColumnIndex(KEY_DESTINATIONADDRESS)));
                    trip.setSourceLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELATITUDE)));
                    trip.setSourcelogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_SOURCELOGITUDE)));
                    trip.setDestinationLatitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLATITUDE)));
                    trip.setDestinationLogitude(cursor.getDouble(cursor
                            .getColumnIndex(KEY_DESTINATIONLOGITUDE)));
                    trip.setCustomerRating(cursor.getFloat(cursor.getColumnIndex(KEY_CUSTOMER_RATING)));


                    tripList.add(trip);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;

    }



    public boolean deleteLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_LOCATION, ID + "=" + id, null) > 0;
    }

    public boolean deleteTrip(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TRIP, KEY_TRIP_ID + " = " + id, null) > 0;
    }
    public boolean deleteAllTrip(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TRIP, null, null) > 0;
    }
    public boolean deleteTripHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TBL_TRIP_HISTORY, null, null) > 0;
    }



}