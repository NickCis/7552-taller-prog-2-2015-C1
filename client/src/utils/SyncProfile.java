package utils;

import model.POSTService;
import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ConfigurationManager;
import utils.DatabaseHelper;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import model.CustomRequest;
import services.AppController;
import com.android.volley.toolbox.ImageRequest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class SyncProfile {
	static protected String getPort(final Context ctx){
		return ConfigurationManager.getInstance().getString(ctx, ConfigurationManager.SAVED_PORT);
	}

	static protected String getIP(final Context ctx){
		return ConfigurationManager.getInstance().getString(ctx, ConfigurationManager.SAVED_IP);
	}

	/**
	 * Sincroniza la informacion de perfil, es decir, baja foto y esas cosas.
	 * Vamos a suponer que nunca va a fallar.
	 */
	static public void syncOwnProfile(final Context ctx) {
		Log.d("WhatsappClient", "SyncProfile.syncOwnProfile Sincronizo profile");
		String access_token = ConfigurationManager.getInstance().getString(ctx, ConfigurationManager.ACCESS_TOKEN);
		DatabaseHelper dbh = DatabaseHelper.getInstance(ctx);
		dbh.open();
		String username = dbh.getUserMe().getUsername();
		dbh.close();
		final String URI = getIP(ctx) + ":" + getPort(ctx) + "/user/"+username+"/profile?access_token="+access_token;
		CustomRequest req = new CustomRequest(URI, null,
			new Listener<JSONObject>(){
				public void onResponse(JSONObject response) {
					Log.i("WhatsappClient", "respuesta de profile :: "+response.toString());
					DatabaseHelper dbh = DatabaseHelper.getInstance(ctx);
					dbh.open();
					try {
						String nickname = response.getString("nickname");
						boolean online  = response.getBoolean("online");
						String status = response.getJSONObject("status").getString("text");
						dbh.getUserMe().setNickname(nickname);
						dbh.getUserMe().setStatusMessage(status);
						dbh.getUserMe().setStatus(online ? DatabaseHelper.STATUS_ONLINE : DatabaseHelper.STATUS_OFFLINE);
						dbh.updateUser(dbh.getUserMe());
					}catch(Exception e){}
					dbh.close();
				}
			},
			new ErrorListener(){
				public void onErrorResponse(VolleyError error){
					Log.e("WhatsappClient", "SyncProfile.syncOwnProfile :: Errorrr obteniendo el perfil! "+error.toString()+" msg: "+error.getMessage()+" cause: "+error.getCause());
				}
			}
		);
		AppController.getInstance().addToRequestQueue(req);
		syncOwnAvatar(ctx);
	}

	/** Sincroniza el avatar.
	 * Obtiene el avatar que tiene el usuario al momento del login
	 */
	static private void syncOwnAvatar(final Context ctx){
		Log.d("WhatsappClient", "SyncProfile.syncOwnAvatar Sincronizo profile");
		String access_token = ConfigurationManager.getInstance().getString(ctx, ConfigurationManager.ACCESS_TOKEN);
		DatabaseHelper dbh = DatabaseHelper.getInstance(ctx);
		dbh.open();
		String username = dbh.getUserMe().getUsername();
		dbh.close();
		String URI = getIP(ctx) + ":" + getPort(ctx) + "/user/" + username +"/avatar?access_token="+access_token;

		ImageRequest imreq = new ImageRequest(URI, new Response.Listener<Bitmap>() {
			public void onResponse(Bitmap t) {
				DatabaseHelper dbh = DatabaseHelper.getInstance(ctx);
				dbh.open();
				dbh.getUserMe().setAvatar(t);
				dbh.updateUser(dbh.getUserMe());
				dbh.close();
			}
		}, 0,0, null, new Response.ErrorListener() {

			public void onErrorResponse(VolleyError error) {
				Log.e("WhatsappClient", "SyncProfile.syncOwnAvatar :: Error buscando avatar "+error.toString()+" msg: "+error.getMessage()+" cause: "+error.getCause());
			}
		});
		AppController.getInstance().addToRequestQueue(imreq);
	}
}
