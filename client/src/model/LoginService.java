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
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import model.ServerResultReceiver;
import org.json.JSONObject;
import services.AppController;



/**
 *
 * @author rburdet
 */
public class LoginService extends IntentService{

	private static String YAHOO_FINANCE_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22#sym#%22)&env=store://datatables.org/alltableswithkeys";

    public LoginService() {
        super("asdasdadsasdasdad");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
		final Bundle data = (Bundle)intent.getParcelableExtra("info");
        final String URI = String.format("http://httpbin.org/get?param1=%1$s","hello");

        final ResultReceiver rec = (ResultReceiver) intent.getParcelableExtra("rec");

        JsonObjectRequest req = new JsonObjectRequest(Method.GET,URI,null, new Response.Listener<JSONObject>(){
            public void onResponse(JSONObject t) {
                data.putString("data", "asdasdasd");
                data.putBoolean("SERVICE", true);
                rec.send(0, data);
            }
        }, new Response.ErrorListener(){

            public void onErrorResponse(VolleyError ve) {
                data.putString("data", "salio todo como el orto");
                data.putBoolean("SERVICE", false);
                rec.send(0, data);
            }
            
        });
        /* ESTO ES LO VIEJO Y FUNCIONA:


		Stock stock = intent.getParcelableExtra("stock");
		final ResultReceiver rec = (ResultReceiver) intent.getParcelableExtra("rec");
		
		// Here we retrieve the stock quote using Yahoo! Finance
    	Log.d("Srv", "Get stock");
        String url = YAHOO_FINANCE_URL.replaceAll("#sym#", stock.getSymbol());
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.connect();
            InputStream is = con.getInputStream();

            // Start parsing XML
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(is, null);
            int event = parser.getEventType();
            String tagName = null;
            String currentTag = null;
            Stock stockResult = new Stock();
            
            while (event != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();
                Log.d("Srv", "Tag:" + tagName);
                if (event == XmlPullParser.START_TAG) {
                	currentTag = tagName;
                }
                else if (event == XmlPullParser.TEXT) {
                	if ("ASK".equalsIgnoreCase(currentTag)) 
                		stockResult.setAskValue(Double.parseDouble(parser.getText()));
                    else if	("BID".equalsIgnoreCase(currentTag)) {                    	
                    	stockResult.setBidValue(Double.parseDouble(parser.getText()));
                    	Bundle b = new Bundle();
                    	b.putParcelable("stock", stockResult);
                    	rec.send(0, b);
                	}
                }
                
                event = parser.next();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
        
        */
        

        AppController.getInstance().addToRequestQueue(req);

    }
    
}
