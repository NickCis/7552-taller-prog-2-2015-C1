/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONObject;
import services.AppController;
import whatsapp.client.R;

/**
 * Metodo abstracto para hacer peticiones
 * @author rburdet
 */
public abstract class ServerService extends IntentService {

	protected ServerResultReceiver resultReceiver;
	public static String URI;
	protected Bundle data;
	protected ResultReceiver rec;
	protected CustomRequest req;

	public ServerService() {
		super("ServerService");

	}

	protected Response.Listener<JSONObject> createSuccessListener() {
		return new Response.Listener<JSONObject>() {
			public void onResponse(JSONObject t) {
				data.putBoolean("SERVICE", true);
				data.putString("data", t.toString());
				if (rec!=null)
					rec.send(0, data);
			}
		};
	}

	protected Response.ErrorListener createErrorListener() {
		return new Response.ErrorListener() {
			public void onErrorResponse(VolleyError ve) {
				data.putString("data", "UPS... Algo salio mal");
				data.putBoolean("SERVICE", false);
				if (rec!=null)
					rec.send(1, data);
			}
		};
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		data = (Bundle) intent.getParcelableExtra("info");
		URI = data.getString("URI");
		rec = (ResultReceiver) intent.getParcelableExtra("rec");
	}

}
