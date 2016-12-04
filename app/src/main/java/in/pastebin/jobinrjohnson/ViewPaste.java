package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

//import com.pddstudio.highlightjs.HighlightJsView;
//import com.pddstudio.highlightjs.models.Language;
//import com.pddstudio.highlightjs.models.Theme;

public class ViewPaste extends AppCompatActivity {

//    EditText etPastetext;

    HighlightJsView highlightJsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        etPastetext = (EditText) findViewById(R.id.etPastetext);
        Bundle extras = getIntent().getExtras();
        String paste_id = extras.getString("paste_id");

        //new ServerPaste(0).execute("http://pastebin.com/raw/" + paste_id);

        final ProgressDialog pd = new ProgressDialog(ViewPaste.this);
        pd.setIndeterminate(true);
        pd.setTitle("Loding Paste..");
        pd.show();

        highlightJsView = (HighlightJsView) findViewById(R.id.highlight_view);

        highlightJsView.setOnThemeChangedListener(new HighlightJsView.OnThemeChangedListener() {
            @Override
            public void onThemeChanged(@NonNull Theme theme) {

            }
        });

        //change theme and set language to auto detect
        highlightJsView.setTheme(Theme.ANDROID_STUDIO);
        highlightJsView.setHighlightLanguage(Language.AUTO_DETECT);
        try {
            highlightJsView.setSource(new URL("http://pastebin.com/raw/" + paste_id));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        highlightJsView.setOnContentChangedListener(new HighlightJsView.OnContentChangedListener() {
            @Override
            public void onContentChanged() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    private class ServerPaste extends AsyncTask<String, Void, String> {

        HashMap<String, String> postData;
        String dataReturned;
        boolean status = false;
        int type;

        ServerPaste(int type) {
            this.type = type;
        }

        ServerPaste() {
            type = 0;
        }

        public HashMap<String, String> getRawPostData() {
            HashMap<String, String> data = new HashMap<>();
            //data.put("api_option", "trends");
            //data.put("api_dev_key", getResources().getString(R.string.api_key));
            return data;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                PastebinRequest request = new PastebinRequest(params[0]);
                request.postData(postData);
                if (request.resultOk()) {
                    dataReturned = request.getResponse();
                    status = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (type) {
                case 0:             //for trending posts
                    postData = getRawPostData();
                    break;
                default:            //default hashmap
                    postData = new HashMap<>();
            }
        }


        private String getValue(String tag, Element element) {
            try {
                NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
                Node node = nodeList.item(0);
                return node.getNodeValue();
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (status) {

//                etPastetext.setText(dataReturned.replaceAll("\n", System.getProperty("line.separator")));

            } else {
                Toast.makeText(ViewPaste.this, "Nothing returned", Toast.LENGTH_LONG).show();
            }
        }
    }


}
