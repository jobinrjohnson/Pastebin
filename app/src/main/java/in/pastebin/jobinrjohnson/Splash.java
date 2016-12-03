package in.pastebin.jobinrjohnson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);

                    //
                    //Thread sleapt for 1000 increase later
                    //

                    startActivity(new Intent(Splash.this, MainActivity.class));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
