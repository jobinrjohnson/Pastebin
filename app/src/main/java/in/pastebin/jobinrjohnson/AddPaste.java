package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class AddPaste extends AppCompatActivity {

    LinearLayout llFirstStep;
    RelativeLayout ll3rdStep;
    Button btnProceed;
    ImageButton btnClose, ibCopyUrl, ibViewPaste, ibDelete, ibShare;
    EditText etPasteName, etPasteText;
    Spinner spPastePrivacy;
    String name, privacy, pasteText, result;
    int step = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paste);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        llFirstStep = (LinearLayout) findViewById(R.id.llFirstStep);
        ll3rdStep = (RelativeLayout) findViewById(R.id.ll3rdStep);

        etPasteName = (EditText) findViewById(R.id.etPasteName);
        etPasteText = (EditText) findViewById(R.id.etPastetext);
        spPastePrivacy = (Spinner) findViewById(R.id.spPastePrivacy);

        btnProceed = (Button) findViewById(R.id.btnProceed);
        btnClose = (ImageButton) findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomePost();
            }
        });

        ibCopyUrl = (ImageButton) findViewById(R.id.ibCopyUrl);
        ibViewPaste = (ImageButton) findViewById(R.id.ibViewPaste);
        ibDelete = (ImageButton) findViewById(R.id.ibDelete);
        ibShare = (ImageButton) findViewById(R.id.ibShare);

        ibCopyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("paste", result);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(AddPaste.this, "Link copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

        ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = result;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pastebin url");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
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

    void navigateStep(boolean up) {
        if (up) {
            step++;
            if (step == 1) {
                llFirstStep.setVisibility(View.GONE);
                ll3rdStep.setVisibility(View.VISIBLE);
            } else if (step == 2) {
                doSomePost();
            } else {
                --step;
            }
        } else {
            if (step == 0)
                finish();
            else {
                llFirstStep.setVisibility(View.VISIBLE);
                ll3rdStep.setVisibility(View.GONE);
                step = 0;
            }
        }
    }

    @Override
    public void onBackPressed() {
        navigateStep(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateStep(false);
                break;
        }
        return true;
    }


    private class ServerPaste extends AsyncTask<String, Void, String> {

        HashMap<String, String> postData;
        String dataReturned;
        boolean status = false, apiStatus = false;
        int type;

        ProgressDialog progressDialog;

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
                request = new PastebinRequest(params[0], AddPaste.this);
                request.postData(postData);
                if (request.resultOk()) {
                    status = true;

                    if (request.isApiError()) {
                        dataReturned = request.getApiErrors();
                    } else {
                        dataReturned = request.getResponse();
                        apiStatus = true;
                    }
                }
            } catch (Exception e) {
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
            progressDialog = new ProgressDialog(AddPaste.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Sending your paste...");
            progressDialog.show();
            Toast.makeText(AddPaste.this, postData.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (status) {
                Toast.makeText(AddPaste.this, dataReturned, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AddPaste.this, "Nothing returned", Toast.LENGTH_LONG).show();
            }
        }
    }

}
