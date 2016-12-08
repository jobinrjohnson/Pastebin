package in.pastebin.jobinrjohnson;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class UserHome extends AppCompatActivity {

    SharedPreferences sp;
    Button btnLogout, btnNewpaste, btnViewPaste;
    ImageView ivUser;
    TextView tvUsername, tvEmail, tvWeb, tvLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        sp = getSharedPreferences("user", MODE_PRIVATE);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnNewpaste = (Button) findViewById(R.id.btnNewpaste);
        btnViewPaste = (Button) findViewById(R.id.btnViewPaste);
        ivUser = (ImageView) findViewById(R.id.ivUser);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvWeb = (TextView) findViewById(R.id.tvWeb);
        tvLoc = (TextView) findViewById(R.id.tvLoc);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserHome.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to logout")
                        .setPositiveButton("Cancel", null)
                        .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor ediit = sp.edit();
                                ediit.clear();
                                ediit.commit();

                                Intent intent = new Intent(UserHome.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setIcon(R.drawable.ic_menu_send)
                        .show();
            }
        });

        btnNewpaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserHome.this, AddPaste.class));
                finish();
            }
        });

        btnViewPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        setUpUser();
    }

    void setUpUser() {
        Picasso.with(UserHome.this).load(sp.getString("user_avatar_url", "http://pastebin.com/i/guest.png")).into(ivUser);
        tvUsername.setText(sp.getString("user_name", "Guest"));
        tvEmail.setText(sp.getString("user_email", "Unknown Email"));
        tvWeb.setText(sp.getString("user_website", "Unknown Web address"));
        tvLoc.setText(sp.getString("user_location", "Unknown Location"));
    }

}
