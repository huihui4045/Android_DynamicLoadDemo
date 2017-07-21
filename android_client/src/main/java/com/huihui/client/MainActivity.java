package com.huihui.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, ProxyActivity.class);
        intent.putExtra(ProxyActivity.EXTRA_DEX_PATH, "/storage/emulated/0/plugin.apk");
        startActivity(intent);
    }
}
