package com.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class FilterControl extends Activity {
    
    private Button alpha, sma, none, done;
    private TextView filter_type, description;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle); // call the parent class' onCreate method
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.filter_control_layout);
        
        filter_type = (TextView) findViewById(R.id.filter_type);
        description = (TextView) findViewById(R.id.description);
        
        alpha = (Button) findViewById(R.id.alpha);
        alpha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filter_type.setText(alpha.getText());
                description.setText(R.string.alpha_description);
                
            }
        });
        
        sma = (Button) findViewById(R.id.sma);
        sma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filter_type.setText(sma.getText());
                description.setText(R.string.sma_description);
            }
        });
        
        none = (Button) findViewById(R.id.none);
        none.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filter_type.setText(none.getText());
                description.setText(R.string.none_description);
            }
        });
        
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
    }
    
}
