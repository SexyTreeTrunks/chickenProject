package org.chicken_ar;

/**
 * Created by chicken on 2017-03-19.
 */

public class DiningInfoListViewItem {
    private String name;
    private float ratingStar;
    private int distance;

    public void setName(String name) {
        this.name = name;
    }

    public void setRatingStar(float ratingStar) {
        this.ratingStar = ratingStar;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public float getRatingStar() {
        return ratingStar;
    }
}
