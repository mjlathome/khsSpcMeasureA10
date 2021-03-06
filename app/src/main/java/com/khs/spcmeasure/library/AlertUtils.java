package com.khs.spcmeasure.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.khs.spcmeasure.R;

/**
 * @author Mark
 *
 */
public class AlertUtils {

	public static AlertDialog.Builder createAlert(Context context, String title, String message) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);				
		dlgAlert.setTitle(title);
		dlgAlert.setMessage(message);
		dlgAlert.setCancelable(false);
		return dlgAlert;		
	}
	
	public static AlertDialog.Builder createAlert(Context context, String message) {
		return createAlert(context, context.getString(R.string.text_error), message);
	}	
	
	// generate alert dialog with the provided title and message
	public static void alertDialogShow(Context context, String title, String message)
    {
		AlertDialog.Builder dlgAlert = createAlert(context, title, message);				
//		dlgAlert = new AlertDialog.Builder(context);				
//		dlgAlert.setTitle(title);
//		dlgAlert.setMessage(message);
//		dlgAlert.setCancelable(false);
		dlgAlert.setPositiveButton(context.getString(R.string.text_okay), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dismiss the dialog					
			}
		}); 				
		dlgAlert.create().show();        
    }
	
	// generate error dialog with the provided title and message
	public static void errorDialogShow(Context context, String message)
    {
		alertDialogShow(context, context.getString(R.string.text_error), message);
    }
	
}
