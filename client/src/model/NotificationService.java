/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import utils.Alarm;
/**
 *
 * @author rburdet
 */
public class NotificationService extends Service{
	Alarm alarm = new Alarm();
	public void onCreate(){		
		super.onCreate();
        Log.d("NotificationService", "on create---------------------------");
		//alarm.SetAlarm(NotificationService.this);
		
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("NotificationService", "on start command -------------------");
		alarm.setAlarm(NotificationService.this);
         return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}