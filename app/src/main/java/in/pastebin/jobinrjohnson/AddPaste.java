package in.pastebin.jobinrjohnson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class AddPaste extends AppCompatActivity {

    LinearLayout llFirstStep, ll3rdStep;
    Button btnProceed1, btnProceed3;
    EditText etPasteName, etPasteText;
    Spinner spPastePrivacy;
    String name, privacy, pasteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llFirstStep = (LinearLayout) findViewById(R.id.llFirstStep);
        ll3rdStep = (LinearLayout) findViewById(R.id.ll3rdStep);

        etPasteName = (EditText) findViewById(R.id.etPasteName);
        etPasteText = (EditText) findViewById(R.id.etPastetext);
        spPastePrivacy = (Spinner) findViewById(R.id.spPastePrivacy);

        btnProceed1 = (Button) findViewById(R.id.btnProceed1);
        btnProceed3 = (Button) findViewById(R.id.btnProceed3);

        btnProceed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llFirstStep.setVisibility(View.GONE);
                ll3rdStep.setVisibility(View.VISIBLE);
            }
        });

        btnProceed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomePost();
            }
        });

    }

    private void doSomePost() {
        String url = getResources().getString(R.string.api_url) + "api_post.php";
        name = etPasteName.getText().toString();
        pasteText = etPasteText.getText().toString();
        privacy = spPastePrivacy.getSelectedItemPosition() + "";
        new ServerPaste().execute(url);
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

        public HashMap<String, String> getPasteData() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_option", "paste");
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            data.put("api_paste_name", name);
            data.put("api_paste_private", privacy);
            data.put("api_paste_code", pasteText);
            return data;
        }

        @Override
        protected String doInBackground(String... params) {

            PastebinRequest request = null;
            try {
                request = new PastebinRequest(params[0]);
                request.postData(postData);
                if (request.resultOk()) {
                    status = true;
                    dataReturned = request.getResponse();
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
                case 0:
                    postData = getPasteData();
                    break;
                default:
                    postData = new HashMap<>();
            }
            Toast.makeText(AddPaste.this, postData.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (status) {
                Toast.makeText(AddPaste.this, dataReturned, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AddPaste.this, "Nothing returned", Toast.LENGTH_LONG).show();
            }
        }
    }

}
