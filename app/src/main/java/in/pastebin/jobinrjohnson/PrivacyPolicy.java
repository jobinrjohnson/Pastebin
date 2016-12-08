package in.pastebin.jobinrjohnson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        TextView tvPolicy = (TextView) findViewById(R.id.tvPolicy);
        tvPolicy.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy)));

    }
}
