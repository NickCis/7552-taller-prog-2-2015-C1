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
 *
 * @author rburdet
 */
public class LoginService extends IntentService{


    public LoginService() {
        super("asdasdadsasdasdad");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
		
		final Bundle data = (Bundle)intent.getParcelableExtra("info");
		final String endPoint = data.getString("endpoint");
		final String usr = data.getString("username");
        final String pw = data.getString("password");
        //final String URI = String.format("http://httpbin.org/get?param1=%1$s","hello");
        final String URI = "http://192.168.1.40:8000/"+endPoint	;
        final ResultReceiver rec = (ResultReceiver) intent.getParcelableExtra("rec");

		Log.d("SERVER URI------------------------------------------------------", URI);
		final StringRequest strReq = new StringRequest(Method.POST, URI, 
			new Response.Listener<String>(){
				public void onResponse(String t) {
					data.putString("data", "asdasdasd");
					data.putBoolean("SERVICE", true);
					rec.send(0,data);
				}
			}, new Response.ErrorListener() {

				public void onErrorResponse(VolleyError ve) {
					data.putString("data", "salio todo como el orto");
					data.putBoolean("SERVICE", false);
					rec.send(1, data);
				}
			}
			){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<String,String>();
				params.put("user", usr);
				params.put("pass",pw);
				return params;
			}
		};
		Log.d("SERVER URI", strReq.getUrl());


        AppController.getInstance().addToRequestQueue(strReq);

    }
    
}
