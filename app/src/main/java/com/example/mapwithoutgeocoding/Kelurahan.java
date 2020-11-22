package com.example.mapwithoutgeocoding;

public class Kelurahan {
    private String nama;
    private Double latitude, longitude;

    public Kelurahan(String nama, Double latitude, Double longitude) {
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getNama() {
        return nama;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
}
