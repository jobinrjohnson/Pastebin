package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.HashMap;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.classifier.CodeProcessor;

public class ViewPaste extends AppCompatActivity {

    CodeView codeView;
    FloatingActionButton fab, fab1Share, fab2copy, fab3delete;
    Animation fab_open, fab_close, rotate_forward, rotate_backward;
    Boolean isFabOpen = false;
    String result = "", paste_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1Share = (FloatingActionButton) findViewById(R.id.fab1Share);
        fab2copy = (FloatingActionButton) findViewById(R.id.fab2copy);
        fab3delete = (FloatingActionButton) findViewById(R.id.fab3delete);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });
        fab2copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("paste", result);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Text copied to clipboard", Snackbar.LENGTH_LONG)
                        .setAction("Done", null).show();
                animateFAB();
            }
        });

        Bundle extras = getIntent().getExtras();
        paste_id = extras.getString("paste_id");
        if (extras.containsKey("paste_name")) {
            getSupportActionBar().setTitle(extras.getString("paste_name"));
        }

        fab1Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "http://pastebin.com/" + paste_id;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pastebin url");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                animateFAB();
            }
        });

        if (!extras.containsKey("mine")) {
            fab3delete.setVisibility(View.GONE);
        }

        new ServerPaste(0).execute("http://pastebin.com/raw/" + paste_id);

        CodeProcessor.init(this);
        codeView = (CodeView) findViewById(R.id.code_view);

    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1Share.startAnimation(fab_close);
            fab2copy.startAnimation(fab_close);
            fab3delete.startAnimation(fab_close);

            fab1Share.setClickable(false);
            fab2copy.setClickable(false);
            fab3delete.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1Share.startAnimation(fab_open);
            fab2copy.startAnimation(fab_open);
            fab3delete.startAnimation(fab_open);

            fab1Share.setClickable(true);
            fab2copy.setClickable(true);
            fab3delete.setClickable(true);
            isFabOpen = true;

        }
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
        ProgressDialog pd;

        ServerPaste(int type) {
            this.type = type;
        }

        ServerPaste() {
            type = 0;
        }

        public HashMap<String, String> getRawPostData() {
            HashMap<String, String> data = new HashMap<>();
            return data;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                PastebinRequest request = new PastebinRequest(params[0], ViewPaste.this);
                request.postData(postData);
                if (request.resultOk()) {
                    dataReturned = request.getResponseAsIs();
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
            pd = new ProgressDialog(ViewPaste.this);
            pd.setIndeterminate(true);
            pd.setTitle("Loding Paste..");
            pd.setMessage("Please wait.");
            pd.show();
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
                result = dataReturned;
                codeView.setCode(result);
            } else {
                Toast.makeText(ViewPaste.this, "Some error occured.", Toast.LENGTH_LONG).show();
                finish();
            }
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }


}
