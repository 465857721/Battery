package com.king.batterytest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AboutActivity extends BaseActivity {
    private Button btn_share;
    private Button btn_feedback;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        context = this;
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                new AlertDialog.Builder(context)
                        .setTitle("电量显示")
                        .setMessage("您觉得好用或者有建议给个评价把~")
                        .setPositiveButton("施舍一下", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                        })
                        .setNegativeButton("残忍拒绝", null)
                        .show();
            }
        });
        btn_feedback = (Button) findViewById(R.id.btn_feedback);
        btn_feedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });


    }


}
