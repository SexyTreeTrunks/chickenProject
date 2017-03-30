package org.chicken_ar;

import android.location.Location;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;

/**
 * Created by chicken on 2017-02-13.
 */

public class TmapClient extends AsyncTask<String, Void, Void>{
    private static final String APP_KEY = "6835d463-8e64-3a23-bccd-0a6abfe672e4";
    private HttpURLConnection connection;
    private Document responseDocument;
    private String resourceURIString;
    private ArrayList<Location> pathPoints;
    //private ArrayList<Location> pointList;
    private ArrayList<String> pathDescriptions;

    @Override
    protected Void doInBackground(String... params) {
        try {
            connection = (HttpURLConnection) setRequestPayloadURL(params[0], params[1], params[2], params[3]).toURL().openConnection();
            setConnectionRequestSetting();
            setRequestHeader(); //얘는 안써도 상관 무
            getResponseData();
            connection.disconnect();
            writeLog("----------------------start-----------------------");
        }catch (Exception ex) {
            Log.e("*****TmapClient error","",ex);
        }
        return null;
    }

    private void setConnectionRequestSetting() {
        try {
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (Exception ex) {
            Log.e("****TmapClient error","connection setting error",ex);
        }
    }

    private void setRequestHeader() {
        connection.setRequestProperty("Accept", "application/xml");
    }

    private URI setRequestPayloadURL(String startX, String startY, String endX, String endY) throws Exception {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("startX", startX));
        nameValuePairs.add(new BasicNameValuePair("startY", startY));
        nameValuePairs.add(new BasicNameValuePair("endX", endX));
        nameValuePairs.add(new BasicNameValuePair("endY", endY));
        //nameValuePairs.add(new BasicNameValuePair("endY", "37.544257"));
        //nameValuePairs.add(new BasicNameValuePair("endX", "126.969354"));
        nameValuePairs.add(new BasicNameValuePair("startName", "start"));
        nameValuePairs.add(new BasicNameValuePair("endName", "end"));
        nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
        nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));

        resourceURIString = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&format=xml&appKey="+APP_KEY;
        URI uri = new URI(resourceURIString);
        uri = new URIBuilder(uri).addParameters(nameValuePairs).build();

        return uri;
    }

    private void getResponseData() {
        try{
            int HttpResult = connection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                Log.i("****TmapClient info","HttpRequest Success");
                responseDocument = parseXML(connection.getInputStream());
                Log.i("****TmapClient info","document = "+responseDocument);
            }else{
                Log.e("****TmapClient error","HttpResult: "+Integer.toString(HttpResult));
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String errorString = null;
                while((errorString = br.readLine())!=null) {
                    Log.e("****httpErrorStream",errorString);
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
            Log.e("****TmapClient error","parseXML error");
        }
        return doc;
    }

    //TODO: Documnet내의 path point 정보 추출해서 ArrayList에 저장하는 코드 구현 필요
    public ArrayList<Location> getPathPoints() {
        Element root = responseDocument.getDocumentElement();
        pathPoints = new ArrayList<Location>();
        //pointList = new ArrayList<Location>();
        pathDescriptions = new ArrayList<String>();
        int hashmapKeyCount = 0;
        try {
            NodeList nodeList = root.getElementsByTagName("Placemark");
            for (int i = 1; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                String nodeType = element.getElementsByTagName("tmap:nodeType").item(0).getTextContent();
                String coordinates = null;
                if (nodeType.contains("LINE")) {
                    coordinates = element.getElementsByTagName("LineString").item(0).getTextContent();
                    Log.i("****TmapClient","coordinate: " + coordinates);
                } else {
                    coordinates = element.getElementsByTagName("Point").item(0).getTextContent();
                    Log.i("****TmapClient","coordinate: " + coordinates);
                    String description = element.getElementsByTagName("description").item(0).getTextContent();
                    pathDescriptions.add(description);
                    /*
                    StringTokenizer splitedLatLon = new StringTokenizer(coordinates, ",");
                    Location location = new Location("provider");
                    while(splitedLatLon.hasMoreElements()) {
                        location.setLongitude(Double.valueOf(splitedLatLon.nextToken()));
                        location.setLatitude(Double.valueOf(splitedLatLon.nextToken()));
                        Log.i("****PointCoordinate","longitude: " + location.getLongitude());
                        Log.i("****PointCoordinate","latitude: " + location.getLatitude());
                        pointList.add(location);
                    }
                    */
                }

                //writeLog("coordinates: " + coordinates+", Description: " + description);


                StringTokenizer splitedLocationTokens = new StringTokenizer(coordinates, " ");
                splitedLocationTokens.nextToken();
                for (int j = 0; splitedLocationTokens.hasMoreElements(); j++, hashmapKeyCount++) {
                    StringTokenizer locationTokens = new StringTokenizer(splitedLocationTokens.nextToken(), ",");
                    Location location;
                    if(nodeType.contains("LINE"))
                        location = new Location("Line");
                    else
                        location = new Location("Point");
                    for (int k = 0; locationTokens.hasMoreElements(); k++) {
                        location.setLongitude(Double.valueOf(locationTokens.nextToken()));
                        location.setLatitude(Double.valueOf(locationTokens.nextToken()));
                        pathPoints.add(location);
                    }
                }
                /*
                StringTokenizer splitedLocationTokens = new StringTokenizer(coordinates, " ");
                for (int j = 0; splitedLocationTokens.hasMoreElements(); j++, hashmapKeyCount++) {
                    StringTokenizer locationTokens = new StringTokenizer(splitedLocationTokens.nextToken(), ",");
                    Location location = new Location("provider");
                    for (int k = 0; locationTokens.hasMoreElements(); k++) {
                        location.setLongitude(Double.valueOf(locationTokens.nextToken()));
                        location.setLatitude(Double.valueOf(locationTokens.nextToken()));
                        pathPoints.add(location);
                    }
                }
                */

            }
        } catch (Exception ex) {
            Log.e("****TmapClient error","getPathPoints error");
        }
        return pathPoints;
    }

    public ArrayList<String> getPathDescriptions() {
        return pathDescriptions;
    }

    public void writeLog(String contents){
        String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
        String filename = "log.txt";
        try{
            File dir = new File(foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.newLine();
            writer.flush();
            writer.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //public ArrayList<Location> getPointList() {return pointList;}
}
