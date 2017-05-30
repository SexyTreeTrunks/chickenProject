package org.chicken_ar;


public class DiningInfo {
    private String id;
    private String name;
    private String longitude;
    private String latitude;
    private int ratingStars;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() { return longitude; }

    public String getLatitude() {
        return latitude;
    }

    public void setratingStar(int ratingStar) {
        this.ratingStars = ratingStar;
    }

    public int getratingStar() {
        return ratingStars;
    }
    
}
