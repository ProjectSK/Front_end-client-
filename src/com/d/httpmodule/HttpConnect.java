package com.d.httpmodule;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class HttpConnect {
	String url;
	JSONArray postdata;

	public HttpConnect(String url, JSONArray data) {
		this.url = url;
		if(data==null)
			postdata = new JSONArray();
		else
			this.postdata = data;
	}
	public void addData(String key, String value) throws JSONException{
		postdata.put(new JSONObject().put(key,value));
	}

	public String connect() {
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response = null;
		
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		
		try {
			StringEntity se = new StringEntity(postdata.toString());
			httpPost.setEntity(se);
			response = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.toString();
	}

}