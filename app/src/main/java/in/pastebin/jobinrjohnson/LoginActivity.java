package in.pastebin.jobinrjohnson;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LoginActivity extends AppCompatActivity {

    EditText etusername, etpassword;
    Button btnSignin, btnSignup;
    TextView tvForgotPass;

    String username, password, user_key;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        sp = getSharedPreferences("user", MODE_PRIVATE);

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
        boolean status = false, apistatus = false;
        int type;

        ProgressDialog progressDialog;

        LoginPbin(int type) {
            this.type = type;
        }

        LoginPbin() {
            type = 0;
        }

        public HashMap<String, String> getLoginData() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            data.put("api_user_name", username);
            data.put("api_user_password", password);
            return data;
        }

        public HashMap<String, String> getUserDataMap() {
            HashMap<String, String> data = new HashMap<>();
            data.put("api_option", "userdetails");
            data.put("api_dev_key", getResources().getString(R.string.api_key));
            data.put("api_user_key", user_key);
            return data;
        }

        @Override
        protected String doInBackground(String... params) {

            PastebinRequest request = null;
            try {
                request = new PastebinRequest(params[0], LoginActivity.this);
                request.postData(postData);
                if (request.resultOk()) {
                    status = true;

                    if (request.isApiError()) {
                        dataReturned = request.getApiErrors();
                    } else {
                        dataReturned = request.getResponse();
                        apistatus = true;
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
                    postData = getLoginData();
                    break;
                case 1:
                    postData = getUserDataMap();
                default:
                    postData = new HashMap<>();
            }
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Logging you in...");
            progressDialog.show();
        }

        private String getValue(String tag, Element element) {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }

        public boolean parseUserData() {
            SharedPreferences.Editor editor = sp.edit();
            DocumentBuilderFactory factory;
            DocumentBuilder builder;
            NodeList nList;
            String userData[] = new String[5];

            String modedData = "<?xml version=\"1.0\"?>\n" +
                    "<records>" + dataReturned + "\t\n" +
                    "</records>";
            factory = DocumentBuilderFactory.newInstance();
            try {
                builder = factory.newDocumentBuilder();
                StringReader sr = new StringReader(modedData);
                InputSource is = new InputSource(sr);
                Document d = builder.parse(is);
                nList = d.getElementsByTagName("paste");

                Node node = nList.item(0);
                final Element element = (Element) node;

                editor.putString("user_name", getValue("user_name", element));
                editor.putString("user_avatar_url", getValue("user_avatar_url", element));
                editor.putString("user_website", getValue("user_website", element));
                editor.putString("user_email", getValue("user_email", element));
                editor.putString("user_location", getValue("user_location", element));
                editor.putString("user_account_type", getValue("user_account_type", element));

                editor.apply();

                return true;

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (type == 1 && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (status) {
                if (apistatus) {
                    switch (type) {
                        case 0:
                            user_key = dataReturned.trim();
                            new LoginPbin(1).execute(getResources().getString(R.string.api_url) + "api_login.php");
                            break;
                        case 1:
                            parseUserData();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;
                    }
                } else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Unable to login")
                            .setMessage(dataReturned)
                            .setPositiveButton("Try Again", null)
                            .setNegativeButton("Forgot Password", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pastebin.com/passmailer"));
                                    startActivity(browserIntent);
                                }
                            })
                            .setIcon(R.drawable.error)
                            .show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Unable to connect", Toast.LENGTH_LONG).show();
            }
        }
    }

}
