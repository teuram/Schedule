package com.teeura.schedule;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Process;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        setContentView(ll);

        ScrollView sv = new ScrollView(this);
        ll.addView(sv);

        TextView tv = new TextView(this);
        final String get = "Выполняется запрос";
        tv.setText(get);
        tv.setPadding(16, 16, 16, 16);

        sv.addView(tv);

        Thread thread = new Thread() {
            @Override
            public void run() {
                final String[] groups = new String[] {
                    "3РПУ-20-1",
                    "23-1МРПс",
                    "23-1 МРП (с)"
                };
                String str = App.schedule(groups);
                runOnUiThread(() -> tv.setText(str));
            }
        };


        new Thread() {
            @Override
            public void run() {
                int dots = 0;
                StringBuffer sb = new StringBuffer("");

                while (thread.isAlive()) {
                    runOnUiThread(() -> tv.setText(get + sb.toString()));
                    dots++;
                    sb.append('.');
                    if (dots > 5) {
                        sb.delete(0, sb.length() - 1);
                        dots = 0;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {}
                }
            }
        }.start();

        thread.start();

    }
}
