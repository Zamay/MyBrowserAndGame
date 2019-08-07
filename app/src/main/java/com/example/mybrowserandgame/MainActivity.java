package com.example.mybrowserandgame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.net.HttpURLConnection;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; " +
            "Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) " +
            "Chrome/18.0.1025.166 Mobile Safari/535.19";

    RequestQueue queue;
    String url ="https://a.lucky-games.online/click?" +
            "pid=720&" +
            "offer_id=3048&" +
            "l=1554545944";
//    String url ="https://a.lucky-games.online/click?pid=720&offer_id=3048";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setUserAgentString(USER_AGENT);
        webView.setWebViewClient(new CustomWebViewClient());

//        webView.loadUrl(url);
        volleyServer();
    }

    private void volleyServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       Log.i("response", response);

                       // Проверка что показывать, а пока
                        Random r = new Random();
                        int choice = r.nextInt(2);
                        if(choice==0) {
                            url = "file:///android_asset/game-js.html";
                        }

                        webView.loadUrl(url);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse", String.valueOf(error));
                    }
                }){
                    @Override
                    public void deliverError(final VolleyError error) {
                        Log.i("deliverError", error.networkResponse.headers.get("Location"));
                        final int status = error.networkResponse.statusCode;
                        if(HttpURLConnection.HTTP_MOVED_PERM == status ||
                                status == HttpURLConnection.HTTP_MOVED_TEMP ||
                                status == HttpURLConnection.HTTP_SEE_OTHER) {
                            url = error.networkResponse.headers.get("Location");
                            volleyServer();
                        }

                        // Пример
//                        if (status == HttpStatus.SC_MOVED_PERMANENTLY || status == HttpStatus.SC_MOVED_TEMPORARILY) {
//                            String newUrl = responseHeaders.get("Location");
//                            request.setRedirectUrl(newUrl);
//                        }
                    }
                };
        queue.add(stringRequest);
    }

    // Кнопка назад
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
