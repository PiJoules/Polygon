

package com.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class VelocityControl extends Activity {

    private Button done;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // setup done button; returns to previous screen on click
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    
}
