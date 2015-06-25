/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import static android.content.Context.NOTIFICATION_SERVICE;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import utils.Checkin;
import utils.ConfigurationManager;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.MessageEntity;
import utils.UserEntity;
import whatsapp.client.ActiveConversationsActivity;
import whatsapp.client.ConversationActivity;
import whatsapp.client.R;
import whatsapp.client.UsersActivity;

/**
 * Servicio de notificaciones, se despierta cada x tiempo para hacer un request
 * @author rburdet
 */
public class NotificationService extends BroadcastReceiver implements ServerResultReceiver.Listener {
	
	NotificationManager notificationManager;
	DatabaseHelper dbH ;
	private Context context;
	
	String idDelUltimo;
	
	/**
	 * Despierta el servicio de notificaciones
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		startService(this.context = context);
		
		wl.release();
	}
	
	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, NotificationService.class);
		dbH = DatabaseHelper.getInstance(context);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi); // Millisec * Second * Minute
	}
	
	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, NotificationService.class);
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
		Log.i("NotificationService", "Request de una notificacion");
		context.startService(intent);
	}
	
	/**
	 * Respuesta del servicio que se haya ejecutado, usado para procesar notificaciones
	 * @param resultCode
	 * @param resultData
	 */
	public void onReceiveResult(int resultCode, Bundle resultData) {
		Log.i("NotificationService", "Respuesta del servidor de una notificacion");
		JSONObject data = null;
		try {
			data = new JSONObject(resultData.getString("data"));
		} catch (JSONException ex) {
			Logger.getLogger(NotificationService.class.getName()).log(Level.SEVERE, null, ex);
		}
		processNotifications(data);
	}
	
	/**
	 * Crea un paquete para hacer el request
	 * @param endpoint endpoint a donde se quiere hablar
	 * @param params paramatros del request
	 * @return paquete con la informacion del request
	 */
	private Bundle createBundle(String endpoint, HashMap params){
		Bundle bundle = new Bundle();
		String access_token = ConfigurationManager.getInstance().getString(this.context, ConfigurationManager.ACCESS_TOKEN); params.put("access_token", access_token);
		String ip = ConfigurationManager.getInstance().getString(this.context, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this.context, ConfigurationManager.SAVED_PORT);
		final String URI = ip + ":" + port + "/" + endpoint;
		bundle.putString("URI", URI);
		bundle.putSerializable("params", params);
		return bundle;
	}
	
	/**
	 * Crea un paquete para hacer el request
	 * @param context contexto en donde se ejecuta
	 * @return paquete con la informacion del request
	 */
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
	
