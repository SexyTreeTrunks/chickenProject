package org.chicken_ar;

import java.util.ArrayList;
import java.util.List;

public class DiningListResponse {
    private List<DiningInfo> result = new ArrayList<DiningInfo>();

    public List<DiningInfo> getResult() {
        return result;
    }
    public void setResult(List<DiningInfo> result) {
        this.result = result;
    }
}
