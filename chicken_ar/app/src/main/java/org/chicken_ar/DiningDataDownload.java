package org.chicken_ar;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class DiningDataDownload extends AsyncTask<Integer, Void, DiningListResponse> {
    private String URL;

    public DiningDataDownload(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onLoaded(List<DiningInfo> diningInfoList);
        void onError();
    }

    private Listener mListener;

    @Override
    protected DiningListResponse doInBackground(Integer... category_input) {
        try {
            setRequestURLByType(category_input[0]);

            String stringResponse = loadJSON(URL);
            Gson gson = new Gson();

            return gson.fromJson(stringResponse, DiningListResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.d("****LoadJSON","GSON error");
            return null;
        }
    }

    private void setRequestURLByType(int type) {
        switch (type) {
            case CategoryType.CAFE:
                URL = "http://6172d84f.ngrok.io/get_cafe_json.php";
                break;
            case CategoryType.DINING_KOREA:
                URL = "http://6172d84f.ngrok.io/get_dining_korean_json.php";
                break;
            case CategoryType.DINING_SNACK:
                URL = "http://6172d84f.ngrok.io/get_dining_snack_json.php";
                break;
            case CategoryType.DINING_JAPANESE:
                URL = "http://6172d84f.ngrok.io/get_dining_japanese_json.php";
                break;
            case CategoryType.DINING_CHINESE:
                URL = "http://6172d84f.ngrok.io/get_dining_chinese_json.php";
                break;
            case CategoryType.DINING_WESTERN:
                URL = "http://6172d84f.ngrok.io/get_dining_western_json.php";
                break;
        }
    }

    @Override
    protected void onPostExecute(DiningListResponse response) {

        if (response != null) {
            mListener.onLoaded(response.getResult());
        } else {
            mListener.onError();
        }
    }

    private String loadJSON(String jsonURL) throws IOException {
        URL url = new URL(jsonURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        int httpConnResult = conn.getResponseCode();
        if(httpConnResult == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
                Log.i("****loadJSON", "http_ok: " + line);
            }
            in.close();
            return response.toString();
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                Log.i("****loadJSON", "http_error: " + line);
            }
            in.close();
            return null;
        }
    }
}

