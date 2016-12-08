package in.pastebin.jobinrjohnson;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class UserHome extends AppCompatActivity {

    SharedPreferences sp;
    Button btnLogout, btnNewpaste, btnViewPaste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnNewpaste = (Button) findViewById(R.id.btnNewpaste);
        btnViewPaste = (Button) findViewById(R.id.btnViewPaste);

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

    }

}
