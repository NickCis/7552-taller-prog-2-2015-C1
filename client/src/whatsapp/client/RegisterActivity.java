/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.POSTService;
import model.ServerResultReceiver;
import org.json.JSONException;
import org.json.JSONObject;
import utils.DatabaseHelper;

/**
 *
 * @author rburdet
 */
public class RegisterActivity extends Activity implements ServerResultReceiver.Listener {

	private String URI, user, pass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: Si el usuario ya se registro tendria que recordar ese usuario
		//if (!isUserRegistered())
		setContentView(R.layout.register);

	}

	public void register(View v) {
		Bundle bundle = new Bundle();
		EditText userNameField = (EditText) findViewById(R.id.username);
		EditText passwordField = (EditText) findViewById(R.id.userpassword);
		EditText passWordValidateField = (EditText) findViewById(R.id.userpasswordValidate);
		String pass1 = passwordField.getText().toString();
		String pass2 = passWordValidateField.getText().toString();
		if (!pass1.equals(pass2)) {
			DialogFactory.createAlertDialog(this, "Contraseña incorrecta",
					"Las contraseñas no coinciden, vuelva a intentarlo",
					RegisterActivity.class
			);
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user", user = userNameField.getText().toString());
			params.put("pass", pass = passwordField.getText().toString());
			bundle.putSerializable("params", params);
			URI = getIP() + ":" + getPort() + "/" + "signup";
			bundle.putString("URI", URI);
			//TODO: esto es auth
			DialogFactory.createProgressDialog(this, "Registrando... por favor espere");
			startService(createCallingIntent(bundle));
		}
	}

	private Intent createCallingIntent(Bundle bundle) {
		Intent intent = new Intent(this, POSTService.class);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		receiver.setListener(this);
		intent.putExtra("rec", receiver);
		intent.putExtra("info", bundle);
		return intent;
	}

	private String getIP() {
		SharedPreferences data = getSharedPreferences(LoginActivity.USER_CFG, 0);
		String ip = getResources().getString(R.string.IP);
		if (data != null) {
			ip = data.getString(LoginActivity.SAVED_IP, ip);
		}
		return ip;
	}

	private String getPort() {
		SharedPreferences data = getSharedPreferences(LoginActivity.USER_CFG, 0);
		String port = getResources().getString(R.string.PORT);
		if (data != null) {
			port = data.getString(LoginActivity.SAVED_PORT, port);
		}
		return port;
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		DialogFactory.disposeDialog();

		if (resultCode == 0) {
			try {
				//me fijo si vino de auth o de register
				JSONObject data = new JSONObject(resultData.getString("data"));
				if (!data.has("access_token")) {
					Bundle bundle = new Bundle();
					HashMap<String, String> params = new HashMap<String, String>();

					params.put("user",user);
					params.put("pass", pass);
					bundle.putSerializable("params", params);
					URI = getIP() + ":" + getPort() + "/" + "auth";
					bundle.putString("URI", URI);
					DialogFactory.createProgressDialog(this, "Registrando... por favor espere");
					startService(createCallingIntent(bundle));
				} else {
					String dataString = data.getString("access_token");
                                        DatabaseHelper dbH = DatabaseHelper.getInstance(this);
                                        dbH.open();
                                        dbH.login(user);
                                        dbH.close();
					LoginActivity.storeAccessToken(this, dataString);
					LoginActivity.storeUserName(this, user);
					startActivity(new Intent(this, MainActivity.class));

				}

				/*if (!data.contains("access_token")) {
				 Bundle bundle = new Bundle();
				 bundle.putString("username", user);
				 bundle.putString("password", pass);
				 URI = getIP() + ":" + getPort() + "/" + "auth";
				 bundle.putString("URI", URI);
				 //TODO: esto es auth
				 InfoDialog.createProgressDialog(this, "Registrando... por favor espere");
				 startService(createCallingIntent(bundle));
				 } else{
				 //Tengo access token, vengo de auth -> tengo q ir a conversaciones y guardar token
				 LoginActivity.storeAcessToken(this,data);
				
				 }*/
			} catch (JSONException ex) {
				Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}
}
