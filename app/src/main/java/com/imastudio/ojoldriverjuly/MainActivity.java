package com.imastudio.ojoldriverjuly;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imastudio.ojoldriverjuly.fcm.MyFirebaseInstanceIDService;
import com.imastudio.ojoldriverjuly.helper.HeroHelper;
import com.imastudio.ojoldriverjuly.helper.LocationMonitoringService;
import com.imastudio.ojoldriverjuly.helper.SessionManager;
import com.imastudio.ojoldriverjuly.model.ResponseDetailDriver;
import com.imastudio.ojoldriverjuly.model.ResponseLoginRegis;
import com.imastudio.ojoldriverjuly.network.InitRetrofit;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "1sd";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 2;
    private SessionManager manager;
    private String token;
    private Timer timer;
    private boolean mAlreadyStartedService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new SessionManager(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        110);


            }
            return;
        }
        if (manager.getGcm().isEmpty()){
            MyFirebaseInstanceIDService  idService = new MyFirebaseInstanceIDService();
          //  idService.tokensaya();
           String tokenw= idService.tokenku();
            token = FirebaseInstanceId.getInstance().getToken();
            manager.setGcm(tokenw);
            Toast.makeText(this, "fcm anda"+ token, Toast.LENGTH_SHORT).show();
            insertTokenFCMtoDB(token);
        }
        HeroHelper.cekStatusGPS(MainActivity.this);
        timer = new Timer();
        AsyncTaskTimer();
        setLocationDriver();
    }

    private void setLocationDriver() {
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                sendLocation(latitude, longitude);
                Log.d("myLatlong: ", latitude + "," + longitude);
            }
        }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));

    }

    private void sendLocation(String latitude, String longitude) {
        String iddriver = manager.getIdUser();
        String token = manager.getToken();
        String device = HeroHelper.getDeviceUUID(this);
        InitRetrofit.getInstance().insert_posisi_driver(token,device,latitude,iddriver, longitude).enqueue(new Callback<ResponseDetailDriver>() {
            @Override
            public void onResponse(Call<ResponseDetailDriver> call, Response<ResponseDetailDriver> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("true")) {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDetailDriver> call, Throwable t) {
                Toast.makeText(MainActivity.this, "gagal"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTaskTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void AsyncTaskTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TimerTask task =  new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            cekplayservice();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                timer.schedule(task,0,10000);
            }
        });
    }

    private void cekplayservice() {
        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {
            DialogInterface dialogInterface = null;
            //Passing null to indicate that it is executing for the first time.
            cekkoneksidevice(dialogInterface);

        } else {
            Toast.makeText(getApplicationContext(), "playserc", Toast.LENGTH_LONG).show();
        }

    }

    private boolean cekkoneksidevice(DialogInterface dialogInterface) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialogInterface != null) {
            dialogInterface.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            aktifkanservice();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("no internet");
        builder.setMessage("no internet");

        String positiveText = "refresh";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (cekkoneksidevice(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                aktifkanservice();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(int i, int ok, View.OnClickListener onClickListener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(i),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(ok), onClickListener).show();
    }

    private void aktifkanservice() {
        if (!mAlreadyStartedService) {

            Log.d(TAG, "service start");
            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isGooglePlayServicesAvailable() {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
            if (status != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(status)) {
                    googleApiAvailability.getErrorDialog(this, status, 2404).show();
                }
                return false;
            }
            return true;
    }


    private void insertTokenFCMtoDB(String token1) {
        String iduser = manager.getIdUser();
        Log.d("tokenku",token1+":");
        InitRetrofit.getInstance().insertFCM(iduser, token1).enqueue(new Callback<ResponseLoginRegis>() {
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                String result= response.body().getResult();
                String msg=response.body().getMsg();
                if (result.equals("true")){
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {

            }
        });
    }

    public void onHistory(View view) {
startActivity(new Intent(this,HistoryActivity.class));
    }
}
