package com.example.mapwithoutgeocoding;

public class Kelurahan {
    private String nama, soundex;
    private Double latitude, longitude;

    public Kelurahan(String nama, Double latitude, Double longitude, String soundex) {
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
        this.soundex = soundex;
    }
    public Kelurahan() {

    }

    public void setNama(String nama) {
        this.nama = nama;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public void setSoundex(String soundex) {
        this.soundex = soundex;
    }

    public String getNama() {
        return nama;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public String getSoundex() {
        return soundex;
    }
}
