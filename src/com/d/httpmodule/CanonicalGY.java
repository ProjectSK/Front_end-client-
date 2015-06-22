package com.d.httpmodule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.http.AndroidHttpClient;

public final class CanonicalGY implements Giseongyong {
    Context context;

    
    public CanonicalGY () {
        this(null);
    }

    public CanonicalGY (Context context) {
        this.context = context;
    }
    
    private AndroidHttpClient newClient () {
        return AndroidHttpClient.newInstance("Giseongyong/0.1 (Helper library for AndroidHttpClient)", context);
    }

    private enum ArgType {
        FormEncode, ApplicationJson, URLArg
    }

    private static class Command {
        public final String method;
        public final String url;
        public ArgType argType;
        public ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        public String json = null;
        public Command (String method, String url) { 
            this.method = method;
            this.url = url;
            if ("GET".equals(method)) {
                argType = ArgType.URLArg;
            } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                argType = ArgType.FormEncode;
            } 
        }
    }

    private ArrayList<Command> reserved = new ArrayList<Command>();
    private Command topCmd = null;
    
    private Command methodCreation (String method, String url) {
        if (topCmd != null) {
            reserved.add(topCmd);
        }
        topCmd = new Command(method, url);
        return topCmd;
    }

    @Override
    public Giseongyong GET(String url) {
        methodCreation("GET", url);
        return this;
    }

    @Override
    public Giseongyong POST(String url) {
        methodCreation("POST", url);
        return this;
    }


    private void assertMethod() {
        if (topCmd == null) {
            throw new IllegalStateException("Please call one of functions that indicates HTTP Method(GET, POST, PUT) beforehand.");
        }
    }


    @Override
    public Giseongyong arg(String key, String value) {
        assertMethod();
        topCmd.nameValuePairs.add(new BasicNameValuePair(key, value));
        return this;
    }

    @Override
    public Giseongyong withForm() {
        assertMethod();
        topCmd.argType = ArgType.FormEncode;
        return this;
    }

    @Override
    public Giseongyong withJson(String json) {
        assertMethod();
        topCmd.argType = ArgType.ApplicationJson;
        topCmd.json = json;
        return this;
    }

    private LinkedList<GYResponse> responses = new LinkedList<GYResponse>();
    

    @Override
    public Giseongyong send() throws IOException {
        assertMethod();
        
        reserved.add(topCmd);
        topCmd = null;

        AndroidHttpClient client = newClient();
        try {
            for (Command cmd : reserved) {
                HttpUriRequest req = null;
                if (cmd.method.equals("GET")) {
                    String url = cmd.url;
                    if (!cmd.nameValuePairs.isEmpty() && cmd.argType == ArgType.URLArg) {
                        if (!url.endsWith("?")) {
                            url += "?";
                        }
                        url += URLEncodedUtils.format(cmd.nameValuePairs, "UTF-8").replace("+", "%20");
                    }
                    req = new HttpGet(url);
                } else {
                    HttpPost postreq = null;
                    if (cmd.method.equals("POST")) {
                        postreq = new HttpPost(cmd.url);
                    } 
                    switch (cmd.argType) {
                    case ApplicationJson:
                        StringEntity strEntity = new StringEntity(cmd.json);
                        strEntity.setContentType("application/json");
                        postreq.setEntity(strEntity);
                        break;
                    case FormEncode:
                        postreq.setEntity(new UrlEncodedFormEntity(cmd.nameValuePairs));
                        break;
                    default:
                        break;
                    }
                    req = postreq;
                } 
                HttpResponse resp = client.execute(req);
                int statusCode = resp.getStatusLine().getStatusCode();
                String content = EntityUtils.toString(resp.getEntity());
                responses.add(new GYResponse(statusCode, content));
            }
        } finally {
            client.close();
            reserved.clear();
        }
        return this;
    }

    @Override
    public GYResponse fetchone() {
        return responses.removeFirst();
    }

    @Override
    public Collection<GYResponse> fetchall() {
        Collection<GYResponse> result = responses;
        responses = new LinkedList<GYResponse>();
        return result;
    }
}
