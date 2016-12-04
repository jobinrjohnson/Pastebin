package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etusername, etpassword;
    Button btnSignin, btnSignup;
    TextView tvForgotPass;

    String username, password;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        sp = getSharedPreferences("user_key", MODE_PRIVATE);

        initVars();

    }

    void initVars() {
        etusername = (EditText) findViewById(R.id.etusername);
        etpassword = (EditText) findViewById(R.id.etpassword);
        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        tvForgotPass = (TextView) findViewById(R.id.tvForgotPass);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pastebin.com/signup"));
                startActivity(browserIntent);
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pastebin.com/passmailer"));
                startActivity(browserIntent);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoggingIn();
            }
        });

    }

    void tryLoggingIn() {
        username = etusername.getText().toString();
        password = etpassword.getText().toString();

        new LoginPbin(0).execute(getResources().getString(R.string.api_url) + "api_login.php");

    }


    private class LoginPbin extends AsyncTask<String, Void, String> {

        HashMap<String, String> postData;
        String dataReturned;
        boolean status = false;
        int type;

        ProgressDialog progressDialog;

        LoginPbin(int type) {
            this.type = type;
        }

        LoginPbin() {
            type = 0;
        }

        public HashMap<String, String> getPasteData() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_option", "paste");
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            data.put("api_user_name", username);
            data.put("api_user_password", password);
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
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Logging you in...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (status) {
                Toast.makeText(LoginActivity.this, dataReturned, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Nothing returned", Toast.LENGTH_LONG).show();
            }
        }
    }

}
