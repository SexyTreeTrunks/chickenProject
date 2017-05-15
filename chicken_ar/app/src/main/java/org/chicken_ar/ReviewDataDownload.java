package org.chicken_ar;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by chicken on 2017-03-22.
 */

public class ReviewDataDownload extends AsyncTask<String, Void, ReviewListResponse> {

    public ReviewDataDownload(ReviewDataDownload.Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onLoaded(List<ReviewInfo> reviewInfoList);
        void onError();
    }

    private ReviewDataDownload.Listener mListener;

    @Override
    protected void onPostExecute(ReviewListResponse response) {
        if (response != null) {
            mListener.onLoaded(response.getResult());
        } else {
            mListener.onError();
        }
    }

    @Override
    protected ReviewListResponse doInBackground(String... params) {
        try {
            String stringResponse = loadJSON(params[0]);
            Gson gson = new Gson();
            return gson.fromJson(stringResponse, ReviewListResponse.class);
        } catch (IOException e) {
            Log.e("****ReviewDownload","io exception");
            return null;
        } catch (Exception e) {
            Log.e("****ReviewDownload","other exception");
            return null;
        }
    }

    private String loadJSON(String restaurantName) throws IOException {
        String URL = "http://6172d84f.ngrok.io/get_review_json.php";
        URL url = new URL(URL);
        URLConnection conn =  url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoOutput(true);
        conn.setDoOutput(true);
        conn.connect();

        String data = URLEncoder.encode("restaurantName", "UTF-8") + "=" + URLEncoder.encode(restaurantName, "UTF-8");

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {
            response.append(line);
            Log.i("****loadJSON", "http_ok: " + line);
        }
        wr.close();
        in.close();

        return response.toString();
    }
}
