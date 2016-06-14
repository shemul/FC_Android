package com.csform.android.fcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csform.android.fcloud.data.Review;
import com.csform.android.fcloud.data.ReviewAdapter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

;import static android.R.attr.id;


public class MainActivity extends AppCompatActivity {

    public static Context contex ;
    Toolbar toolbar;
    private Button sendButton;
    private TextView console;
    ListView listView;
    public static ReviewAdapter adapter;
    public RecyclerView recyclerView;
    final List<Review> items = new ArrayList<Review>();

    public String IMEI ;
    public static Socket mSocket2;

    {
        try {
            mSocket2 = IO.socket("https://socket-shemul.c9.io:8080");
            Log.d("skt", "Socket succeded connection");
        } catch (URISyntaxException e) {

            Log.d("skt", e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("skt", "destroying");
       // mSocket2.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("sky" ,"On Resume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("skt", "on Restart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contex = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI =  telephonyManager.getDeviceId();

        /*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Faculty Cloud");
        toolbar.setSubtitle("SHEILD ID : " + IMEI.substring(9,15)  + ", Alpha");
        this.setSupportActionBar(toolbar);
        */
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        mSocket2.connect();
        Log.d("skt" ,"Again onCreate");


        recyclerView.setAdapter(adapter = new ReviewAdapter(items, R.layout.fb));
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        console = (TextView) findViewById(R.id.textView);
        console.setMovementMethod(new ScrollingMovementMethod());



        requestServer(IMEI);

        mSocket2.on("item", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            JSONObject msg = data.getJSONObject("msg");
                            //Log.d("socketeting",  msg.toString());
                            //console.append("Faculty Name : " + msg.getString("f_name"));

                            Log.d("skt" ,msg.getString("f_imei"));
                            String imei =  msg.getString("f_imei");

                            if(imei.equals(IMEI)) {

                                String _id = msg.getString("_id");
                                String f_id = msg.getString("f_id");
                                String f_name = msg.getString("f_name");
                                String f_email = msg.getString("f_email");
                                String f_status = msg.getString("f_status");
                                String f_dept = msg.getString("f_dept");
                                items.add(new Review(_id, f_id, f_name, f_email, f_status, f_dept));
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d("skt" , "I am not concerned");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });


        mSocket2.on("update", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("skt", "updating");
                        //JSONArray data = (JSONArray) args[0];

                        JSONObject ja = (JSONObject) args[0];
                        try {
                            JSONArray ob = ja.getJSONArray("item");
                            JSONArray ts = ob.getJSONArray(0);
                            JSONObject sd = ts.getJSONObject(0);

                            String imei =  sd.getString("f_imei");
                            String ids = sd.getString("_id");
                            Log.d("skt" , ids);
                            if(imei.equals(IMEI)) {
                                String f_name = sd.getString("f_name");
                                String _id = sd.getString("_id");
                                String f_id = sd.getString("f_id");
                                String f_email = sd.getString("f_email");
                                String f_status = sd.getString("f_status");
                                String f_dept = sd.getString("f_dept");

                                // console.append(f_name);
                                //values.remove(tmp);
                                Boolean flag = false ;
                                for (Review model : items) {
                                    final String text = model._id.toLowerCase();
                                    if (text.contains(_id)) {
                                        //items.remove(model);
                                        //items.add();
                                        int a = items.indexOf(model);
                                        items.set(a, new Review(_id, f_id, f_name, f_email, f_status, f_dept));
                                        adapter.notifyDataSetChanged();
                                        flag = true;
                                        break;
                                    } else {
                                        flag = false;

                                    }
                                }

                                if(!flag) {
                                    items.add( new Review(_id, f_id, f_name, f_email, f_status, f_dept));
                                    adapter.notifyDataSetChanged();
                                }
                                // return filteredModelList;
                                adapter.notifyDataSetChanged();

                                //listView.setAdapter(adapter);
                            } else {
                                Log.d("skt" , "One Faculty has been updated but I am not concerned and I will try to remove him from this sheild if he/she exists");

                                try {
                                    for (Review model : items) {
                                        final String text = model._id.toLowerCase();
                                        if (text.contains(ids)) {
                                            items.remove(model);
                                            adapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }

                                } catch (Exception e) {
                                    Log.d("skt" , "tried Delete from this sheild");
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

        mSocket2.on("reload_app" , new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("skt", "Server is pushing a reboot command" + " " + args[0].toString());
                        Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
        });


        mSocket2.on("removeThis", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("skt", "delete");
                        //JSONArray data = (JSONArray) args[0];

                        JSONObject ja = (JSONObject) args[0];
                        try {
                            console.append(ja.getString("id"));
                            String id = ja.getString("id");
                            for (Review model : items) {
                                final String text = model._id.toLowerCase();
                                if (text.contains(id)) {
                                    items.remove(model);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            // return filteredModelList;
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }


    public void requestServer(String IMEI) {
        mSocket2.emit("android", IMEI);

        mSocket2.on("android_socket", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //JSONObject data = (JSONObject) args[0];
                        JSONArray jb = (JSONArray) args[0];
                        JSONObject ob = null;

                        for (int i = 0; i < jb.length(); i++) {

                            try {

                                ob = (JSONObject) jb.getJSONObject(i);

                                String f_name = ob.getString("f_name");
                                String _id = ob.getString("_id");
                                String f_id = ob.getString("f_id");
                                String f_email= ob.getString("f_email");
                                String f_status = ob.getString("f_status");
                                String f_dept = ob.getString("f_dept");
                                items.add(new Review(_id, f_id, f_name, f_email,f_status,f_dept));
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        });
    }





}
