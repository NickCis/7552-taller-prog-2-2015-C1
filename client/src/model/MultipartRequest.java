package model;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import android.graphics.Bitmap;

/**
 * Encargada de hacer requests multipart para archivos
 * @author rburdet ncisco;
 */
public class MultipartRequest extends Request<String> {

	private final Response.Listener<String> mListener;
	private final Map<String, Bitmap> mKeyValue;

	private final static String BOUNDARY = "------------------------7d1f9a2c454ea51e";

	public MultipartRequest(String url, Map<String, Bitmap> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(Method.POST, url, errorListener);

		mListener = listener;
		mKeyValue = params;
	}

	@Override
	public String getBodyContentType() {
		return "multipart/form-data; boundary="+BOUNDARY;
	}

	byte[] concatenateByteArrays(byte[] a, byte[] b){
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		byte[] out = ("").getBytes();
		for (Map.Entry<String, Bitmap> entry : mKeyValue.entrySet()) {
			Bitmap bitmap = entry.getValue();
			out = concatenateByteArrays(out, (BOUNDARY+"\ncontent-disposition: form-data; name=\""+entry.getKey()+"\"; filename=\"avatar.png\"\nContent-Type: image/png\nContent-Transfer-Encoding: binary\n\n").getBytes());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			out = concatenateByteArrays(out, stream.toByteArray());
			out = concatenateByteArrays(out, ("\n"+BOUNDARY).getBytes());
		}
		return out;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String jsonString = "";
		try {
			jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return Response.success(jsonString, getCacheEntry());
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}
} 
