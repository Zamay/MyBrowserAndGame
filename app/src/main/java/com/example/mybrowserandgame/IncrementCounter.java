package com.example.mybrowserandgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class IncrementCounter extends AppCompatActivity {

    TextView tvCounter;
    Button btnIncrement;
    int counter = 0;

    private WebView webView;
    private boolean backPressedOnce = false;
    private Handler statusUpdateHandler = new Handler();
    private Runnable statusUpdateRunnable;
    private ProgressBar spinner;

    private static String promoType, promoUrl;
    private static String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    static String url = "http://android-app-rest.zinenko.net/api/entry?package=" + PACKAGE_NAME;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increment_counter);

        tvCounter = findViewById(R.id.tvCounter);
        btnIncrement = findViewById(R.id.btnIncrement);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        queue = Volley.newRequestQueue(this);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().getUserAgentString();
        webView.setWebViewClient(new CustomWebViewClient());

        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = counter + 1;
                tvCounter.setText(String.valueOf(counter));
            }
        });
        volleyServer();
    }

    public void showToast(View view) {
        webView.loadUrl("about:blank");
        spinner.setVisibility(View.VISIBLE);
        volleyServer();
    }

    private void volleyServer() {

        // Быстроту не заметил. В кеш и так пишет. Можно и не юзать.
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();

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
        queue.add(stringRequest);
    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            promoType = jsonObject.getString("promoType");
            promoUrl = jsonObject.getString("promoUrl");
            String responseUrl = "";

            if (promoType.equals("l")) {
                responseUrl = promoUrl;
            } else if (promoType.equals("p")) {
                responseUrl = promoUrl;
            } else if (promoType.equals("w")) {
                responseUrl = "file:///android_asset/game-js.html";
            }

            Log.d("url", responseUrl);
            spinner.setVisibility(View.GONE);
            webView.loadUrl(responseUrl);
        } catch (JSONException e) {
            Log.e("parseDataError", e.getMessage());
            e.printStackTrace();
        }
    }

    // Кнопка назад
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {

            if (backPressedOnce) {
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
