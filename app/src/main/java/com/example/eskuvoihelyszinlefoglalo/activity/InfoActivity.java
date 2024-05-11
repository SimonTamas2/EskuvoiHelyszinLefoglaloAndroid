package com.example.eskuvoihelyszinlefoglalo.activity;

import android.content.res.loader.ResourcesLoader;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eskuvoihelyszinlefoglalo.R;
import com.example.eskuvoihelyszinlefoglalo.utils.FileUtils;
import com.example.eskuvoihelyszinlefoglalo.utils.MENU;
import com.example.eskuvoihelyszinlefoglalo.utils.NavUtils;

import java.io.IOException;
import java.io.InputStream;

public class InfoActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        NavUtils.setupBottomNav(this,findViewById(R.id.nav),MENU.INFO);

        webView = findViewById(R.id.webview);


        InputStream inputStream = getResources().openRawResource(R.raw.info);
        String unencodedHtml = "";
        try {
            unencodedHtml = new String(FileUtils.getBytes(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                Base64.NO_PADDING);
        webView.loadData(encodedHtml,"text/html","base64");
    }
}
