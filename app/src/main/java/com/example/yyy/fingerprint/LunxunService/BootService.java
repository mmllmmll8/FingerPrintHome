package com.example.yyy.fingerprint.LunxunService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.example.yyy.fingerprint.LoginRegister.AddressUtil;
import com.example.yyy.fingerprint.LoginRegister.Keys;
import com.example.yyy.fingerprint.LoginRegister.PostOptions;
import com.example.yyy.fingerprint.LoginRegister.PostThread;
import com.example.yyy.fingerprint.MainlistFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BootService extends Service {



    public MyBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public BootService getService()
        {
            return BootService.this;
        }
    }

    MainlistFragment fragment;
    public void setContext(MainlistFragment fragment){
        this.fragment=fragment;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    //onCreate()在服务创建的时候调用
    @Override
    public void onCreate() {
        super.onCreate();

//        Log.e("Service", "Oncreate");
//

    }

    //onStartCommand()是处理事情的逻辑方法
    //在每次服务启动的时候调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                myNotificationManager.notify(NOTIFICATION_ID,notify);//发送通知
//                //myNotificationManager.cancel(NOTIFICATION_ID,notify);//取消通知
//                //Log.e("BootService", "onStartCommand");
//
//            }
//
//        }).start();


       connect();


        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);//获取到了AlarmManager的实例
        int anMinute = 2*1000;//定义触发时间为5s之后

        long triggerAtTime = SystemClock.elapsedRealtime() + anMinute;
        //使用SystemClock.elapsedRealtime()可以获取到系统开机至今所经历的时间

        //发送广播的方法Intent，接收广播的方法BroadcastReceiver-广播接收器
        Intent i = new Intent(this,BootBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        // PendingIntent.getBroadcast获取能够执行广播的PendingIntent.这样当定时任务被触发的时候，广播接收器的onReceive()方法就可以得到执行

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        //ELAPSED_REALTIME_WAKEUP为AlarmManager的工作类型，表示让定时任务的触发时间从系统开机算起，并会唤醒CPU
        //用PendingIntent指定处理定时任务的广播接收器为AlarmReceiver（自定义类），最后用set方法完成Alarm的设定

        return super.onStartCommand(intent, flags, startId);
    }


    public void connect() {
        SharedPreferences userSettings= getSharedPreferences("isLoginsetting", 0);
        if(userSettings.getBoolean("isLogin",false)){

            new SynchroThread(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL,fragment).start();

            //getRun(Keys.USER_ID, Keys.IMEI, AddressUtil.LOGIN_URL);


        }
    }

//    public void getRun(String user_id, String IMEI, String url) {
//
//        if (TextUtils.isEmpty(url)) {
//            Log.e("SynchroURL","EMPTY");
//            throw new NullPointerException("please ensure url is not equals  null ");
//        }
//        BufferedReader bufferedReader = null;
//        try {
//            URL httpUrl = new URL(url);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
//            //设置请求头header
//            httpURLConnection.setRequestProperty("test-header", "post-header-value");
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setReadTimeout(5000);
//
//            OutputStream outputStream = httpURLConnection.getOutputStream();
//            String params = "model=" + PostOptions.SYNCHRO
//                    + "&user_id=" + URLEncoder.encode(user_id, "utf-8")
//                    + "&IMEI=" + URLEncoder.encode(IMEI, "utf-8");
//            outputStream.write(params.getBytes());
//
//            //获取内容
//            InputStream inputStream = httpURLConnection.getInputStream();
//            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            final StringBuffer stringBuffer = new StringBuffer();
//            String line = null;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            String response = stringBuffer.toString();
//            response = PostThread.decrypt(response);
//            Log.d("response", response);
//            List<Synchro> synchros = new ArrayList<>();
//            String[] sourceStrArray = response.split("&");
//            Log.d("length", sourceStrArray.length+"");
//            for (int i = 0; i < sourceStrArray.length; i+=6) {
//                String cpu_id = sourceStrArray[i].split("=")[1];
//                Log.d("cpu_id", cpu_id);
//                String file_path = sourceStrArray[i+1].split("=")[1];
//                Log.d("file_path", file_path);
//                String authority_number = sourceStrArray[i+2].split("=")[1];
//                Log.d("authority_number", authority_number);
//                String operate_date = sourceStrArray[i+3].split("=")[1];
//                Log.d("operate_date", operate_date);
//                String operate_time = sourceStrArray[i+4].split("=")[1];
//                Log.d("operate_time", operate_time);
//                String isPermit = sourceStrArray[i+5].split("=")[1];
//                Log.d("isPermit", isPermit);
//
//                Synchro synchro = new Synchro(cpu_id, file_path, authority_number, operate_date, operate_time, isPermit);
//                synchros.add(synchro);
//            }
//
//            Message message = new Message();
//            message.arg1 = 1;
//            message.obj = synchros;
//            fragment.handler.sendMessage(message);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }



    //onDestroy()在服务销毁的时候调用
    //在该方法中回收那些不再使用的资源
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e("Service", "OnDestory");
    }

}