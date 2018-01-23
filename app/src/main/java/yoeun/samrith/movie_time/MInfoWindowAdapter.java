package yoeun.samrith.movie_time;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Mister_Brown on 11/21/2016.
 */

public class MInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;


    public MInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {



        View v = LayoutInflater.from(context).inflate(R.layout.info_layout,null);
        TextView txtName = (TextView) v.findViewById(R.id.txtName);
        ImageView imgLogo = (ImageView)v.findViewById(R.id.infoLogo);

        txtName.setText(marker.getTitle()+"\n"+marker.getTag());

        try {
            InputStream ims = null;
            ims = context.getAssets().open(marker.getSnippet());
            Drawable d = Drawable.createFromStream(ims, null);
            imgLogo.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;

    }
}
