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

public class DataUpload extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //loading = ProgressDialog.show(getApplicationContext(), "Please Wait", null, true, true);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //loading.dismiss();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            if (params.length == 4) {
                Log.i("****DataUpload", "user의 review data upload");
                String name = (String) params[0];
                String userId = (String) params[1];
                String ratingStars = (String) params[2];
                String contents = (String) params[3];
                String link = "http://6172d84f.ngrok.io/input_review.php";
                String data = URLEncoder.encode("restaurantName", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                data += "&" + URLEncoder.encode("ratingStars", "UTF-8") + "=" + URLEncoder.encode(ratingStars, "UTF-8");
                data += "&" + URLEncoder.encode("contents", "UTF-8") + "=" + URLEncoder.encode(contents, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                Log.i("****doInBackground", name+","+userId+","+ratingStars+","+contents);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    Log.d("****DataUpload","line - " + line);
                    break;
                }
                wr.close();
                reader.close();
                return sb.toString();
            } else {
                Log.i("****DataUpload", "user가 계산한 ratingStars upload");
                String type = (String) params[0];
                String restaurantName = (String) params[1];
                String ratingStars = (String) params[2];
                String link = "http://6172d84f.ngrok.io/input_review.php";
                String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                data += "&" + URLEncoder.encode("restaurantName", "UTF-8") + "=" + URLEncoder.encode(restaurantName, "UTF-8");
                data += "&" + URLEncoder.encode("ratingStars", "UTF-8") + "=" + URLEncoder.encode(ratingStars, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

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
                return sb.toString();
            }
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
