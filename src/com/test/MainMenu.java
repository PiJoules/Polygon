package com.test;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.Arrays;

/**
 *
 * @author Pi_Joules
 */
public class MainMenu extends Activity {

    // xml stuff
    private Button play, settings;
    private TableLayout table;
    private TextView[][] tvs;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //Remove title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // set to landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.main_menu_layout);
        
        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent playScreen = new Intent(getApplicationContext(), Test2.class);
                startActivity(playScreen);
            }
        });
        
        settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsScreen = new Intent(getApplicationContext(), Settings.class);
                startActivity(settingsScreen);
            }
        });
        
        System.out.println("Loaded Main Menu");
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        final ScoreManager sm = new ScoreManager(this);
        String[][] scores = sm.getParsedScores();
        
        if (table == null){
            table = (TableLayout) findViewById(R.id.table);
            tvs = new TextView[scores.length][scores[0].length];
            for (int i = 0; i < scores.length; i++){
                tvs[i][0] = (TextView) findViewById(R.id.name0 + 2*i);
                tvs[i][1] = (TextView) findViewById(R.id.name0 + 2*i+1);
            }
        }
        for (int i = 0; i < scores.length; i++){
            tvs[i][0].setText(scores[i][0]);
            tvs[i][1].setText(scores[i][1]);
        }
        
        System.out.println("Contents of saved data: " + Arrays.deepToString(scores));
    }
    
}
