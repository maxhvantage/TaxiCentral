package com.taxicentral.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.taxicentral.R;

public class DialogManager {

    ProgressHUD mProgressHUD;
    public static boolean cancelable = false;

    @SuppressWarnings("deprecation")
    public void showAlertDialog(final Context context, String title, String message,
                                DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        //final Boolean localStatus = status;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
//		if(status != null)
//			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", ok);
        alertDialog.setButton("CANCLE", cancel);
        try {
            alertDialog.show();
        } catch (Exception e) {

        }
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(final Context context, String title, String message,
                                DialogInterface.OnClickListener ok) {
        //final Boolean localStatus = status;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setCancelable(cancelable);
//		if(status != null)
//			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", ok);
        try {
            alertDialog.show();
        } catch (Exception e) {

        }
    }

    public void showProcessDialog(Context context, String title) {
        try {
              /*
               * pDialog = new ProgressDialog(context); pDialog.setMessage(title);
		       * pDialog.setIndeterminate(false); pDialog.setCancelable(false); pDialog.show();
		       */

            mProgressHUD = ProgressHUD.show(context, title, true, true, new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    mProgressHUD.dismiss();

                }
            });
        } catch (Exception e) {
        }

    }

    public void stopProcessDialog() {
		    /*
		     * if (pDialog != null && pDialog.isShowing()) pDialog.dismiss();
		     */

        if (mProgressHUD != null && mProgressHUD.isShowing()) mProgressHUD.dismiss();

    }
}
