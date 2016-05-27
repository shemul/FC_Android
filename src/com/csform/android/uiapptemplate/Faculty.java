package com.csform.android.uiapptemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Faculty extends AppCompatActivity {
    public String _id , f_status , f_name ;
    public String current_status;
    Toolbar toolbar ;
    EditText txtCustom;
    Button setCustom ;
    public static Socket f_socket;
    {
        try {
            f_socket = IO.socket("https://socket-shemul.c9.io:8080");
            Log.d("skt", "Faculty Socket succeded");
        } catch (URISyntaxException e) {

            Log.d("skt", e.getMessage());
        }
    }

    private void attemptSend()  {

        JSONObject obj = new JSONObject();

        try {
            obj.put("_id", _id);
            obj.put("f_status", current_status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        f_socket.emit("test", obj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        f_socket.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        f_name = getIntent().getStringExtra("f_name");
        _id = getIntent().getStringExtra("_id");
        f_status = getIntent().getStringExtra("f_status");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Faculty Cloud");
        toolbar.setSubtitle(f_name + " , " + f_status);

        f_socket.connect();
       // f_socket.emit("facultyMode" , _id);
        setCustom = (Button) findViewById(R.id.setCustom);
        txtCustom = (EditText) findViewById(R.id.editText);
        setCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_status = txtCustom.getText() +"";
                toolbar.setSubtitle(f_name + " , " + current_status);
                attemptSend();
            }
        });


        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        ImageView imageView = (ImageView) findViewById(R.id.centerImage);

        if(f_status.equals("Available")) {
            rippleBackground.startRippleAnimation();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rippleBackground.isRippleAnimationRunning()) {
                        rippleBackground.stopRippleAnimation();
                        current_status = "Gone";
                        toolbar.setSubtitle(f_name + " , " + current_status);

                    } else {
                        rippleBackground.startRippleAnimation();
                        current_status = "Available";
                        toolbar.setSubtitle(f_name + " , " + current_status);
                    }
                    Log.d("skt", current_status);
                    attemptSend();
                }
            });

        } else {
            rippleBackground.stopRippleAnimation();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rippleBackground.isRippleAnimationRunning()) {
                        rippleBackground.stopRippleAnimation();
                        current_status = "Gone";
                        toolbar.setSubtitle(f_name + " , " + current_status);
                    } else {
                        rippleBackground.startRippleAnimation();
                        current_status = "Available";
                        toolbar.setSubtitle(f_name + " , " + current_status);
                    }
                    Log.d("skt", current_status);
                    attemptSend();
                }
            });

        }


    }
}
