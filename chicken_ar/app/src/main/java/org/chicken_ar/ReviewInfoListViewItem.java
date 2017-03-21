package org.chicken_ar;

/**
 * Created by chicken on 2017-03-22.
 */

public class ReviewInfoListViewItem {
    private String userId;
    private float ratingStars;
    private String contents;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRatingStars(float ratingStars) {
        this.ratingStars = ratingStars;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUserId() {
        return userId;
    }

    public float getRatingStars() {
        return ratingStars;
    }

    public String getContents() {
        return contents;
    }
}
