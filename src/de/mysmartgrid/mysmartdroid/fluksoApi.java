package de.mysmartgrid.mysmartdroid;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuilder;


import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.conn.SingleClientConnManager;


public class fluksoApi
{
	private String url;
	private String token;
	private String sensor;

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			Log.e("fluksoApi", "Conversion failed!", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("fluksoApi", "Couldn't close InputStream!", e);
			}
		}

		return sb.toString();
	}

	public fluksoApi(String url, String sensor, String token) throws IllegalArgumentException
	{
		if (url == null || url == "")
			throw new IllegalArgumentException("url has to be a non-empty String");
		if (token == null || token == "")
			throw new IllegalArgumentException("token has to be a non-empty String");
		if (sensor == null || sensor == "")
			throw new IllegalArgumentException("sensor has to be a non-empty String");
		this.url = url;
		this.token = token;
		this.sensor = sensor;
	}

	public String request (String interval, String unit) throws java.security.NoSuchAlgorithmException, java.security.KeyManagementException, java.security.KeyStoreException, java.security.UnrecoverableKeyException
	{
		if (interval == null || interval == "")
			throw new IllegalArgumentException("interval has to be a non-empty string");
		if (unit == null || unit == "")
			throw new IllegalArgumentException("unit has to be a non-empty string");

		Log.d("fluksoApi/request", "Requesting " + this.url+"sensor/"+this.sensor+"?interval="+interval+"&unit="+unit);
		HttpGet get = new HttpGet(this.url+"sensor/"+this.sensor+"?interval="+interval+"&unit="+unit+"&version=1.0");

		/*SSLSocketFactory sslf = new SSLSocketFactory(null);
		sslf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme ("https", sslf, 8443));
		SingleClientConnManager cm = new
			SingleClientConnManager(get.getParams(), schemeRegistry);
		HttpClient client = new DefaultHttpClient(cm, get.getParams());

		//HttpClient client = new DefaultHttpClient();

		get.addHeader("X-Token", this.token);*/
		HttpClient client = new DefaultHttpClient();
		get.addHeader("X-Version", "1.0");
		get.addHeader("Accept", "application/json");

		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream in = entity.getContent();
				String result = convertStreamToString(in);
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
