package in.pastebin.jobinrjohnson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AddPaste extends AppCompatActivity {

    LinearLayout llFirstStep, ll3rdStep;
    Button btnProceed1, btnProceed2;
    EditText etPasteName;
    Spinner spPastePrivacy;

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
        spPastePrivacy = (Spinner) findViewById(R.id.spPastePrivacy);

        btnProceed1 = (Button) findViewById(R.id.btnProceed1);

        btnProceed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llFirstStep.setVisibility(View.GONE);
                ll3rdStep.setVisibility(View.VISIBLE);
            }
        });
    }

}
