package com.dbvertex.dilsayproject.Swipe;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by janisharali on 29/08/16.
 */
public class Profile {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("age")
    @Expose
    private String age;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("instaverified")
    @Expose
    private String instaverified;

    public String getInstaverified() {
        return instaverified;
    }

    public void setInstaverified(String instaverified) {
        this.instaverified = instaverified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
