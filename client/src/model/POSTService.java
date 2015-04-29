/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import java.util.Map;
import org.json.JSONObject;
import services.AppController;

/**
 * Servicio encargado de hacer un request POST al servidor
 * @author rburdet
 */
public class POSTService extends ServerService {

	public POSTService() {
		super();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
		Map<String, String> params = (Map<String, String>) data.getSerializable("params");
		req = new CustomRequest(Method.POST,URI, params, createSuccessListener(),createErrorListener());
		AppController.getInstance().addToRequestQueue(req);
	}

}
