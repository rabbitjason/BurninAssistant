package com.bugsbunny.burninassistant;

import android.net.http.SslError;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2015/12/9.
 */
public class WebViewActivity extends BaseActivity {

    private WebView myWebView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        myWebView = (WebView) findViewById(R.id.wbvShop);

        //myWebView.loadUrl("https://ipcrs.pbccrc.org.cn/");
        myWebView.loadUrl("http://baidu.com");
        myWebView.requestFocusFromTouch();

        // JavaScript使能(如果要加载的页面中有JS代码，则必须使能JS)
        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);

        // 更强的打开链接控制：自己覆写一个WebViewClient类：除了指定链接从WebView打开，其他的链接默认打开
        myWebView.setWebViewClient(new ShopWebViewClient());
    }

    private class ShopWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //handler.cancel(); // Android默认的处理方式
            //handler.proceed();  // 接受所有网站的证书
            //handleMessage(Message msg); // 进行其他处理
            super.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(myWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){
            myWebView.goBack();   //goBack()表示返回webView的上一页面
            return true;
        }
        return false;
    }
}
