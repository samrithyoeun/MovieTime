package yoeun.samrith.movie_time;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mister_Brown on 11/23/2016.
 */

public interface MLocationListener {
    void getLastKnownLocation(LatLng lastLocation);

}
