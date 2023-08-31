package com.teeura.schedule;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Process;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.View;
import android.content.ContentResolver;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;

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

        LinearLayout l_scroll = new LinearLayout(this);
        l_scroll.setOrientation(LinearLayout.VERTICAL);

        sv.addView(l_scroll);

        // ===============
        TextView tv = new TextView(this);
        tv.setPadding(16, 16, 16, 16);

        Typeface face = Typeface.MONOSPACE;
        tv.setTypeface(face);

        tv.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", tv.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Скопированно в буффер обмена", 
                        Toast.LENGTH_SHORT).show();
            }
        });

        l_scroll.addView(tv);
        // ===============

        new Thread() {
            @Override
            public void run() {
                final String[] groups = new String[] {
                    "4РПУ-20-1"
                };
                String str = App.schedule(groups);
                runOnUiThread(() -> tv.setText(str));
            }
        }.start();


        // new Thread() {
        //     @Override
        //     public void run() {
        //         int dots = 0;
        //         StringBuffer sb = new StringBuffer("");

        //         while (thread.isAlive()) {
        //             runOnUiThread(() -> tv.setText(sb.toString()));
        //             dots++;
        //             sb.append('.');
        //             if (dots > 5) {
        //                 sb.delete(0, sb.length() - 1);
        //                 dots = 0;
        //             }
        //             try {
        //                 Thread.sleep(100);
        //             } catch (Exception e) {}
        //         }
        //     }
        // }.start();

        // thread.start();

    }
}
