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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DELETEService;
import model.GETService;
import model.ServerResultReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import whatsapp.client.ActiveConversationsActivity;
import whatsapp.client.ConversationActivity;
import whatsapp.client.R;

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
		}
	}

	private void processAck(JSONObject data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void processMessage(JSONObject data) {
		if (ConversationActivity.isShowing()) {
			try {
				//ConversationActivity.getInstance().addMsgs(ce);
				String txt = data.getJSONObject("data").getString("message");
				ConversationActivity.getInstance().addMsgs(txt);
			} catch (JSONException ex) {
				Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
			}

		} 
		else if (ActiveConversationsActivity.isShowing()){
			try {
				write(data.getJSONObject("data"));
				ActiveConversationsActivity.getInstance().informNuevo(data.getJSONObject("data").getString("from"));
			} catch (JSONException ex) {
				Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
			}
			
		}
		else {
			sendNotification(data);
		}
	}

	private void sendNotification(JSONObject data) {
		String from = "";
		String txt = "";
		try {
			from = data.getJSONObject("data").getString("from");
			txt = data.getJSONObject("data").getString("message");
			write(data.getJSONObject("data"));

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
		//Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_SHORT).show(); // For example

	}

	//private void write(JSONObject data) {
	//	try {
	//		SharedPreferences store = context.getSharedPreferences("pepe", 0);
	//		SharedPreferences.Editor editor = store.edit();

	//		Set<String> set = store.getStringSet("messages", new LinkedHashSet<String>());
	//		set.add(data.getString("message"));
	//		/*editor.putString("from", data.getString("from"));
	//		 editor.putString("id", data.getString("id"));
	//		 editor.putString("time",data.getString("time"));
	//		 */
	//		editor.putStringSet("messages", set);
	//		editor.apply();
	//	} catch (JSONException ex) {
	//		Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
	//	}
	//}

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
		/*
		para hacer un fetch de la conversacion necesito el usuario. 
		si me llega un id de usuario tendria que crear el usuario en la bas de datos y ahi con eso lo q estaria haciedno es agregarlo a la lista de usuarios


		
		creo entity de usuario  con el username
		creo una conversation entity y le agrego la entity usuario
		capazq tmb tenga q agregar el userMe  
		y dsp hago un fetch

		puedo hacer un fetch conversacion del user

		
		*/
		try {
			ConversationEntity ce = dbh.fetchConversation(data.getString("from"));
			UserEntity ue = dbh.createUser(0, data.getString("from"), "nick", DatabaseHelper.NORMAL);
			dbh.createMessage(ce, ue, null, null,data.getString("message") , DatabaseHelper.NOT_SEEN);
			//Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, "ASDASD");
			return ce;

		} catch (JSONException ex) {
			Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

}
