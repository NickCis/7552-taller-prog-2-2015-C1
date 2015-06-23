/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import static android.content.Context.NOTIFICATION_SERVICE;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DELETEService;
import model.GETService;
import model.POSTService;
import model.ServerResultReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import services.AppController;
import whatsapp.client.ActiveConversationsActivity;
import whatsapp.client.ConversationActivity;
import whatsapp.client.R;
import whatsapp.client.UsersActivity;

/**
 *
 * @author rburdet
 */
public class Alarm extends BroadcastReceiver implements ServerResultReceiver.Listener {

	NotificationManager notificationManager;
	DatabaseHelper dbh ;
	private Context context;

	String idDelUltimo;

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Put here YOUR code.
		//TODO: sacarlo de aca
		startService(this.context = context);

		wl.release();
	}

	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Alarm.class);
		dbh = new DatabaseHelper(context);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi); // Millisec * Second * Minute
	}

	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, Alarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	/**
	 * Crea servicio que va a pedir notificaciones 
	 * @param context contexto donde se ejecuta
	 */
	private void startService(Context context) {
		Intent intent = new Intent(context, GETService.class);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		receiver.setListener(this);
		Bundle bundle = createBundle(context);
		intent.putExtra("rec", receiver);
		intent.putExtra("info", bundle);
		context.startService(intent);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		JSONObject data = null;
		try {
			data = new JSONObject(resultData.getString("data"));
		} catch (JSONException ex) {
			Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
		}
		processNotifications(data);
	}

	private Bundle createBundle(String endpoint, HashMap params){
		Bundle bundle = new Bundle();
		String access_token = ConfigurationManager.getInstance().getString(this.context, ConfigurationManager.ACCESS_TOKEN);
		params.put("access_token", access_token);
		String ip = ConfigurationManager.getInstance().getString(this.context, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this.context, ConfigurationManager.SAVED_PORT);
		final String URI = ip + ":" + port + "/" + endpoint;
		bundle.putString("URI", URI);
		bundle.putSerializable("params", params);
		return bundle;
	}

	private Bundle createBundle(Context context) {
		Bundle bundle = new Bundle();
		String access_token = ConfigurationManager.getInstance().getString(context, ConfigurationManager.ACCESS_TOKEN);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		String ip = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_PORT);
		final String URI = ip + ":" + port + "/" + "notification";
		bundle.putString("URI", URI);
		bundle.putSerializable("params", params);
		return bundle;
	}

	private void processNotifications(JSONObject data) {
		JSONArray notifications;
		if (data != null) {
			if (data.has("notifications")) {
				try {
					notifications = data.getJSONArray("notifications");
					if (notifications.length() != 0) {
						for (int i = 0; i < notifications.length(); i++) {
							processNotification(notifications.getJSONObject(i));
						}
						startServiceDelete(context);
					}

				} catch (JSONException ex) {
					Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
		}
	}

	private void processNotification(JSONObject data) {
		String type = null;
		try {
			type = data.getString("type");
			idDelUltimo = data.getString("id");

		} catch (JSONException ex) {
		}
		if (type.equals("message")) {
			processMessage(data);
		} else if (type.equals("ack")) {
			processAck(data);
		} else if (type.equals("profile")){
			processProfileChanged(data);
		} else if (type.equals("avatar")){
			processAvatarChanged(data);
		}
	}

	private void processAvatarChanged(JSONObject data){
		String username = "";
		try{
			data = data.getJSONObject("data");
			username = data.getString("username");
			fetchAvatar(username);
		}catch(JSONException jsone) {}
	}

	private void fetchAvatar(final String username){
		String ip = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_PORT);
		String access_token = ConfigurationManager.getInstance().getString(context, ConfigurationManager.ACCESS_TOKEN);
		String URI = ip + ":" + port + "/user/" + username +"/avatar?access_token="+access_token;
		//send("user/"+username+"/avatar",null,2,1);
		ImageRequest imreq = new ImageRequest(URI, new Response.Listener<Bitmap>() {
			public void onResponse(Bitmap t) {
				updateAvatar(username,t);
				if (UsersActivity.getInstance()!=null)
					if (UsersActivity.getInstance().isShowing())
						UsersActivity.getInstance().updateView(username);
			}
		}, 0,0, null, null);
		AppController.getInstance().addToRequestQueue(imreq);
	}


	private void processProfileChanged(JSONObject data){
		String username = "";
		try{
			data = data.getJSONObject("data");
			username = data.getString("username");
			updateProfile(data);
			if (UsersActivity.getInstance()!= null)
				if (UsersActivity.getInstance().isShowing())
					UsersActivity.getInstance().updateView(username);
			dbh.close();
		}catch(JSONException ex){}
	}

	private void updateAvatar(String username,Bitmap b){
		
		dbh = new DatabaseHelper(context);
		dbh.open();
		UserEntity fetchUser = dbh.fetchUser(username);
		fetchUser.setAvatar(b);
		dbh.updateUser(fetchUser);
		dbh.close();
	}


	private void updateProfile(JSONObject data){
		try{
			dbh = new DatabaseHelper(context);
			dbh.open();
			String username = data.getString("username");
			UserEntity user = dbh.fetchUser(username);
			String nickname = data.getString("nickname");
			boolean connected = data.getBoolean("online");
			long lastActivity = data.getLong("last_activity");
			JSONObject status = data.getJSONObject("status");
			long lastStatus = status.getLong("time");
			String statusText = status.getString("text");
			user.setNickname(nickname);
			user.setStatus(connected ? DatabaseHelper.CONNECTED : DatabaseHelper.DISCONNECTED);
			dbh.updateUser(user);
			dbh.close();
		}catch(JSONException ex){}
	}

	private void processAck(JSONObject data) {
		//TODO: Hacer esto ! 
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void processMessage(JSONObject data) {
		try {
			write(data.getJSONObject("data"));
			markArrived(data.getJSONObject("data"));
			if (ConversationActivity.isShowing()) {
				//ConversationActivity.getInstance().addMsgs(ce);
				String txt = data.getJSONObject("data").getString("message");
				ConversationActivity.getInstance().addMsgs(txt);
				
			}
			else if (ActiveConversationsActivity.isShowing()){
				ActiveConversationsActivity.getInstance().informNuevo(data.getJSONObject("data").getString("from"));
			}
			else {
				sendNotification(data);
			}
			
		} catch (JSONException ex) {
			Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void markArrived(JSONObject data){
		try{
		HashMap<String,String> params = new HashMap<String,String>();
		String id = data.getString("id");
		params.put("id[]",id);
		String from = data.getString("from");
		String endpoint = "user/" + from + "/messages/arrived";
		startService(endpoint,params,new ArrivedMessageReceiver(), POSTService.class);
		}catch(JSONException ex){}

	}
	
	private void startService(String endpoint, HashMap params, ServerResultReceiver.Listener listener, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		if (listener == null)
			receiver.setListener(this);
		else
			receiver.setListener(listener);
		Bundle bundle = createBundle(endpoint,params);
		intent.putExtra("rec", receiver);
		intent.putExtra("info", bundle);
		context.startService(intent);
	}

	private void startService(String endpoint, HashMap params , Class<?> cls) {
		startService(endpoint,params,this,cls);
	}

	private void sendNotification(JSONObject data) {
		String from = "";
		String txt = "";
		try {
			from = data.getJSONObject("data").getString("from");
			txt = data.getJSONObject("data").getString("message");
		} catch (JSONException ex) {
			Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
		}
		Intent notificationIntent = new Intent(context, ConversationActivity.class);
		notificationIntent.putExtra("id", from);
		notificationIntent.putExtra("msg", txt);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.notification_icon);
		builder.setContentIntent(pendingIntent);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon));
		builder.setTicker("Mensaje de: " + from);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		builder.setContentTitle("WAZAP");
		builder.setContentText(from + ": " + txt);

		Notification n = builder.build();

		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(1, n);
	}

		private void startServiceDelete(Context context) {
		Intent intent = new Intent(context, DELETEService.class);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		receiver.setListener(this);

		Bundle bundle = new Bundle();
		String access_token = ConfigurationManager.getInstance().getString(context, ConfigurationManager.ACCESS_TOKEN);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		params.put("id", idDelUltimo);

		String ip = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_PORT);
		final String URI = ip + ":" + port + "/" + "notification";
		bundle.putString("URI", URI);
		bundle.putSerializable("params", params);

		intent.putExtra("rec", receiver);
		intent.putExtra("info", bundle);
		context.startService(intent);
	}

	private ConversationEntity write(JSONObject data) {
		dbh = new DatabaseHelper(context);
		dbh.open();
		try {
			ConversationEntity ce = dbh.fetchConversation(data.getString("from"));
			UserEntity ue = dbh.fetchUser(data.getString("from"));
			//UserEntity ue = dbh.createUser(0, data.getString("from"), "nick", DatabaseHelper.NORMAL);
			dbh.createMessage(ce, ue, null, null,data.getString("message") , DatabaseHelper.NOT_SEEN);
			//Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, "ASDASD");
			return ce;

		} catch (JSONException ex) {
			Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private class ArrivedMessageReceiver implements ServerResultReceiver.Listener{
		public void onReceiveResult(int resultCode, Bundle resultData) {
			JSONObject data = null;
			try {
				data = new JSONObject(resultData.getString("data"));
				processArrived(data);
			} catch (JSONException ex) {
				Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		public void processArrived(JSONObject data) throws JSONException{
			data.getJSONArray("messages");
			//Parsear el tiempo, si esta leido, decirle a la vista q ponga doble tick
			//Si no tiene q poner 1 solo
		}
	
	}

}
