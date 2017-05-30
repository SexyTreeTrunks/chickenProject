package org.chicken_ar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chicken on 2017-03-22.
 */

public class ReviewListResponse {

    private List<ReviewInfo> result = new ArrayList<ReviewInfo>();

    public List<ReviewInfo> getResult() {
        return result;
    }
    public void setResult(List<ReviewInfo> result) {
        this.result = result;
    }
}
