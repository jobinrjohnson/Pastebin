package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
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
    FloatingActionButton fab, fab1, fab2;
    Animation fab_open, fab_close, rotate_forward, rotate_backward;
    Boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

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
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action2", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                animateFAB();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action22", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                animateFAB();
            }
        });

        Bundle extras = getIntent().getExtras();
        String paste_id = extras.getString("paste_id");
        if (extras.containsKey("paste_name")) {
            getSupportActionBar().setTitle(extras.getString("paste_name"));
        }

        new ServerPaste(0).execute("http://pastebin.com/raw/" + paste_id);

        CodeProcessor.init(this);
        codeView = (CodeView) findViewById(R.id.code_view);

    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
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
                codeView.setCode(dataReturned);
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
