package com.taxicentral.Utils;

/**
 * Created by MAX on 11/9/2015.
 */
public class AppConstants {

    public static final int TRIP_FREE = 0;
    public static final int TRIP_ALOT = 200;
    public static final int TRIP_START = 202;
    public static final int TRIP_STOP = 209;
    public static final int TRIP_FINISH = 500;

    public static final int POSTPAID = 0;
    public static final int PREPAID = 1;

    public static final String APP = "userapp";
    public static final String CORPORATE = "corporate";
    public static final String TAXI_COMPANY = "taxicompany";
    public static final String WEB_PORTAL = "webportal";

    public static final String APPURL_OLD = "http://priyanshpatodi.com/testsite/texiDriverApp/api/api.php?method=";
    public static final String APPURL = "http://www.hvantagetechnologies.com/central-taxi/driver_app/";

    public static final String REGISTER = APPURL+"register.php";
    public static final String LOGIN = APPURL + "general_api.php?method=SignIn";
    public static final String CHANGEPASSWORD = APPURL + "general_api.php?method=changePassword";
    public static final String LOGOUT = APPURL + "general_api.php?method=logout";
    public static final String GETTRIP = APPURL + "trip.php?method=getTrip";
    public static final String ACCEPTTRIP = APPURL + "trip.php?method=acceptTrip";
    public static final String TRIPREJECT = APPURL + "trip.php?method=rejectTrip";
    public static final String SENDMESSAGE = APPURL + "trip.php?method=sendMessage";
    public static final String ARRIVEDTRIP = APPURL + "trip.php?method=arrivedTrip";
    public static final String BOARDEDTRIP = APPURL + "trip.php?method=boardedTrip";
    public static final String FINISHTRIP = APPURL + "trip.php?method=endTrip";
    public static final String RATING = APPURL + "trip.php?method=rateing";
    public static final String ANONYMOUSREPORT = APPURL + "trip.php?method=anonymousReport";
    public static final String NEWS = APPURL + "trip.php?method=news";
    public static final String TRIPHISTORY = APPURL + "trip.php?method=tripHistory";
    public static final String UPDATELOCATION = APPURL + "trip.php?method=locationUpdate";
    public static final String PAYPAYMENT = APPURL + "trip.php?method=PayPayment";

    public static final String PAYMENTCANCELED = APPURL_OLD + "paymentCancelled";
    public static final String STARTTRIP = APPURL_OLD + "startTrip";
    public static final String STOPTRIP = APPURL_OLD + "stopTrip";


}
