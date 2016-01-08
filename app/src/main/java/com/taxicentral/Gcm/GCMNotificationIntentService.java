package com.taxicentral.Gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.taxicentral.Activity.AlertDialogActivity;
import com.taxicentral.Activity.NavigationDrawer;
import com.taxicentral.Activity.NotificationActivity;
import com.taxicentral.Activity.RecivedMessage;
import com.taxicentral.Activity.TaxiWaitingActivity;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.NotificationModel;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Function;

import org.json.JSONException;
import org.json.JSONObject;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
    DBAdapter db;
	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		db = new DBAdapter(GCMNotificationIntentService.this);
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				String registerMessage = 	""+extras.get(Config.LOGIN);
				String tripCancel = 	""+extras.get(Config.TRIP_CANCEL);
				String reciveMessage = ""+extras.get(Config.TRIP_RECIVE_MSG);
				String boarded = ""+extras.get(Config.TRIP_BOARDED);

				Log.d("NOTIFICATION ","registerMessage:"+ registerMessage +" tripCancel: "+ tripCancel +" reciveMessage: "+reciveMessage+" boarded: "+boarded);
				if(!registerMessage.equalsIgnoreCase("null")){
					//sendNotification(registerMessage);
				}else if(!tripCancel.equalsIgnoreCase("null")){
					tripCancelNotify(tripCancel);
				}else if(!reciveMessage.equalsIgnoreCase("null")){

					tripReciveMessage(reciveMessage);
				}else if(!boarded.equalsIgnoreCase("null")){
					tripBoardedNotify(boarded);
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void tripBoardedNotify(String boarded) {

		final Handler mHandler = new Handler(Looper.getMainLooper());

		try {
			JSONObject jsonObject = new JSONObject(boarded);
			String alreadyBarded = jsonObject.getString("message");
			if(alreadyBarded.equalsIgnoreCase("yes") && TaxiWaitingActivity.instance != null){

			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							TaxiWaitingActivity.fabBoarded.performClick();
						}
					});

				}
			}).start();

			}else{

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	private void tripReciveMessage(String reciveMessage) {
		try {
			JSONObject jsonObject = new JSONObject(reciveMessage);

			sendNotification(jsonObject.getString("message"));

			Intent intent = new Intent(this, RecivedMessage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("message", jsonObject.getString("message"));
			startActivity(intent);


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void tripCancelNotify(String tripCancel){
		AppPreferences.setTripId(this, "");

		try {
			JSONObject jsonObject = new JSONObject(tripCancel);

			NotificationModel notification = new NotificationModel();
			notification.setId(Integer.parseInt(jsonObject.getString("trip_id")));
			notification.setHeader("Trip Canceled");
			notification.setDescription("Trip Id : " + jsonObject.getString("trip_id") + "\n" + jsonObject.getString("canceltaxirequest"));
			db.insertNotification(notification);

			sendNotification("Your trip has been cancel");

			Intent intent = new Intent(this, NavigationDrawer.class);
			intent.putExtra("notificationData", tripCancel);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, NotificationActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Taxi Central")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setDefaults(Notification.DEFAULT_ALL)
				.setContentText(msg);


		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		mNotificationManager.cancel(NOTIFICATION_ID);
	}


	public void showDialog(Context context){
		final String[] message = {getString(R.string.i_am_waiting_out_side)};
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		final View dialogView = inflater.inflate(R.layout.send_message_for_waiting, null);
		dialogBuilder.setView(dialogView);
		View header = inflater.inflate(R.layout.dialog_heading, null);
		ImageView icon = (ImageView) header.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.ic_action_send);
		TextView textView = (TextView) header.findViewById(R.id.text);
		textView.setText(R.string.send_message);
		dialogBuilder.setCustomTitle(header);
		//dialogBuilder.setTitle(R.string.send_message);
		//dialogBuilder.setIcon(R.drawable.ic_action_send);
		dialogBuilder.setCancelable(false);

		final AlertDialog alertDialog = dialogBuilder.create();

		final RadioGroup mRadioGroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
		Button accept = (Button) dialogView.findViewById(R.id.btn_accept);
		Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
				message[0] = radioButton.getText().toString();
			}
		});

		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				alertDialog.dismiss();
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}


}
