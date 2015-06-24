/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Encargado de empezar el servicio de notificaciones
 * @author rburdet
 */
public class NotificationServiceStarter extends Service{
	services.NotificationService alarm = new services.NotificationService();
	public void onCreate(){
		super.onCreate();
		Log.i("NotificationServiceStarter", "creando servicio de notificaciones");
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i("NotificationServiceStarter", "Empezando servicio de notificaciones");
		alarm.setAlarm(NotificationServiceStarter.this);
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
}