package com.hw4.juri.hw4;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
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

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "CivicInfoDownloader";
    private String apk_key = "AIzaSyCZLX6xpcnK3nG0EggqPv-ra423TiYHQ68";
    private MainActivity mainActivity;

    public CivicInfoDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=" + apk_key + "&address="+strings[0];
        Uri dataUri = Uri.parse(CIVIC_URL);
        String dataUrl = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(dataUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){
            Toast.makeText(mainActivity.getApplicationContext(),
                    "Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            mainActivity.setOfficialList(null);
        }else if(s.length() == 0){
            Toast.makeText(mainActivity.getApplicationContext(),
                    "No data is available for the specified location", Toast.LENGTH_SHORT).show();
            mainActivity.setOfficialList(null);
        }else{
            ArrayList<Official> officialList = parseJSON(s);
            String location = parseLocation(s);
            Object[] result = {location, officialList};

            mainActivity.setOfficialList(result);
        }
    }

    private String parseLocation(String s){
        String location = "";
        try {
            JSONObject jCivic = new JSONObject(s);
            JSONObject jLocation = jCivic.getJSONObject("normalizedInput");

            String city = jLocation.getString("city");
            String state = jLocation.getString("state");
            String zip = jLocation.getString("zip");

            if(!city.equals("")){
                location  = location + city + ", ";
            }
            location = location + state + " " + zip;
            return location;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Official> parseJSON(String s) {
        try {
            ArrayList<Official> officialList = new ArrayList<Official>();
            // Get location
            JSONObject jCivic = new JSONObject(s);

            // Get office
            JSONArray jOffice = jCivic.getJSONArray("offices");
            JSONArray jOfficial = jCivic.getJSONArray("officials");
            for(int i=0;i<jOffice.length();i++){
                String officeName = ((JSONObject)jOffice.get(i)).getString("name");
                JSONArray jIndex = ((JSONObject)jOffice.get(i)).getJSONArray("officialIndices");

                for(int j=0;j<jIndex.length();j++){
                    // Get official
                    int index = (Integer) jIndex.get(j);
                    JSONObject jObj = (JSONObject) jOfficial.get(index);

                    String name = jObj.getString("name");
                    String address = parseAddress(jObj);
                    String party = jObj.has("party")?jObj.getString("party"):"Unknown";
                    String phone = parsePhone(jObj);
                    String website = parseWebsite(jObj);
                    String email = parseEmail(jObj);
                    String photo = jObj.has("photoUrl")?jObj.getString("photoUrl"):"";
                    String gpID = parseChannels(jObj)[0];
                    String fbID = parseChannels(jObj)[1];
                    String twID = parseChannels(jObj)[2];
                    String ytbID = parseChannels(jObj)[3];

                    Official official = new Official(officeName, name, party, photo, address, phone, email, website,
                            gpID, fbID, twID,ytbID);
                    officialList.add(official);
                }
            }
            return officialList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String parseAddress(JSONObject jObj) throws JSONException {
        String address = "";
        if(jObj.has("address")){
            JSONArray jAdds = jObj.getJSONArray("address");
            JSONObject jAdd = (JSONObject)jAdds.get(0);
            String line1 = jAdd.has("line1")?jAdd.getString("line1"):"";
            String line2 = jAdd.has("line2")?jAdd.getString("line2"):"";
            String city = jAdd.has("city")?jAdd.getString("city"):"";
            String state = jAdd.has("state")?jAdd.getString("state"):"";
            String zip = jAdd.has("zip")?jAdd.getString("zip"):"";

            String lines3 = city + " , " + state + " " + zip;
            address = line1 + "\n" + line2 + "\n" + lines3;
        }else{
            address = "No Data Provided";
        }
        return address;
    }

    private String parsePhone(JSONObject jObj) throws JSONException {
        String phone = "";
        if(jObj.has("phones")){
            JSONArray jPhones = jObj.getJSONArray("phones");
            phone = jPhones.get(0).toString();
        }else{
            phone = "No Data Provided";
        }
        return phone;
    }

    private String parseWebsite(JSONObject jObj)throws JSONException {
        String website = "";
        if(jObj.has("urls")){
            JSONArray jWeb = jObj.getJSONArray("urls");
            website = jWeb.get(0).toString();
        }else{
            website = "No Data Provided";
        }
        return website;
    }

    private String parseEmail(JSONObject jObj)throws JSONException{
        String email = "";
        if(jObj.has("emails")){
            JSONArray jEmail = jObj.getJSONArray("emails");
            email = jEmail.get(0).toString();
        }else{
            email = "No Data Provided";
        }
        return email;
    }

    private String[] parseChannels(JSONObject jObj) throws JSONException {
        String[] channels = {"", "", "", ""};
        if (jObj.has("channels")) {
            JSONArray jChannel = jObj.getJSONArray("channels");
            for (int i = 0; i < jChannel.length(); i++) {
                JSONObject ch = (JSONObject) jChannel.get(i);
                if (ch.getString("type").equals("GooglePlus")) {
                    channels[0] = ch.getString("id");
                }
                if (ch.getString("type").equals("Facebook")) {
                    channels[1] = ch.getString("id");
                }
                if (ch.getString("type").equals("Twitter")) {
                    channels[2] = ch.getString("id");
                }
                if (ch.getString("type").equals("YouTube")) {
                    channels[3] = ch.getString("id");
                }
            }
        }
        return channels;
    }
}
