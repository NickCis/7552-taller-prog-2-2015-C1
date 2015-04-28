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
 * Clase factory de dialogos informativos
 * @author rburdet
 */
public class InfoDialog {
	private static ProgressDialog dialog;
	private static AlertDialog alertDialog;
	
	/**
	 * Crea un dialogo con un infinite progress en un contexto dado con un mensaje a eleccion
	 * @param ctx contexto en el cual se quiere mostrar el dialogo
	 * @param msg texto dentro del mensaje
	 * @return dialogo creado
	 */
	public static ProgressDialog createProgressDialog(Context ctx, String msg){
		dialog = new ProgressDialog(ctx);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
		return dialog;
	}

	/**
	 * Crea un dialogo con un boton de aceptar, el dialogo se crea en un contexto dado, con un titulo y un mensaje, cuando se aprete aceptar se va a dirigir a la activity definida en cls
	 * @param ctx contexto donde se genera el dialogo
	 * @param title titulo del dialogo
	 * @param msg mensaje a mostrar dentro del dialogo
	 * @param cls nombre del activity a ejecutar cuando se acepte el dialogo
	 * @return  dialogo creado
	 */
	public static AlertDialog createAlertDialog(final Context ctx, String title, String msg,final Class<?> cls){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int i) {
				di.dismiss();
				if (cls!=null)
					ctx.startActivity(new Intent(ctx, cls));
			}
		});
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		return alertDialog;
	}

	/**
	 * Crea un dialogo con un boton de aceptar, el dialogo se crea en un contexto dado, con un titulo y un mensaje
	 * @param ctx contexto donde se genera el dialogo
	 * @param title titulo del dialogo
	 * @param msg mensaje a mostrar dentro del dialogo
	 * @return  dialogo creado
	 */
	
	public static AlertDialog createAlertDialog(final Context ctx, String title, String msg){
		return createAlertDialog(ctx, title, msg, null);
	}
	
	/**
	 * Cierra el dialogo q se este mostrando
	 */
	public static void disposeDialog(){
		if (dialog!=null && dialog.isShowing())
			dialog.dismiss();
	}
	
	
	
	
	
}
