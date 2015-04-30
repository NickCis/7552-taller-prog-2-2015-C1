/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import android.content.Intent;
import android.net.Uri;
import com.android.volley.Request.Method;
import java.util.Map;
import static model.ServerService.URI;
import services.AppController;

/**
 *
 * @author rburdet
 */
public class DELETEService extends ServerService{

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
			Map<String, String> params = (Map<String, String>) data.getSerializable("params");
		if (params !=null)
			URI = formGETUri(params);
		req = new CustomRequest(Method.DELETE, URI, null, createSuccessListener(), createErrorListener());
		AppController.getInstance().addToRequestQueue(req);
	}	
	
	private String formGETUri(Map<String, String> params) {
		Uri.Builder builder = new Uri.Builder();
		for (Map.Entry<String, String> entrySet : params.entrySet()) {
			String key = entrySet.getKey();
			String value = entrySet.getValue();
			builder.appendQueryParameter(key, value);
		}
		return URI + builder.build().toString();
	}
	
}
