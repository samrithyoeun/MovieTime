package yoeun.samrith.movie_time;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Mister_Brown on 11/27/2016.
 */

public class NetworkManager {

    public static void checkNetwork(final Context context,View view){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected()))
        {
            Snackbar.make(view,"មិនអាចភ្ជាប់ internet បានទេ dear! ",Snackbar.LENGTH_INDEFINITE)
            .setAction("SETTING", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        })
                .setActionTextColor(context.getResources().getColor(R.color.colorAccent))
                .show();
        }

    }

    public static void checkGPS (final Context mContext, View view){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        if( ! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Snackbar.make(view,"មិនអាចភ្ជាប់ GPS បានទេ dear! ",Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTING", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })
                    .setActionTextColor(mContext.getResources().getColor(R.color.colorAccent))
                    .show();
        }
    }
}
