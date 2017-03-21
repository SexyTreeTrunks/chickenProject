package org.chicken_ar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by chicken on 2017-03-20.
 */

public class ReviewUpload extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        Log.i("****doInBackground", "background 진입");

        try {
            String name = (String) params[0];
            String userId = (String) params[1];
            String ratingStars = (String) params[2];
            String contents = (String) params[3];
            String link = "http://d5f7bde7.ngrok.io/input_review.php";
            String data = URLEncoder.encode("restaurantName", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
            data += "&" + URLEncoder.encode("ratingStars", "UTF-8") + "=" + URLEncoder.encode(ratingStars, "UTF-8");
            data += "&" + URLEncoder.encode("contents", "UTF-8") + "=" + URLEncoder.encode(contents, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            Log.i("****doInBackground", "url 커낵쎤");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            Log.i("****return from server", sb.toString());
            return sb.toString();

        } catch (UnsupportedEncodingException e) {
            Log.e("****encoding error","");
            return null;
        } catch (MalformedURLException e) {
            Log.e("****URL 형식 에러","");
            return null;
        } catch (IOException e) {
            Log.e("****IO error","");
            return null;
        } catch (Exception ex) {
            Log.e("****error","");
            return null;
        }

    }
}
