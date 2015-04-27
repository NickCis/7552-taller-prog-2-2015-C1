/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 *
 * @author rburdet
 */
public class InfoDialog {
	private static ProgressDialog dialog;
	private static AlertDialog alertDialog;
	
	public static ProgressDialog createProgressDialog(Context ctx, String msg){
		dialog = new ProgressDialog(ctx);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
		return dialog;
	}
	public static AlertDialog createAlertDialog(final Context ctx, String title, String msg,final Class<?> cls){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int i) {
				di.dismiss();
				ctx.startActivity(new Intent(ctx, cls));

			}
		});
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		return alertDialog;
	}
	
	public static void disposeDialog(){
		if (dialog!=null && dialog.isShowing())
			dialog.dismiss();
	}
	
	
	
	
	
}
