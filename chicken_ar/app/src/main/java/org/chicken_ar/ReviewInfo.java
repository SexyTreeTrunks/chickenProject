package org.chicken_ar;

/**
 * Created by chicken on 2017-03-22.
 */

public class ReviewInfo {
    private String restaurantName;
    private String userId;
    private int ratingStars;
    private String contents;

    public int getRatingStars() {
        return ratingStars;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getUserId() {
        return userId;
    }

    public String getContents() {
        return contents;
    }
}
