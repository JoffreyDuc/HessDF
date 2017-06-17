package com.example.duc_joffrey_esig.hessdf;

/**
 * Created by DUC_JOFFREY-ESIG on 14.03.2017.
 */

public class Association {
    private int id;
    private String name;
    private byte[] image;
    private String site;
    private double latitude;
    private double longitude;
    private double distance;

    public Association(int id, String name, byte[] image, String site, double latitude, double longitude, double distance) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.site = site;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
