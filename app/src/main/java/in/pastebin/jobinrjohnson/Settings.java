package in.pastebin.jobinrjohnson;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class Settings extends AppCompatActivity {

    LinearLayout llclear, llaboutpbin, llaboutpdev, llpolicy, llstar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("user", MODE_PRIVATE);

        llaboutpbin = (LinearLayout) findViewById(R.id.llaboutpbin);
        llaboutpdev = (LinearLayout) findViewById(R.id.llaboutpdev);
        llpolicy = (LinearLayout) findViewById(R.id.llpolicy);
        llstar = (LinearLayout) findViewById(R.id.llstar);
        llclear = (LinearLayout) findViewById(R.id.llclear);

        llpolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, PrivacyPolicy.class));
            }
        });

        llaboutpdev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jobinrjohnson.in/"));
                startActivity(browserIntent);
            }
        });

        llstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });

        llaboutpbin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pastebin.com/faq"));
                startActivity(browserIntent);
            }
        });

        llclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setTitle("Delete all data")
                        .setMessage("This will clear all your accounts")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Do", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor ediit = sp.edit();
                                ediit.clear();
                                ediit.commit();

                                Intent intent = new Intent(Settings.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });

    }
}
