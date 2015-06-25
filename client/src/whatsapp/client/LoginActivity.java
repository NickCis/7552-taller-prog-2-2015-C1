package whatsapp.client;

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

/**
 * Activity inicial, permite el logeo y cambio de IP PORT
 * @author rburdet
 */
public class LoginActivity extends Activity implements ServerResultReceiver.Listener {
	
	private static final String REGISTERED = "registered";
	static final String USER_CFG = "user_cfg";
	static final String SAVED_IP = "IP";
	static final String SAVED_PORT = "PORT";
	static final String ACCESS_TOKEN = "access_token";
	
	static void storeAccessToken(Context ctx, String data) {
		SharedPreferences store = ctx.getSharedPreferences(LoginActivity.USER_CFG, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putString(LoginActivity.ACCESS_TOKEN, data);
		editor.apply();
	}
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: Si el usuario ya se registro tendria que recordar ese usuario
		//if (!isUserRegistered())
		setContentView(R.layout.login);
		EditText ipEdit = (EditText) findViewById(R.id.ipEditText);
		ipEdit.setText(getDefaultIP());
		EditText portEdit = (EditText) findViewById(R.id.portEditText);
		portEdit.setText(getDefaultPort());
		
		//startService(new Intent(this,NotificationService.class));
		EditText editText = (EditText) findViewById(R.id.userpassword);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
				if(actionId == EditorInfo.IME_ACTION_DONE){
					login();
					return true;
				}
				return false;
			}
		});
	}
	
	public void register(View v) {
		String newIp = getIP();
		String newPort = getPort();
		saveData(SAVED_IP, newIp);
		saveData(SAVED_PORT, newPort);
		startActivity(new Intent(this, RegisterActivity.class));
	}
	
	public void login(View v) {
		login();
	}

	public void login() {
		//TODO: verificar campos
		Bundle bundle = new Bundle();
		saveData(SAVED_IP, getIP());
		saveData(SAVED_PORT, getPort());
		EditText userNameField = (EditText) findViewById(R.id.username);
		EditText passwordField = (EditText) findViewById(R.id.userpassword);
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("user", userNameField.getText().toString());
		params.put("pass", passwordField.getText().toString());
		bundle.putSerializable("params", params);
		final String URI = getIP() + ":" + getPort() + "/" + "auth";
		bundle.putString("URI", URI);
		//TODO: esto es auth
		DialogFactory.createProgressDialog(this, "Conectandose... por favor espere");
		Log.i("Auth", "Haciendo login");
		startService(createCallingIntent(bundle));
	}
	
	private Intent createCallingIntent(Bundle bundle) {
		Intent intent = new Intent(this, POSTService.class);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		receiver.setListener(this);
		intent.putExtra("rec", receiver);
		intent.putExtra("info", bundle);
		return intent;
	}
	
	/**
	 * Prueba si el usuario ya se registro alguna vez
	 *
	 * return true si el usuario no fue registrado, false si ya se registro
	 * alguna vez
	 */
	private boolean isUserRegistered() {
		SharedPreferences data = getSharedPreferences(REGISTERED, 0);
		return data.getBoolean(REGISTERED, false);
	}
	
	/**
	 * Setea un flag de que el usuario ya fue registrado
	 */
	private final void registerUser() {
		SharedPreferences data = getSharedPreferences(REGISTERED, 0);
		SharedPreferences.Editor editor = data.edit();
		editor.putBoolean(REGISTERED, true);
	}

	/**
	 * Sincroniza la informacion de perfil, es decir, baja foto y esas cosas.
	 * Vamos a suponer que nunca va a fallar.
	 */
	public void syncProfile() {
		Log.d("WhatsappClient", "Sincronizo profile");
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		DatabaseHelper dbh = DatabaseHelper.getInstance(this);
		dbh.open();
		String username = dbh.getUserMe().getUsername();
		dbh.close();
		final String URI = getIP() + ":" + getPort() + "/user/"+username+"/profile?access_token="+access_token;
		final Context ctx = this;
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
					Log.e("WhatsappClient", "syncProfile :: Errorrr obteniendo el perfil! "+error.toString()+" msg: "+error.getMessage()+" cause: "+error.getCause());
				}
			}
		);
		AppController.getInstance().addToRequestQueue(req);
		syncAvatar();
	}

	/** Sincroniza el avatar.
	 * Obtiene el avatar que tiene el usuario al momento del login
	 */
	//TODO: sacar de aca, se usa tmb en UsersActivity
	private void syncAvatar(){
		final Context ctx = this;
		Log.d("WhatsappClient", "Sincronizo profile");
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		DatabaseHelper dbh = DatabaseHelper.getInstance(this);
		dbh.open();
		String username = dbh.getUserMe().getUsername();
		dbh.close();
		String URI = getIP() + ":" + getPort() + "/user/" + username +"/avatar?access_token="+access_token;

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
				Log.e("WhatsappClient", "syncAvatar :: Error buscando avatar "+error.toString()+" msg: "+error.getMessage()+" cause: "+error.getCause());
			}
		});
		AppController.getInstance().addToRequestQueue(imreq);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		DialogFactory.disposeDialog();
		if (resultCode == 0) {
			Log.i("Auth", "Exito haciendo login");
			JSONObject data = null;
			try {
				data = new JSONObject(resultData.getString("data"));
				String dataString = data.getString("access_token");
				DatabaseHelper dbH = DatabaseHelper.getInstance(this);
				dbH.open();
				dbH.login(((EditText) findViewById(R.id.username)).getText().toString());
				dbH.close();
				LoginActivity.storeAccessToken(this, dataString);
			} catch (JSONException ex) {
				Logger.getLogger(LoginActivity.class.getName()).log(Level.SEVERE, null, ex);
			}
			saveUser();
			syncProfile();
			startActivity(new Intent(this, MainActivity.class));
		} else {
			DialogFactory.createAlertDialog(this, "Problema ingresando",
				"Verifique sus datos, en caso de no tener un usuario por favor registrese");
			Log.e("Auth", "Error haciendo login");
		}
	}
	
	private String getDefaultIP() {
		String IP = getResources().getString(R.string.IP);
		SharedPreferences data = getSharedPreferences(USER_CFG, 0);
		if (data != null) {
			IP = data.getString(SAVED_IP, IP);
		}
		return IP;
	}
	
	public String getIP() {
		String IP = getDefaultIP();
		EditText edit = (EditText) findViewById(R.id.ipEditText);
		String newIp = edit.getText().toString();
		if (!newIp.equalsIgnoreCase(IP)) {
			saveData(SAVED_IP, newIp);
			return newIp;
		}
		return IP;
	}
	
	private void saveData(String key, String value) {
		SharedPreferences data = getSharedPreferences(USER_CFG, 0);
		SharedPreferences.Editor editor = data.edit();
		editor.putString(key, value);
		editor.apply();
	}
	
	private String getDefaultPort() {
		String port = getResources().getString(R.string.PORT);
		SharedPreferences data = getSharedPreferences(USER_CFG, 0);
		if (data != null) {
			port = data.getString(SAVED_PORT, port);
		}
		return port;
	}
	
	private String getPort() {
		String port = getDefaultPort();
		EditText edit = (EditText) findViewById(R.id.portEditText);
		String newPort = edit.getText().toString();
		if (!newPort.equalsIgnoreCase(port)) {
			saveData(SAVED_PORT, newPort);
			return newPort;
		}
		return port;
	}
	
	
	private void saveUser(){
		EditText userNameField = (EditText) findViewById(R.id.username);
		storeUserName(this, userNameField.getText().toString());
	}
	
	static void storeUserName(Context ctx, String data) {
		SharedPreferences store = ctx.getSharedPreferences(LoginActivity.USER_CFG, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putString(ConfigurationManager.USERNAME, data);
		editor.apply();
	}
	
}
