package com.example.mybrowserandgame;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient  extends WebViewClient {
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    // Для старых устройств
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}

//    Перенаправлять ссылки
//    private class CustomWebViewClient extends WebViewClient {
//        @RequiresApi(Build.VERSION_CODES.N)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            String url = request.getUrl().toString();
//            if (URLUtil.isNetworkUrl(url)) {
//                return false;
//            }
//            if (url.startsWith("intent")) {
//                try {
//                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
//                    String fallbackUrl = intent.getStringExtra("browser_fallback_url");
//                    if (fallbackUrl != null) {
//                        webView.loadUrl(fallbackUrl);
//                    }
//
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                    Toast.makeText(MainActivity.this, "No activity found", Toast.LENGTH_LONG).show();
//                }
//            }
//            return true;
//        }
//    }


