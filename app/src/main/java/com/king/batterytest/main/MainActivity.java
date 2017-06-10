package com.king.batterytest.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.king.batterytest.R;
import com.king.batterytest.home.HomeActivity;
import com.king.batterytest.service.BackService;

import org.greenrobot.eventbus.EventBus;


public class MainActivity extends BaseActivity {
    private Button btn_start;
    private Button btn_close;
    private NotificationManager nm;
    private CheckBox ck;
    private SharedPreferences.Editor editor;
    private Button btn_about;
    private RadioGroup group;
    public static MainActivity myself;
    private Context context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = this;
        myself = this;
        setContentView(R.layout.activity_main);

        ck = (CheckBox) findViewById(R.id.ck);
        editor = getSharedPreferences("myapp", 0).edit();

        ck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {

                    editor.putInt("ck", 0);// 0为默认值 ，默认开启

                } else {
                    editor.putInt("ck", 1);// 0为默认值 ，默认开启
                }
                editor.commit();
            }
        });

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        group = (RadioGroup) findViewById(R.id.radioGroup);
        SharedPreferences prefs = getSharedPreferences("myapp", 0);// 默认值为0
        // ，0为关闭状态~
        int flags = prefs.getInt("index", 0);
        int cki = prefs.getInt("ck", 0);

        if (cki == 0) {
            ck.setChecked(true);
        } else {
            ck.setChecked(false);
        }
        // group.clearCheck();
        Log.d("zkzk", flags + "");

        switch (flags) {
            case 0:
                RadioButton rb = (RadioButton) findViewById(R.id.rb_nor);
                rb.setChecked(true);
                break;
            case 1:
                RadioButton rb1 = (RadioButton) findViewById(R.id.rb_blue);
                rb1.setChecked(true);
                break;
            case 2:
                RadioButton rb2 = (RadioButton) findViewById(R.id.rb_thr);
                rb2.setChecked(true);
                break;
            case 3:
                RadioButton rb3 = (RadioButton) findViewById(R.id.rb_four);
                rb3.setChecked(true);
            default:
                break;
        }
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                Log.d("zk", "checkedId =" + checkedId);
                // group.getc
                editor.putInt("open", 0);
                Intent i = new Intent(MainActivity.this, BackService.class);
                int index = group.indexOfChild(MainActivity.this
                        .findViewById(checkedId));
                Log.d("zk", "" + index);
                switch (index) {
                    case 0:
                        editor.putInt("index", 0);
                        editor.commit();

                        startService(i);
                        break;
                    case 1:
                        editor.putInt("index", 1);
                        editor.commit();
                        startService(i);
                        break;
                    case 2:
                        editor.putInt("index", 2);
                        editor.commit();
                        startService(i);
                        Log.d("zk", "conmit");
                        break;
                    case 3:
                        editor.putInt("index", 3);
                        editor.commit();
                        startService(i);
                        Log.d("zk", "conmit");
                        break;
                    default:
                        break;
                }
            }
        });
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                editor.putInt("open", 0);// 0为开启
                editor.commit();
                Intent i = new Intent(MainActivity.this, BackService.class);
                startService(i);
                Toast.makeText(context, "开启成功！请在通知栏查看！", Toast.LENGTH_LONG)
                        .show();

            }
        });
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                editor.putInt("open", 1);// 1为关闭
                editor.commit();
                Intent i = new Intent(MainActivity.this, BackService.class);
                i.putExtra("ff", 0);
                stopService(i);
                Toast.makeText(context, "关闭成功！", Toast.LENGTH_LONG).show();

            }
        });
        btn_about = (Button) findViewById(R.id.btn_ab);
        btn_about.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                // overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });


    }


}
