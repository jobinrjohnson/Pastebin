package in.pastebin.jobinrjohnson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jobin on 12/3/2016.
 */
public class PastebinRequest {

    HttpURLConnection conn;

    public PastebinRequest(URL url) throws IOException {
        conn = (HttpURLConnection) url.openConnection();
    }

}
