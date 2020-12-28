package com.khs.spcmeasure.library;

import android.util.Log;

import org.apache.http.client.ClientProtocolException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSONParser {
	
	private static final String TAG = "JSONParser";
	private static int TIMEOUT = 60 * 1000;
	
	// was TODO:
//	static InputStream is = null;
//	static JSONObject jObj = null;
//	static String json = "";
	
	// constructor
	public JSONParser() {		
	}
	
	public JSONObject getJSONFromUrl(String url) {
		// 2020Dec28 - declare connection
		HttpURLConnection c = null;
		JSONObject jObj = null;
		
		// http request
		try {
			// 2020Dec28 - create url
			URL u = new URL(url);

			// 2020Dec28 - open connection
			c = (HttpURLConnection) u.openConnection();

			// 2020Dec28 - specify GET request
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setConnectTimeout(TIMEOUT);
			c.setReadTimeout(TIMEOUT);

			// 2020Dec28 - get JSON response from Http request
			jObj = getJSONFromResponse(c);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// convert response to json string
		// TODO was:
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is, HTTP.UTF_8 /* was: , "ios-8859-1" */), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = sb.toString();
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e.toString());
//		}
		
		// TODO was:
//		// try parse the json string to a json object
//		try {
//			jObj = new JSONObject(json);			
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}

		return jObj;	
	}
	
	public JSONObject getJSONFromUrl(String url, String body) {
		// 2020Dec28 - declare connection
		HttpURLConnection con = null;
		JSONObject jObj = null;
		
		Log.d(TAG, "body = " + body);
		
		// make http POST request with String body
		try {
			// 2020Dec28 - create url
			URL u = new URL(url);

			// 2020Dec28 - open connection
			con = (HttpURLConnection) u.openConnection();

			// 2020Dec28 - specify POST request.  See:
			// https://blog.codavel.com/how-to-integrate-httpurlconnection
			// https://www.baeldung.com/httpurlconnection-post
			// https://stackoverflow.com/questions/37795759/httpurlconnection-illegal-state-exception-already-connected

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setUseCaches(false);
			con.setAllowUserInteraction(false);
			con.setConnectTimeout(TIMEOUT);
			con.setReadTimeout(TIMEOUT);

			//
			con.setDoOutput(true);

			OutputStream os = con.getOutputStream();
			byte[] input = body.getBytes("utf-8");
			os.write(input, 0, input.length);

			// 2020Dec28 - get JSON response from Http request
			jObj = getJSONFromResponse(con);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jObj;		
	}

	// 2020Dec28 - extracts json from http connection
	private JSONObject getJSONFromResponse(HttpURLConnection httpCon) {
		JSONObject jObj = null;

		// extract json from response body
		try {
			// Log.d(TAG, "httpResp - status(1) = " +  httpCon.getResponseCode());
			String jStr = "";
			httpCon.connect();

			// Log.d(TAG, "httpResp - status(2) = " +  httpCon.getResponseCode());

			if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 2020Dec28 - get response as a String
				BufferedReader br = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line+"\n");
				}
				br.close();

				// 2020Dec28 - get response as JSON
				jObj = new JSONObject(sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(TAG, "getJSONFromResponse - Error parsing data " + e.toString());
		} finally {
			httpCon.disconnect();
		}

		return jObj;
	}

}

