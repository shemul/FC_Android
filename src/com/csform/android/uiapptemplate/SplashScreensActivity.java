package com.csform.android.uiapptemplate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.csform.android.uiapptemplate.data.Review;
import com.csform.android.uiapptemplate.splash.font.FontelloTextView;
import com.csform.android.uiapptemplate.splash.font.view.kbv.KenBurnsView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SplashScreensActivity extends Activity {

    public  Button letsgo;
    private KenBurnsView mKenBurns;
    private FontelloTextView mLogo;
    private TextView welcomeText;
    public TextSwitcher textSwitcher;
    public Boolean isFaculty = false;
    public String[] animTexts = new String[3];
    public String[] animTexts2 = new String[3];
    public String _id, f_status ,f_name;
    TextView imei, usermac, routerssid, routermac, status;
    Button btnNext;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://socket-shemul.c9.io:8080");
            Log.d("skt", "succeded connection");
        } catch (URISyntaxException e) {

            Log.d("skt", e.getMessage());
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d("skt", "Splash destroying");

        //    mSocket.disconnect();
       //
        //mHandler.removeCallbacks(timerTask);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        setContentView(R.layout.activity_splash_screen);

        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mLogo = (FontelloTextView) findViewById(R.id.logo);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        mKenBurns.setImageResource(R.drawable.sno);
        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        letsgo = (Button) findViewById(R.id.letsgo);
        letsgo.setVisibility(View.INVISIBLE);
        letsgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFaculty) {
                    Log.d("skt", "hmm");
                    Intent facultyIntent = new Intent(SplashScreensActivity.this, Faculty.class);
                    facultyIntent.putExtra("_id" , _id);
                    facultyIntent.putExtra("f_status", f_status);
                    facultyIntent.putExtra("f_name" , f_name);
                    Log.d("skt" , f_name);
                    startActivity(facultyIntent);
                    finish();
                } else {
                    Log.d("skt", "Student is happening");
                    startActivity(new Intent(SplashScreensActivity.this, MainActivity.class));
                    mSocket.disconnect();
                    finish();
                }
            }
        });
        animation1();
        animation2();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String dev_imei = telephonyManager.getDeviceId();

        WifiManager wifiMan = (WifiManager) this.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String dev_usermac = wifiInf.getMacAddress();
        String router_mac = wifiInf.getBSSID();
        String router_ssid = wifiInf.getSSID();
        final List<Review> items = new ArrayList<Review>();


        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                TextView t = new TextView(getApplicationContext());
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
                t.setTypeface(face);
                t.setTextColor(getResources().getColor(R.color.white));

                t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                return t;

            }
        });


        if(!isOnline()) {
            textSwitcher.setText("Internet Unavailable");
           // Toast.makeText(SplashScreensActivity.this, "No net", Toast.LENGTH_SHORT).show();
        } else {
            mSocket.on("entrance", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    SplashScreensActivity.this.runOnUiThread(new Runnable() {
                        String f_imei;

                        @Override
                        public void run() {
                            JSONArray jb = (JSONArray) args[0];
                            Log.d("skt", "in entrance");
                            JSONObject ob = null;

                            for (int i = 0; i < jb.length(); i++) {

                                try {
                                    ob = (JSONObject) jb.getJSONObject(i);
                                    f_imei = ob.getString("f_imei");
                                    f_name = ob.getString("f_name");
                                    _id = ob.getString("_id");
                                    f_status = ob.getString("f_status");


                                    if (f_imei.equals(dev_imei)) {
                                        // status.setText("User Detected : " + model.f_name);
                                        //Log.d("skt", "Matched");
                                        isFaculty = true;
                                        animTexts = new String[]{"Connecting to Server", "Checking credentials", "Welcome " + f_name};
                                        mHandler.post(timerTask);
                                        //mSocket.disconnect();
                                        break;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            if (!isFaculty) {
                                //mSocket.disconnect();
                                animTexts = new String[]{"Connecting to Remote", "Identifying user", "Welcome Student !"};
                                mHandler.post(timerTask);
                            } else {

                            }

                        }
                    });
                }
            });

            mSocket.connect();
        }


        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);

    }

    int i = 0;
    private Handler mHandler = new Handler();
    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            textSwitcher.setText(animTexts[i]);
            mHandler.postDelayed(timerTask, 2000);
            if(i==2) {
                mHandler.removeCallbacks(timerTask);
                letsgo.setVisibility(View.VISIBLE);
                if(!isFaculty){
                   // mSocket.disconnect();
                }
            } else {
                i++;
            }
        }

    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(1);
    }

    public  boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if(nInfo!=null && nInfo.isConnectedOrConnecting()){

            return true;
        } else {

            return false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Animation depends on category.
     */
    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

}
