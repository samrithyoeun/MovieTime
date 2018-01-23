package yoeun.samrith.movie_time;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mister_Brown on 11/21/2016.
 */

public class Cinema {
    private int id;
    private String name;
    private float lat;
    private float lng;
    private String url;
    private String logo;
    private String description;
    private String price;
    private String phone;

    public Cinema(int id, String name, float lat, float lng, String url, String logo, String description, String price,String phone) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.url = url;
        this.logo = logo;
        this.description = description;
        this.price = price;
        this.phone =phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LatLng getLatlng(){
        return new LatLng(lat,lng);
    }



}
