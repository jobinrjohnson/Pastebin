package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;

public class AddPaste extends AppCompatActivity {

    LinearLayout llFirstStep;
    RelativeLayout ll3rdStep;
    Button btnProceed;
    ImageButton btnClose, ibCopyUrl, ibViewPaste, ibDelete, ibShare;
    EditText etPasteName, etPasteText, etFinalRes;
    Spinner spPastePrivacy, spPasteFormat, spPasteExpiry;
    String name, privacy, pasteText, result, pasteid;
    String[] pasteformat, pasteExpiry;
    int step = 0;
    SharedPreferences sp;
    AdView adView;
    AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paste);

        adView = (AdView) findViewById(R.id.banner_AdView1);
        adRequest = new AdRequest.Builder()
                //.addTestDevice("4EC7E2B2060506BA2CFD947556E4CBF1")
                .build();

        sp = getSharedPreferences("user", MODE_PRIVATE);

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
        etFinalRes = (EditText) findViewById(R.id.etFinalRes);
        spPastePrivacy = (Spinner) findViewById(R.id.spPastePrivacy);
        spPasteFormat = (Spinner) findViewById(R.id.spPasteFormat);
        spPasteExpiry = (Spinner) findViewById(R.id.spPasteFormat);

        btnProceed = (Button) findViewById(R.id.btnProceed);
        btnClose = (ImageButton) findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pasteformat = getResources().getStringArray(R.array.paste_format_value);
        pasteExpiry = getResources().getStringArray(R.array.paste_expire_date_value);
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

        if (sp.contains("user_key")) {
            {
                ibDelete.setVisibility(View.VISIBLE);
            }
        }

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(AddPaste.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to delete this paste")
                        .setPositiveButton("Cancel", null)
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = getResources().getString(R.string.api_url) + "api_post.php";
                                new ServerPaste(1).execute(url);
                            }
                        })
                        .setIcon(R.drawable.ic_delete_black)
                        .show();
            }
        });

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

        ibViewPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPaste.this, ViewPaste.class);
                i.putExtra("paste_id", pasteid);
                i.putExtra("paste_name", name);
                if (sp.contains("user_key")) {
                    i.putExtra("mine", true);
                }
                startActivity(i);
                finish();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //navigateStep(false);
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
            data.put("api_paste_format", pasteformat[spPasteFormat.getSelectedItemPosition()]);
            data.put("api_paste_expire_date", pasteExpiry[spPasteExpiry.getSelectedItemPosition()]);

            if (sp.contains("user_key")) {
                data.put("api_user_key", sp.getString("user_key", ""));
            }

            return data;
        }

        public HashMap<String, String> getDeleteData() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_option", "delete");
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            data.put("api_user_key", sp.getString("user_key", ""));
            data.put("api_paste_key", pasteid);
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
            progressDialog = new ProgressDialog(AddPaste.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Please wait.");
            switch (type) {
                case 0:
                    postData = getPasteData();
                    progressDialog.setMessage("Posting your paste.");
                    break;
                case 1:
                    postData = getDeleteData();
                    progressDialog.setMessage("Deleting your paste.");
                    break;
                default:
                    postData = new HashMap<>();
            }
            progressDialog.show();
            Toast.makeText(AddPaste.this, postData.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (status) {
                if (apiStatus) {

                    if (type == 1) {
                        Toast.makeText(AddPaste.this, "Paste Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    adView.loadAd(adRequest);

                    result = dataReturned;
                    pasteid = dataReturned.substring(dataReturned.lastIndexOf("/") + 1, dataReturned.length());

                    llFirstStep.setVisibility(View.GONE);
                    ll3rdStep.setVisibility(View.VISIBLE);

                    etFinalRes.setText(result);
                    etPasteName.setEnabled(false);
                    btnProceed.setVisibility(View.GONE);


                } else {
                    new AlertDialog.Builder(AddPaste.this)
                            .setTitle("Some error occured")
                            .setMessage(dataReturned)
                            .setPositiveButton("Try Again", null)
                            .setIcon(R.drawable.error)
                            .show();
                }

            } else {
                new AlertDialog.Builder(AddPaste.this)
                        .setTitle("Unable to get date")
                        .setMessage("Request had timed out")
                        .setPositiveButton("Try Again", null)
                        .setIcon(R.drawable.error)
                        .show();
            }
        }
    }

}
