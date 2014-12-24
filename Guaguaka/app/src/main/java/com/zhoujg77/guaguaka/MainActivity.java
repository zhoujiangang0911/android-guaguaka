package com.zhoujg77.guaguaka;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    Guaka guaka;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guaka = (Guaka) findViewById(R.id.id_gua);

        guaka.setOnGuaKaCompleteListener(new Guaka.OnGuaKaCompleteListener() {
            @Override
            public void complete() {
                Toast.makeText(getApplicationContext(),"你刮完了",Toast.LENGTH_SHORT).show();
            }
        });
        guaka.setText("设置字体需要调用重新设置矩形");

    }



}
