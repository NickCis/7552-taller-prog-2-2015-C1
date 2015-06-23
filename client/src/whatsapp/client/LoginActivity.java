package whatsapp.client;

import model.POSTService;
import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import utils.DatabaseHelper;

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
	}

	public void register(View v) {
		String newIp = getIP();
		String newPort = getPort();
		saveData(SAVED_IP, newIp);
		saveData(SAVED_PORT, newPort);
		startActivity(new Intent(this, RegisterActivity.class));
	}

	public void login(View v) {
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
		DialogFactory.createProgressDialog(this, "Registrando... por favor espere");
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

	public void onReceiveResult(int resultCode, Bundle resultData) {
		//Si fue correcto:
//		if (resultCode == 0) {
//			registerUser();
//		}
//		registerUser();

		DialogFactory.disposeDialog();
		if (resultCode == 0) {
			JSONObject data = null;
			try {
				data = new JSONObject(resultData.getString("data"));
				String dataString = data.getString("access_token");
                                DatabaseHelper dbH = DatabaseHelper.getInstance(this);
                                dbH.open();
                                dbH.login(dataString);
                                dbH.close();
				LoginActivity.storeAccessToken(this, dataString);
			} catch (JSONException ex) {
				Logger.getLogger(LoginActivity.class.getName()).log(Level.SEVERE, null, ex);
			}
			startActivity(new Intent(this, MainActivity.class));
		} else {
			DialogFactory.createAlertDialog(this, "Problema ingresando",
					"Verifique sus datos, en caso de no tener un usuario por favor registrese",
					LoginActivity.class
			);
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

}
