/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.ProgressDialog;
import android.content.Context;

/**
 *
 * @author rburdet
 */
public class InfoDialog {
	private static ProgressDialog dialog;
	
	public static ProgressDialog createProgressDialog(Context ctx, String msg){
		dialog = new ProgressDialog(ctx);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
		return dialog;
	}
	
	public static void disposeDialog(){
		dialog.dismiss();
	}
	
	
	
	
}
