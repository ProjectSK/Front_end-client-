package com.d.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class MyWebActivity extends Activity {
    public static String SOURCE = "<!DOCTYPE html>\n" + 
            "<head>\n" + 
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\"> \n" +
            "    <script type=\"text/javascript\" src=\"scripts/d3.min.js\"></script>\n" + 
            "    <script type=\"text/javascript\" src=\"scripts/radialProgress.js\"></script>\n" + 
            "\n" + 
            "    <link type=\"text/css\" rel=\"stylesheet\" href=\"styles/style.css\">\n" + 
            "\n" + 
            "    <style>\n" + 
            "\n" + 
            "        #test .arc2 {\n" + 
            "            stroke-weight:0.1;\n" + 
            "            fill: #3660b0;\n" + 
            "        }\n" + 
            "\n" + 
            "        #outer {\n" + 
            "            background:#FFFFFF;\n" + 
            "            border-radius: 5px;\n" + 
            "            color: #000;\n" + 
            "        }\n" + 
            "\n" + 
            "        #div1, #div2, #div3, #div4 {\n" + 
            "            width: 33%;\n" + 
            "            height: 200px;\n" + 
            "            box-sizing: border-box;\n" + 
            "            float: left;\n" + 
            "        }\n" + 
            "\n" + 
            "        #div2 .arc {\n" + 
            "            stroke-weight: 0.1;\n" + 
            "            fill: #f0a417;\n" + 
            "        }\n" + 
            "\n" + 
            "        #div2 .arc2 {\n" + 
            "            stroke-weight: 0.1;\n" + 
            "            fill: #b00d08;\n" + 
            "        }\n" + 
            "\n" + 
            "        #div3 .arc {\n" + 
            "            stroke-weight: 0.1;\n" + 
            "            fill: #1d871b;\n" + 
            "        }\n" + 
            "\n" + 
            "\n" + 
            "        .selectedRadial {\n" + 
            "            border-radius: 3px;\n" + 
            "            background: #f4f4f4;\n" + 
            "            color: #000;\n" + 
            "            box-shadow: 0 1px 5px rgba(0,0,0,0.4);\n" + 
            "            -moz-box-shadow: 0 1px 5px rgba(0,0,0,0.4);\n" + 
            "            border: 1px solid rgba(200,200,200,0.85);\n" + 
            "        }\n" + 
            "\n" + 
            "        .radial {\n" + 
            "            border-radius: 3px;\n" + 
            "            background: #FFFFFF;\n" + 
            "            color: #000;\n" + 
            "\n" + 
            "        }\n" + 
            "\n" + 
            "\n" + 
            "    </style>\n" + 
            "</head>\n" + 
            "\n" + 
            "<body>\n" + 
            "\n" + 
            "<div id='outer' style=\"margin: 0px auto; margin-top:20px; padding:10px\">\n" + 
            "    <div id=\"main\" style=\"margin: 0px auto; \">\n" + 
            "        <div id=\"div1\"></div>\n" + 
            "        <div id=\"div2\"></div>\n" + 
            "        <div id=\"div3\"></div>\n" + 
            "        <div id=\"div4\"></div>\n" + 
            "    </div>\n" + 
            "</div>\n" + 
            "\n" + 
            "\n" + 
            "\n" + 
            "<script language=\"JavaScript\">\n" + 
            "\n" + 
            "    var div1=d3.select(document.getElementById('div1'));\n" + 
            "    var div2=d3.select(document.getElementById('div2'));\n" + 
            "    var div3=d3.select(document.getElementById('div3'));\n" + 
            "    var div4=d3.select(document.getElementById('div4'));\n" + 
            "\n" + 
            "    start();\n" + 
            "\n" + 
            "    function onClick1() {\n" + 
            "        deselect();\n" + 
            "        div1.attr(\"class\",\"selectedRadial\");\n" + 
            "    }\n" + 
            "\n" + 
            "    function onClick2() {\n" + 
            "        deselect();\n" + 
            "        div2.attr(\"class\",\"selectedRadial\");\n" + 
            "    }\n" + 
            "\n" + 
            "    function onClick3() {\n" + 
            "        deselect();\n" + 
            "        div3.attr(\"class\",\"selectedRadial\");\n" + 
            "    }\n" + 
            "\n" + 
            "    function labelFunction(val,min,max) {\n" + 
            "\n" + 
            "    }\n" + 
            "\n" + 
            "    function deselect() {\n" + 
            "        div1.attr(\"class\",\"radial\");\n" + 
            "        div2.attr(\"class\",\"radial\");\n" + 
            "        div3.attr(\"class\",\"radial\");\n" + 
            "    }\n" + 
            "\n" + 
            "    function start() {\n" + 
            "\n" + 
            "        var rp1 = radialProgress(document.getElementById('div1'))\n" + 
            "                .label(\"RADIAL 1\")\n" + 
            "                .onClick(onClick1)\n" + 
            "                .diameter(150)\n" + 
            "                .value(78)\n" + 
            "                .render();\n" + 
            "\n" + 
            "        var rp2 = radialProgress(document.getElementById('div2'))\n" + 
            "                .label(\"RADIAL 2\")\n" + 
            "                .onClick(onClick2)\n" + 
            "                .diameter(150)\n" + 
            "                .value(132)\n" + 
            "                .render();\n" + 
            "\n" + 
            "        var rp3 = radialProgress(document.getElementById('div3'))\n" + 
            "                .label(\"RADIAL 3\")\n" + 
            "                .onClick(onClick3)\n" + 
            "                .diameter(150)\n" + 
            "                .minValue(100)\n" + 
            "                .maxValue(200)\n" + 
            "                .value(150)\n" + 
            "                .render();\n" + 
            "\n" + 
            "    }\n" + 
            "\n" + 
            "\n" + 
            "\n" + 
            "\n" + 
            "\n" + 
            "</script>\n" + 
            "\n" + 
            "</body>\n" + 
            "</html>\n" + 
            "\n" + 
            "       <!--\n" + 
            "/**\n" + 
            "Copyright (c) 2014 BrightPoint Consulting, Inc.\n" + 
            "\n" + 
            "Permission is hereby granted, free of charge, to any person\n" + 
            "obtaining a copy of this software and associated documentation\n" + 
            "files (the \"Software\"), to deal in the Software without\n" + 
            "restriction, including without limitation the rights to use,\n" + 
            "copy, modify, merge, publish, distribute, sublicense, and/or sell\n" + 
            "copies of the Software, and to permit persons to whom the\n" + 
            "Software is furnished to do so, subject to the following\n" + 
            "conditions:\n" + 
            "\n" + 
            "The above copyright notice and this permission notice shall be\n" + 
            "included in all copies or substantial portions of the Software.\n" + 
            "\n" + 
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n" + 
            "EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES\n" + 
            "OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" + 
            "NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT\n" + 
            "HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,\n" + 
            "WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING\n" + 
            "FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR\n" + 
            "OTHER DEALINGS IN THE SOFTWARE.\n" + 
            "*/\n" + 
            "\n" + 
            "              ->\n";
    
    WebView webview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		webview = new WebView(this);
		setContentView(webview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		webview.loadDataWithBaseURL("file:///android_asset/", SOURCE, "text/html; charset=utf-8", null, null);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(false);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
		setContentView(webview);
	}
}
