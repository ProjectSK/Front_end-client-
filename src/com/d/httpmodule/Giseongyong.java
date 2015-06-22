package com.d.httpmodule;

import java.io.IOException;
import java.util.Collection;

public interface Giseongyong {


    public Giseongyong GET(String url);
    public Giseongyong POST(String url);
    public Giseongyong arg(String key, String value);
    public Giseongyong withForm();
    public Giseongyong withJson(String json);

    public Giseongyong send() throws IOException;
    public GYResponse fetchone();
    public Collection<GYResponse> fetchall();
}