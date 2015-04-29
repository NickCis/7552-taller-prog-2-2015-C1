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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import model.GETService;
import model.POSTService;
import model.ServerResultReceiver;
import whatsapp.client.LoginActivity;
import whatsapp.client.R;

/**
 *
 * @author rburdet
 */
public class Alarm extends BroadcastReceiver implements ServerResultReceiver.Listener{

	NotificationManager notificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Put here YOUR code.
		startService(context);

		/*
		Intent notificationIntent = new Intent(context, LoginActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.notification_icon);
		builder.setContentIntent(pendingIntent);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon));
		builder.setTicker("SOY UN TICKER");
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		builder.setContentTitle("titulo");
		builder.setContentText("texto content");

		Notification n = builder.build();

		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(1, n);
		//Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_SHORT).show(); // For example
*/
		wl.release();
	}

	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi); // Millisec * Second * Minute
	}

	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, Alarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

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
		Log.d("ALARM", "ESTOY EN LA VUELTA DEL GET DE NOTIFICATION-*******************************************************************************************");
	}

	private Bundle createBundle(Context context) {
		Bundle bundle = new Bundle();
		String ip = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(context, ConfigurationManager.SAVED_PORT);
		final String URI = ip+ ":" + port + "/" + "notification";
		bundle.putString("URI", URI);
		return bundle;
	}
}
