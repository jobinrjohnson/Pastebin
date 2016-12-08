package in.pastebin.jobinrjohnson;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jobin on 12/3/2016.
 */
public class PastebinRequest {

    HttpURLConnection conn;
    Context context;
    String returned;

    public PastebinRequest(String murl, Context mcontext) throws IOException {
        context = mcontext;
        URL url = new URL(murl);
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
    }

    public void setToGet() throws ProtocolException {
        conn.setRequestMethod("GET");
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public boolean resultOk() {
        try {
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getResponse() {
        return returned;
    }

    public String getResponseAsIs() {
        return returned;
    }

    public void readStream() throws IOException {
        String response = "";
        if (resultOk()) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += ((response.equals("") ? "" : System.getProperty("line.separator")) + line);
            }
        }
        returned = response;
    }

    public String getApiErrors() throws IOException {
        String errors = "", line, api_error = context.getResources().getString(R.string.api_bad_req_code);
        String[] lines = getResponse().split(System.getProperty("line.separator"));

        for (int i = 0; i < lines.length; i++) {
            line = lines[i];
            if (line.contains(api_error)) {
                errors += line.substring(line.indexOf(api_error) + api_error.length(), line.length());
            }
        }

        return errors;
    }

    public boolean isApiError() {
        String response = getResponse();
        if (response.contains(context.getResources().getString(R.string.api_bad_req_code)) || response == "" || response == null || response == " ") {
            return true;
        } else {
            return false;
        }
    }


    public void postData(HashMap postData) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(postData));

        writer.flush();
        writer.close();
        os.close();
        readStream();
    }

}
