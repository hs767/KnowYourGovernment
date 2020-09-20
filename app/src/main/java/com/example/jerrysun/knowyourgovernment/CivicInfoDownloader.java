package com.example.jerrysun.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jerrysun on 4/30/17.
 */

public class CivicInfoDownloader extends AsyncTask<String, Integer, String> {

    private MainActivity mainActivity;

    private final String civicInfoSearchURL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private final String myAPIKey = "AIzaSyAQzS7Q-xY4ZF7eOXmJSU07uMPK58MsTmE";
    private String searchAddress;

    public CivicInfoDownloader (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute (String s) {
        //read JSON
        if (s.isEmpty()) {
            Toast.makeText(mainActivity, "No data available for the entered location", Toast.LENGTH_LONG).show();
        }

        ArrayList<Object> resultObjList = parseJSON(s);
        if (resultObjList == null) {
            Toast.makeText(mainActivity, "The civic info service is unavailable", Toast.LENGTH_LONG).show();
        }

        mainActivity.setOfficialList(resultObjList);
    }

    @Override
    protected String doInBackground (String... params) {
        searchAddress = params[0];
        Uri.Builder builderURL = Uri.parse(civicInfoSearchURL).buildUpon();
        builderURL.appendQueryParameter("key", myAPIKey).appendQueryParameter("address", searchAddress);
        String urlToUse = builderURL.toString();

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private ArrayList<Object> parseJSON(String s) {
        ArrayList<Object> resultObjList = new ArrayList<>();
        String location;
        ArrayList<Official> officialList = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject normalizedInput = jObjMain.getJSONObject("normalizedInput");

            location = normalizedInput.getString("city")
                    + ", "
                    + normalizedInput.getString("state")
                    + " "
                    + normalizedInput.getString("zip");

            JSONArray offices = jObjMain.getJSONArray("offices");
            JSONArray officials = jObjMain.getJSONArray("officials");

            for (int i = 0; i < offices.length(); i++) {
                JSONObject jObjOffice = (JSONObject) offices.get(i);

                String officeName = "";
                if (jObjOffice.has("name")) {
                    officeName = jObjOffice.getString("name");
                } else {
                    officeName = "No Data Provided";
                }

                JSONArray officialIndices = jObjOffice.getJSONArray("officialIndices");

                for (int j = 0; j < officialIndices.length(); j++) {

                    JSONObject jObjOfficial = (JSONObject) officials.get((int) officialIndices.get(j));

                    String name = "";
                    String address = "";
                    String party = "";
                    String phones = "";
                    String emails = "";
                    String urls = "";
                    String photoUrl = "";
                    Map<String, String> channels = new HashMap<String, String>();

                    if (jObjOfficial.has("name")) {
                        name = jObjOfficial.getString("name");
                    } else {
                        name = "No Data Provided";
                    }

                    if (jObjOfficial.has("address")) {
                        JSONObject addressJObj = (JSONObject) jObjOfficial.getJSONArray("address").get(0);

                        String line1 = "";
                        String line2 = "";
                        String line3 = "";
                        String city = "";
                        String state = "";
                        String zip = "";

                        if (addressJObj.has("line1"))
                            line1 = addressJObj.getString("line1");
                        if (addressJObj.has("line2"))
                            line2 = addressJObj.getString("line2");
                        if (addressJObj.has("line3"))
                            line3 = addressJObj.getString("line3");

                        if (addressJObj.has("city"))
                            city = addressJObj.getString("city");
                        if (addressJObj.has("state"))
                            state = addressJObj.getString("state");
                        if (addressJObj.has("zip"))
                            zip = addressJObj.getString("zip");

                        address = line1 + " " + line2 + " " + line3 + "\n" + city + ", " + state + " " + zip;

                    } else {
                        address = "No Data Provided";
                    }

                    if (jObjOfficial.has("party")) {
                        party = jObjOfficial.getString("party");
                    } else {
                        party = "No Data Provided";
                    }

                    if (jObjOfficial.has("phones")) {
                        phones = (String) jObjOfficial.getJSONArray("phones").get(0);
                    } else {
                        phones = "No Data Provided";
                    }

                    if (jObjOfficial.has("emails")) {
                        emails = (String) jObjOfficial.getJSONArray("emails").get(0);
                    } else {
                        emails = "No Data Provided";
                    }

                    if (jObjOfficial.has("urls")) {
                        urls = (String) jObjOfficial.getJSONArray("urls").get(0);
                    } else {
                        urls = "No Data Provided";
                    }

                    if (jObjOfficial.has("photoUrl")) {
                        photoUrl = jObjOfficial.getString("photoUrl");
                    } else {
                        photoUrl = null;
                    }

                    if (jObjOfficial.has("channels")) {
                        JSONArray channelsJArray = jObjOfficial.getJSONArray("channels");

                        for (int k = 0; k < channelsJArray.length(); k++) {
                            try {
                                JSONObject jObjChannel = (JSONObject) channelsJArray.get(k);
                                String type = jObjChannel.getString("type");
                                String id = jObjChannel.getString("id");
                                channels.put(type, id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        channels = null;
                    }

                    officialList.add(new Official(officeName, name, address, party, phones, urls, emails, photoUrl, channels));
                }

            }
            //Log.d(TAG, "parseJSON: location: " + location);
            resultObjList.add(location);
            resultObjList.add(officialList);

            return resultObjList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
