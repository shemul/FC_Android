package com.csform.android.uiapptemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csform.android.uiapptemplate.data.Review;
import com.csform.android.uiapptemplate.data.ReviewAdapter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

;


public class MainActivity extends AppCompatActivity {

    public static Context contex ;
    Toolbar toolbar;
    private Button sendButton;
    private TextView console;
    ListView listView;
    public static ReviewAdapter adapter;
    public RecyclerView recyclerView;

    private void attemptSend() throws JSONException {

        JSONObject obj = new JSONObject();
        obj.put("fname", "Jhon Doe");
        obj.put("fdept", "EEE");
        obj.put("status", "Available");


        mSocket2.emit("client", obj);
    }

    public static Socket mSocket2;

    {
        try {
            mSocket2 = IO.socket("https://socket-shemul.c9.io:8080");
            Log.d("skt", "Student Socket succeded connection");
        } catch (URISyntaxException e) {

            Log.d("skt", e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("skt", "destroying");
        mSocket2.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contex = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Faculty Cloud");
        toolbar.setSubtitle("Alpha");
        this.setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        mSocket2.connect();

        //List<String> where = new ArrayList<String>();
        final List<Review> items = new ArrayList<Review>();

        recyclerView.setAdapter(adapter = new ReviewAdapter(items, R.layout.fb));
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        console = (TextView) findViewById(R.id.textView);
        console.setMovementMethod(new ScrollingMovementMethod());

        Log.d("skt", "socket 2 is staring");
        //mSocket2.connect();
        mSocket2.on("entrance", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                Log.d("skt", "Student Entrance");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //JSONObject data = (JSONObject) args[0];
                        JSONArray jb = (JSONArray) args[0];
                        JSONObject ob = null;

                        for (int i = 0; i < jb.length(); i++) {

                            try {

                                ob = (JSONObject) jb.getJSONObject(i);
                                Log.d("entrance2" , ob.toString());
                                String f_name = ob.getString("f_name");
                                String _id = ob.getString("_id");
                                String f_id = ob.getString("f_id");
                                String f_email= ob.getString("f_email");
                                String f_status = ob.getString("f_status");
                                items.add(new Review(_id, f_id, f_name, f_email,f_status));
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        });


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
                            String _id = msg.getString("_id");
                            String f_id = msg.getString("f_id");
                            String f_name = msg.getString("f_name");
                            String f_email = msg.getString("f_email");
                            String f_status = msg.getString("f_status");

                            items.add(new Review(_id, f_id, f_name, f_email,f_status));
                            adapter.notifyDataSetChanged();
                            //items.add(msg.getString("f_name"));
                            //listView.setAdapter(adapter);

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
                        Log.d("socketeting", "updating");
                        //JSONArray data = (JSONArray) args[0];

                        JSONObject ja = (JSONObject) args[0];
                        try {
                            JSONArray ob = ja.getJSONArray("item");
                            JSONArray ts = ob.getJSONArray(0);
                            JSONObject sd = ts.getJSONObject(0);
                            String f_name = sd.getString("f_name");
                            String _id = sd.getString("_id");
                            String f_id = sd.getString("f_id");
                            String f_email = sd.getString("f_email");
                            String f_status = sd.getString("f_status");
                            // console.append(f_name);
                            //values.remove(tmp);
                            for (Review model : items) {
                                final String text = model._id.toLowerCase();
                                if (text.contains(_id)) {
                                    //items.remove(model);
                                    //items.add();
                                    int a = items.indexOf(model);
                                    items.set(a, new Review(_id, f_id, f_name, f_email,f_status));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            // return filteredModelList;
                            adapter.notifyDataSetChanged();

                            //listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

        mSocket2.on("removeThis", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("socketeting", "delete");
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



}
