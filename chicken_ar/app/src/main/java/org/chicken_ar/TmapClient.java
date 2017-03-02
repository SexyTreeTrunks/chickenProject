package org.chicken_ar;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by chicken on 2017-02-13.
 */

public class TmapClient extends AsyncTask<String, Void, Document>{
    private HttpURLConnection connection;
    private Document responseDocument;
    private final static String resourceURIString = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&format=xml&appKey=6835d463-8e64-3a23-bccd-0a6abfe672e4";
    private ArrayList<Location> pathPoints;

    @Override
    protected Document doInBackground(String... params) {
        try {
            connection = (HttpURLConnection) setRequestPayloadURL(params[0],params[1]).toURL().openConnection();
            setConnectionRequestSetting();
            setRequestHeader(); //얘는 안써도 상관 무
            getResponseData();
            return responseDocument;
        }catch (Exception ex) {
            Log.e("-----TmapClient error","",ex);
        }
        return null;
    }

    private void setConnectionRequestSetting() {
        try {
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (Exception ex) {
            Log.e("----error----","connection setting error",ex);
        }
    }

    private void setRequestHeader() {
        connection.setRequestProperty("Accept", "application/xml");
    }

    private URI setRequestPayloadURL(String startX, String startY) throws Exception{
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("startX", startX));
        nameValuePairs.add(new BasicNameValuePair("startY", startY));
        nameValuePairs.add(new BasicNameValuePair("endX", "126.969354"));
        nameValuePairs.add(new BasicNameValuePair("endY", "37.544257"));
        nameValuePairs.add(new BasicNameValuePair("startName", "start"));
        nameValuePairs.add(new BasicNameValuePair("endName", "end"));
        nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
        nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));

        URI uri = new URI(resourceURIString);
        uri = new URIBuilder(uri).addParameters(nameValuePairs).build();

        return uri;
    }

    private void getResponseData() {
        try{
            int HttpResult = connection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                Log.i("----info----","HttpRequest Success");
                responseDocument = parseXML(connection.getInputStream());
                Log.i("----info----","document = "+responseDocument);
            }else{
                Log.e("----error----","HttpResult: "+Integer.toString(HttpResult));
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String errorString = null;
                while((errorString = br.readLine())!=null) {
                    Log.e("----httpErrorStream",errorString);
                }
            }
        }catch(Exception ex) {
                ex.printStackTrace();
        }
    }

    private Document parseXML(InputStream inputStream) {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try{
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
            doc = objDocumentBuilder.parse(inputStream);
        }catch(Exception ex){
            Log.e("----parseXML error",ex.toString());
        }
        return doc;
    }

    //TODO: Documnet내의 path point 정보 추출해서 ArrayList에 저장하는 코드 구현 필요
    public ArrayList<Location> getPathPoints() {
        return pathPoints;
    }
}
