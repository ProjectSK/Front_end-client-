package com.d.httpmodule;

public class GYResponse {
    public int code;
    public String content;
    
    public GYResponse(int code, String content) {
        this.code = code;
        this.content = content;
    }
}