	/**
	 * Procesa las notificaciones y las borra una vez procesadas
	 * @param data Datos del servidor
	 */
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
					Logger.getLogger(NotificationService.class.getName()).log(Level.SEVERE, null, ex);
				}
				
			}
		}
	}
	
	
	/**
	 * Procesa una notificacion
	 * @param data Datos de una notificacion del servidor
	 */
	private void processNotification(JSONObject data) {
		String type = null;
		try {
			type = data.getString("type");
			idDelUltimo = data.getString("id");
			
		} catch (JSONException ex) {
		}
		if (type.equals("message")) {
			Log.i("NotificationService", "Llego un mensaje");
			processMessage(data);
		} else if (type.equals("ack")) {
			Log.i("NotificationService", "Llego un ack");
			processAck(data);
		} else if (type.equals("profile")){
			Log.i("NotificationService", "Cambiaron un profile");
			processProfileChanged(data);
		} else if (type.equals("avatar")){
			Log.i("NotificationService", "Cambiaron un avatar");
			processAvatarChanged(data);
		} else if (type.equals("checkin")){
			Log.i("NotificationService", "Hicieron checkin");
			processCheckin(data);
		}
	}
	
	private void processCheckin(JSONObject data){
		try{
			data = data.getJSONObject("data");
			//TODO: Esperar a que mati le ponga el campo name a Checkin
			String place = data.getString("name");
			Double lat = data.getDouble("latitude");
			Double longit = data.getDouble("longitude");
			Long time = data.getLong("time");
			String username = data.getString("username");
			DatabaseHelper dbh = DatabaseHelper.getInstance(context);
			dbh.open();
			UserEntity fetchUser = dbh.fetchUser(username);
			Checkin checkin = new Checkin(lat,longit );
			fetchUser.setCheckin(checkin);
			dbh.updateUser(fetchUser);
			dbh.close();
		}catch(JSONException ex){}
	}
	
	private void processAvatarChanged(JSONObject data){
		String username = "";
		try{
			data = data.getJSONObject("data");
			username = data.getString("username");
			fetchAvatar(username);
		}catch(JSONException jsone) {}
	}
	
	/**
	 * Busca avatar en el servidor
	 * @param username nombre de quien quiero el avatar
	 */
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
		}, 0,0, null, new Response.ErrorListener() {

			public void onErrorResponse(VolleyError ve) {
				Log.e("Avatar","Error actualizando avatar");
			}
		});
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
			dbH.close();
		}catch(JSONException ex){}
	}
	
	/**
	 * Actualiza el avatar en la bdd
	 * @param username username de quien tengo que actualizar
	 * @param b avatar
	 */
	private void updateAvatar(String username,Bitmap b){
		
		dbH = DatabaseHelper.getInstance(context);
		dbH.open();
		UserEntity fetchUser = dbH.fetchUser(username);
		fetchUser.setAvatar(b);
		dbH.updateUser(fetchUser);
		dbH.close();
	}
	
	
	/**
	 * Actualiza el perfil en la bdd
	 * @param data datos del servidor
	 */
	private void updateProfile(JSONObject data){
		try{
			dbH = DatabaseHelper.getInstance(context);
			dbH.open();
			String username = data.getString("username");
			UserEntity user = dbH.fetchUser(username);
			String nickname = data.getString("nickname");
			boolean connected = data.getBoolean("online");
			long lastActivity = data.getLong("last_activity");
			JSONObject status = data.getJSONObject("status");
			long lastStatus = status.getLong("time");
			String statusText = status.getString("text");
			user.setNickname(nickname);
			user.setStatus(connected ? DatabaseHelper.STATUS_ONLINE : DatabaseHelper.STATUS_OFFLINE);
			dbH.updateUser(user);
			dbH.close();
		}catch(JSONException ex){}
	}
	
	/**
	 *  Si me llega un ack con que leyo un mensaje, entonces marco todos como leidos
	 * @param data datos del servidor
	 */
	private void processAck(JSONObject data) {
		try{
			data.getJSONObject("data");
			long arrivedTime = data.getLong("arrived");
			if (arrivedTime!=0)
				markAllArrived(data);
			long readTime = data.getLong("read");
			if (readTime!=0){
				if (ConversationActivity.isShowing())
					ConversationActivity.getInstance().refresh();
				markAllRead(data);
			}
		}catch(JSONException ex){}
	}
	
	private void markAllArrived(JSONObject data) throws JSONException{
		String from = data.getString("from");
		DatabaseHelper dbh =  DatabaseHelper.getInstance(context);
		ConversationEntity ce = dbh.fetchConversation(from);
		UserEntity ue = ce.getUser(1);
		List<MessageEntity> fetchMessages = dbh.fetchMessages(ce);
		for (MessageEntity msg : fetchMessages){
			if (msg.getStatus() == DatabaseHelper.NOT_SENT)
				dbh.updateMessage(ce, ue, null, Calendar.getInstance(), msg.getContent(), DatabaseHelper.SENT);
		}
	}
	
	private void markAllRead(JSONObject data) throws JSONException{
		String from = data.getString("from");
		DatabaseHelper dbh =  DatabaseHelper.getInstance(context);
		ConversationEntity ce = dbh.fetchConversation(from);
		UserEntity ue = ce.getUser(1);
		List<MessageEntity> fetchMessages = dbh.fetchMessages(ce);
		for (MessageEntity msg : fetchMessages){
			if (msg.getStatus() == DatabaseHelper.NOT_SEEN)
				dbh.updateMessage(ce, ue, null, Calendar.getInstance(), msg.getContent(), DatabaseHelper.SEEN);
		}
	}
	
	private void processMessage(JSONObject data) {
		try {
			write(data.getJSONObject("data"));
			if (ConversationActivity.isShowing()) {
				//ConversationActivity.getInstance().addMsgs(ce);
				String txt = data.getJSONObject("data").getString("message");
				ConversationActivity.getInstance().addMsgs(txt);
				mark(data,true);
				
			}
			else if (ActiveConversationsActivity.isShowing()){
				ActiveConversationsActivity.getInstance().informNuevo(data.getJSONObject("data").getString("from"));
				mark(data,false);
			}
			else {
				sendNotification(data);
				mark(data,false);
			}
			
		} catch (JSONException ex) {
			Logger.getLogger(NotificationService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * Marca un mensaje como enviado o leido
	 * @param data mensaje llegado del servidor
	 * @param arrived true si fue leido
	 */
	private void mark(JSONObject data,boolean arrived){
		try{
			HashMap<String,String> params = new HashMap<String,String>();
			data = data.getJSONObject("data");
			String endpoint;
			String id = data.getString("id");
			params.put("id[]",id);
			String from = data.getString("from");
			if (!arrived)
				endpoint = "user/" + from + "/messages/arrived";
			else
				endpoint = "user/" + from + "/messages/read";
			startService(endpoint,params,new ArrivedMessageReceiver(), POSTService.class);
		}catch(JSONException ex){}
	}
	
	/**
	 * Empieza un servicio POST o GET a un endpoint con parametros params y un listener
	 * @param endpoint endpoint del servidor
	 * @param params parametros
	 * @param listener listener del callback
	 * @param cls POST.class o GET.class
	 */
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
	
	/**
	 * Empieza un servicio POST o GET a un endpoint con parametros params agrega a NotificationService como listener
	 * @param endpoint endpoint del servidor
	 * @param params parametros
	 * @param cls POST.class o GET.class
	 */
	private void startService(String endpoint, HashMap params , Class<?> cls) {
		startService(endpoint,params,this,cls);
	}
	
	
	/**
	 * Manda una notificacion al sistema operativo de que llego un mensaje y que sera abierto por MensajerO
	 * @param data datos del mensaje a mandar
	 */
	private void sendNotification(JSONObject data) {
		String from = "";
		String txt = "";
		try {
			from = data.getJSONObject("data").getString("from");
			txt = data.getJSONObject("data").getString("message");
		} catch (JSONException ex) {
			Logger.getLogger(NotificationService.class.getName()).log(Level.SEVERE, null, ex);
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
		builder.setContentTitle("MensajerO");
		builder.setContentText(from + ": " + txt);
		
		Notification n = builder.build();
		
		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(1, n);
	}
	
	/**
	 * Hace un request http DELETE
	 * @param context contexto donde se ejecuta
	 */
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
	
	/**
	 * Escribe en la base de datos un mensaje
	 * @param data informacion del mensaje a guardar
	 * @return Conversacion en la que se escribio, null si no se encuentra
	 */
	private ConversationEntity write(JSONObject data) {
		dbH = DatabaseHelper.getInstance(context);
		dbH.open();
		try {
			ConversationEntity ce = dbH.fetchConversation(data.getString("from"));
			UserEntity ue = dbH.fetchUser(data.getString("from"));
			//UserEntity ue = dbh.createUser(0, data.getString("from"), "nick", DatabaseHelper.NORMAL);
			dbH.createMessage(ce, ue, null, null,data.getString("message") , DatabaseHelper.NOT_SEEN);
			//Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, "ASDASD");
			return ce;
			
		} catch (JSONException ex) {
			Logger.getLogger(NotificationService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	/**
	 * Listener de recibo de ack de mensajes
	 */
	private class ArrivedMessageReceiver implements ServerResultReceiver.Listener{
		public void onReceiveResult(int resultCode, Bundle resultData) {
			JSONObject data = null;
			try {
				data = new JSONObject(resultData.getString("data"));
				processArrived(data);
			} catch (JSONException ex) {
				Logger.getLogger(NotificationService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		public void processArrived(JSONObject data) throws JSONException{
			data.getJSONArray("messages");
			//Parsear el tiempo, si esta leido, decirle a la vista q ponga doble tick
			//Si no tiene q poner 1 solo
		}
		
	}
	
}
