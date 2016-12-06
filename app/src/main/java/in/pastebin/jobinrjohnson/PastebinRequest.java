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

    public String getResponse() throws IOException {
        String response = "";
        if (resultOk()) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        return response;
    }

    public String getResponseAsIs() throws IOException {
        String response = "";
        if (resultOk()) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += (line + System.getProperty("line.separator"));
            }
        }
        return response;
    }

    public String getApiErrors() throws IOException {
        String errors = "", line, api_error = context.getResources().getString(R.string.api_bad_req_code);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = br.readLine()) != null) {
            if (line.contains(api_error)) {
                errors += line.substring(line.lastIndexOf(api_error + api_error.length()), line.length());
            }
        }
        return errors;
    }

    public boolean isApiError() {
        try {
            String response = getResponse();
            if (response.contains(context.getResources().getString(R.string.api_bad_req_code))) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
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
    }

}
