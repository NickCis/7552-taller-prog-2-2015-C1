/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import whatsapp.client.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;
import model.ServerResultReceiver;
import org.json.JSONObject;
import services.AppController;

/**
 * Servicio encargado de hacer un request POST al servidor
 * @author rburdet
 */
public class LoginService extends IntentService {

	public LoginService() {
		super("LoginService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final Bundle data = (Bundle) intent.getParcelableExtra("info");
		final String usr = data.getString("username");
		final String pw = data.getString("password");
		//final String URI = String.format("http://httpbin.org/get?param1=%1$s","hello");
		final String URI = data.getString("URI");
		final ResultReceiver rec = (ResultReceiver) intent.getParcelableExtra("rec");
				Map<String, String> params = new HashMap<String, String>();
		params.put("user", usr);
		params.put("pass", pw);
		CustomRequest req = new CustomRequest(Method.POST, URI, params,
				new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject t) {
						data.putBoolean("SERVICE", true);
						data.putString("data", t.toString());
						rec.send(0, data);
					}

				},
				new Response.ErrorListener() {
					public void onErrorResponse(VolleyError ve) {
						data.putString("data", "UPS... Algo salio mal");
						data.putBoolean("SERVICE", false);
						rec.send(1, data);
					}
				}
		);
		AppController.getInstance().addToRequestQueue(req);
	}

}
