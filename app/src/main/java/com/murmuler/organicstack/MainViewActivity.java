package com.murmuler.organicstack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.murmuler.organicstack.util.Constants;
import com.murmuler.organicstack.util.MyWebClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String ROOT_CONTEXT = Constants.ROOT_CONTEXT;
    private static final int MAIN_PAGE = 1;
    private static final int LIKE_PAGE = 2;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.botMain)
    ImageButton botMain;
    @BindView(R.id.botSearch)
    ImageButton botSearch;
    @BindView(R.id.botLike)
    ImageButton botLike;
    @BindView(R.id.botMore)
    ImageButton botMore;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private String memberId;
    private String nickname;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.bottom_more).into(botMore);
        Glide.with(this).load(R.drawable.bottom_search).into(botSearch);

        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new MyWebClient());
        webView.setWebChromeClient(new WebChromeClient());
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new AndroidBridge(), "mainView");

        Intent intent = getIntent();
        memberId = intent.getExtras().getString("memberId");
        nickname = intent.getExtras().getString("nickname");
        String isLike = intent.getExtras().getString("isLike");
        if(isLike != null) {
            iconSetting(LIKE_PAGE);
            webView.loadUrl(ROOT_CONTEXT+"/mobile/like/"+memberId);
        } else {
            webView.loadUrl(ROOT_CONTEXT+"/mobile/main");
            iconSetting(MAIN_PAGE);
        }
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.loginId);
        userName.setText(nickname);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void iconSetting(int viewName) {
        switch (viewName) {
            case MAIN_PAGE :
                Glide.with(this).load(R.drawable.bottom_main_on).into(botMain);
                Glide.with(this).load(R.drawable.bottom_like).into(botLike);
                break;
            case LIKE_PAGE :
                Glide.with(this).load(R.drawable.bottom_main).into(botMain);
                Glide.with(this).load(R.drawable.bottom_like_on).into(botLike);
                break;
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void search(final String keyword) {
            handler.post(new Runnable(){
                public void run(){
                    Log.d("검색 요청", keyword);
                    Intent intent = new Intent(MainViewActivity.this, MainActivity.class);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("memberId", memberId);
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.botMain)
    public void clickMain(View v) {
        iconSetting(MAIN_PAGE);
        webView.loadUrl(ROOT_CONTEXT+"/mobile/main");
    }
    @OnClick(R.id.botSearch)
    public void clickSearch(View v) {
        Intent intent = new Intent(MainViewActivity.this, MainActivity.class);
        intent.putExtra("nickname", nickname);
        intent.putExtra("memberId", memberId);
        startActivity(intent);
    }
    @OnClick(R.id.botLike)
    public void clickLike(View v) {
        iconSetting(LIKE_PAGE);
        webView.loadUrl(ROOT_CONTEXT+"/mobile/like/"+memberId);
    }
    @OnClick(R.id.botMore)
    public void clickMore(View v) {
        drawerLayout.openDrawer(navigationView);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        System.out.println("selected " + id);

        switch (id) {
            case R.id.nav_search :
                Intent intent = new Intent(MainViewActivity.this, MainActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("memberId", memberId);
                startActivity(intent);
                break;
            case R.id.nav_like :
                iconSetting(LIKE_PAGE);
                webView.loadUrl(ROOT_CONTEXT+"/mobile/like/"+memberId);
                break;
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }


}
