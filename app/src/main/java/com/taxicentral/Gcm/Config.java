package com.taxicentral.Gcm;

public interface Config {

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "507542572202";
	static final String LOGIN = "message";
	static final String TRIP_CANCEL = "tripcancel";
	static final String TRIP_RECIVE_MSG = "sendmessage";
	static final String TRIP_BOARDED = "borded";
	static final String NEW_TRIP = "newTrip";
	static final String ADMIN_MSG = "adminMessage";
	static final String OUTOFZONE = "adminNotification";
	static final String ZONEUPDATE = "zoneUpdate";
	static final String PAYMENTMODE = "paymentmode";


	public static final int OUTOFZONE_ID = 10;
	public static final int NEW_TRIP_ID = 11;
	public static final int TRIP_RECIVE_MSG_ID = 12;

}
