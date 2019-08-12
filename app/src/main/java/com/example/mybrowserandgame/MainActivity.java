package com.example.mybrowserandgame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private boolean backPressedOnce = false;
    private Handler statusUpdateHandler = new Handler();
    private Runnable statusUpdateRunnable;
    private ProgressBar spinner;

    public static String promoType, promoUrl;
    public static String PACKAGE_NAME;

    RequestQueue queue;

    //    String url = "http://android-app-rest.zinenko.net/api/entry";
    String url = "http://android-app-rest.zinenko.net/api/entry?package=" + PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        queue = Volley.newRequestQueue(this);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().getUserAgentString();
        webView.setWebViewClient(new CustomWebViewClient());

        volleyServer();
    }

    private void volleyServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Successful response", "Successful response");
                        parseData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse", String.valueOf(error));
                    }
                });
//                {
//                    @Override
//                    public void deliverError(final VolleyError error) {
//                        Log.i("deliverError", error.networkResponse.headers.get("Location"));
//                        final int status = error.networkResponse.statusCode;
//                        if(HttpURLConnection.HTTP_MOVED_PERM == status ||
//                                status == HttpURLConnection.HTTP_MOVED_TEMP ||
//                                status == HttpURLConnection.HTTP_SEE_OTHER) {
//                            url = error.networkResponse.headers.get("Location");
//                            volleyServer();
//                        }
//                    }
//                };
        queue.add(stringRequest);
    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            promoType = jsonObject.getString("promoType");
            promoUrl = jsonObject.getString("promoUrl");

            if(promoType.equals("l") ) {
                url = promoUrl;
            } else if (promoType.equals("p")){
                url = promoUrl;
            } else if (promoType.equals("w")) {
                url = "file:///android_asset/game-js.html";
            }

            Log.i("url", url);
            spinner.setVisibility(View.GONE);
            webView.loadUrl(promoUrl);
        } catch (JSONException e) {
            Log.e("parseDataError", e.getMessage());
            e.printStackTrace();
        }
    }

    // Кнопка назад
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {

            if(backPressedOnce) {
                super.onBackPressed();
            }

            backPressedOnce = true;
            final Toast toast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT);
            toast.show();

            statusUpdateRunnable = new Runnable() {
                @Override
                public void run() {
                    backPressedOnce = false;
                    toast.cancel();
                }
            };
            statusUpdateHandler.postDelayed(statusUpdateRunnable, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statusUpdateHandler != null) {
            statusUpdateHandler.removeCallbacks(statusUpdateRunnable);
        }
    }

}
