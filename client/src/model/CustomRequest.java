/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.android.volley.AuthFailureError;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import java.net.URLEncoder;

/**
 * Clase encagardada de hacer requests al servidor, los parametros son configurados por el servicio que lo use, por lo general servicios get y post
 * @author rburdet
 */
public class CustomRequest extends Request<JSONObject>{

      private Listener<JSONObject> listener;
      private Map<String, String> params;

      public CustomRequest(String url, Map<String, String> params,
                Listener<JSONObject> reponseListener, ErrorListener errorListener) {
            super(Method.GET, url, errorListener);
            this.listener = reponseListener;
            this.params = params;
      }

      public CustomRequest(int method, String url, Map<String, String> params,
                Listener<JSONObject> reponseListener, ErrorListener errorListener) {
            super(method, url, errorListener);
            this.listener = reponseListener;
            this.params = params;
        }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
      return params;
    };

	@Override
	public byte[] getBody() throws AuthFailureError {
		Map<String,String> params = getParams();
		if (params != null && params.size() > 0){
			return encodeParameters(params,getParamsEncoding());
		}
		return null;
	}

	@Override
	public byte[] getPostBody() throws AuthFailureError {
		Map<String,String> params = getPostParams();
		if (params != null && params.size() > 0){
			return encodeParameters(params,getPostParamsEncoding());
		}
		return null;
	}


	private byte[] encodeParameters(Map<String,String> params , String paramsEncoding){
		StringBuilder encodedParams = new StringBuilder();
		try{
			for (Map.Entry<String,String> entry : params.entrySet()){
				encodedParams.append(entry.getKey());
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(),paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		}catch (UnsupportedEncodingException uee){
			throw new RuntimeException("Encoding not supported : " + paramsEncoding,uee);
		}
	}
	
    

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
         try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new JSONObject(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
    }

}