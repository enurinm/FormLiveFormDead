package com.example.a190427swipetestapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/* 190512 [안드로이드] 위치정보(위도, 경도)를 주소로 변환하기 (Geocoder)
*   http://dailyddubby.blogspot.com/2018/04/104-geocoder.html
*
* */
public class GpsInfo extends Service implements LocationListener {

    private final Context mContext;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값

    boolean isGetLocation = false;


    Location location;

    double lat; // 위도

    double lon; // 경도


    // 최소 GPS 정보 업데이트 거리 10미터

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    // 최소 GPS 정보 업데이트 시간 밀리세컨(1분)

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;


    protected LocationManager locationManager;


    public GpsInfo(Context context) {

        this.mContext = context;

        getLocation();

    }


    @TargetApi(23)

    public Location getLocation() {

        if ( Build.VERSION.SDK_INT >= 23 &&

                ContextCompat.checkSelfPermission(

                        mContext, android.Manifest.permission.ACCESS_FINE_LOCATION )

                        != PackageManager.PERMISSION_GRANTED &&

                ContextCompat.checkSelfPermission(

                        mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)

                        != PackageManager.PERMISSION_GRANTED) {


            return null;

        }


        try {

            locationManager = (LocationManager) mContext

                    .getSystemService(LOCATION_SERVICE);


            // GPS 정보 가져오기

            isGPSEnabled = locationManager

                    .isProviderEnabled(LocationManager.GPS_PROVIDER);


            // 현재 네트워크 상태 값 알아오기

            isNetworkEnabled = locationManager

                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {

                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현

            } else {

                this.isGetLocation = true;

                // 네트워크 정보로 부터 위치값 가져오기

                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(

                            LocationManager.NETWORK_PROVIDER,

                            MIN_TIME_BW_UPDATES,

                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


                    if (locationManager != null) {

                        location = locationManager

                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {

                            // 위도 경도 저장

                            lat = location.getLatitude();

                            lon = location.getLongitude();


                            Log.v("알림", "위도 : " + lat + "경도 " + lon);

                        }

                    }

                }


                if (isGPSEnabled) {

                    if (location == null) {

                        locationManager.requestLocationUpdates(

                                LocationManager.GPS_PROVIDER,

                                MIN_TIME_BW_UPDATES,

                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {

                            location = locationManager

                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {

                                lat = location.getLatitude();

                                lon = location.getLongitude();

                            }

                        }

                    }

                }

            }


        } catch (Exception e) {

            e.printStackTrace();

        }

        return location;

    }


    //gps종료

    public void stopUsingGPS(){

        if(locationManager != null){

            locationManager.removeUpdates(GpsInfo.this);

        }

    }


    //위도값 가져오기

    public double getLatitude(){

        if(location != null){

            lat = location.getLatitude();

        }

        return lat;

    }


    //경도값 가져오기

    public double getLongitude(){

        if(location != null){

            lon = location.getLongitude();

        }

        return lon;

    }


    //gps가 켜져있는지 확인

    public boolean isGetLocation() {

        return this.isGetLocation;

    }


    //gps설정 정보를 가져올 수 없을 때

    public void showSettingsAlert(){

        makeDialog();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void makeDialog(){

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);

        alt_bld.setMessage("GPS 사용이 필요합니다. \n설정창으로 가시겠습니까?").setCancelable(

                false).setPositiveButton("네",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        // 네 클릭

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        mContext.startActivity(intent);

                    }

                }).setNegativeButton("아니오",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        // 아니오 클릭. dialog 닫기.

                        dialog.cancel();

                    }

                });

        AlertDialog alert = alt_bld.create();


        // 대화창 클릭시 뒷 배경 어두워지는 것 막기

        //alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        // 대화창 제목 설정

        alert.setTitle("GPS 사용 허가");


        // 대화창 배경 색 설정

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255,62,79,92)));


        alert.show();

    }

}
