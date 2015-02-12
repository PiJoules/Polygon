package com.test;

// Android imports
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

// Activity for the main menu screen
public class MainMenu extends Activity {

    // Buttons
    private Button play, settings;
    // Table that shows the high scores and names
    private TableLayout table, table2;
    // The text fields containing the high scores and names
    private TextView[][] tvs, table2tvs;
    
    // Method called when the main menu screen is created. Overrides onCreate method of Activity
    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        
        //Remove title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // set to landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.main_menu_layout);
        
        // Play button that starts the game
        play = (Button) findViewById(R.id.play);
        // Play button event handler
        play.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent playScreen = new Intent(getApplicationContext(), GameActivity.class);
                // Starts the game
                startActivity(playScreen);
            }
        });
        
        // Settings button that takes the player to the change settings screen
        settings = (Button) findViewById(R.id.settings);
        // Settings button event handler
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsScreen = new Intent(getApplicationContext(), Settings.class);
                // Sends the user to the change settings screen
                startActivity(settingsScreen);
            }
        });
        
        updateScores();
    }
    

    // Method called when the user reopens the app. Overrides the onResume method of Activity
    @Override
    protected void onResume(){
        super.onResume();
        updateScores();
    }
    
    private void updateScores(){
        ScoreManager sm = new ScoreManager(this);
        String[][] scores = sm.getParsedScores();
                
        // Create table if it doesn't already exist
        if (table == null){
            table = (TableLayout) findViewById(R.id.table);
            tvs = new TextView[scores.length][scores[0].length];
            for (int i = 0; i < scores.length; i++){
                tvs[i][0] = (TextView) findViewById(R.id.name0 + 2*i);
                tvs[i][1] = (TextView) findViewById(R.id.name0 + 2*i+1);
            }
        }
        // Show high scores and names
        for (int i = 0; i < scores.length; i++){
            tvs[i][0].setText(scores[i][0]);
            tvs[i][1].setText(scores[i][1]);
        }
        
        int table2rows = 5;
        int table2cols = 2;
        if (table2 == null){
            table2 = (TableLayout) findViewById(R.id.table2);
            table2tvs = new TextView[table2rows][table2cols];
            for (int i = 0; i < table2rows; i++){
                table2tvs[i][0] = (TextView) findViewById(R.id.table2name0 + 2*i);
                table2tvs[i][1] = (TextView) findViewById(R.id.table2name0 + 2*i+1);
            }
        }
        
        HTTPManager httpm = new HTTPManager(this);
        if (httpm.post(sm.getScoreString(), sm.DELIMETER, sm.DELIMETER2)){
            ScoreManager smGlobal = new ScoreManager(this,"global_scores.txt");
            smGlobal.writeData(httpm.getHttpResponse());
            if (smGlobal.readSavedData()){
                smGlobal.resetParsedScores();
                String[][] globalScores = smGlobal.getParsedScores();
                for (int i = 0; i < globalScores.length; i++){
                    table2tvs[i][0].setText(globalScores[i][0]);
                    table2tvs[i][1].setText(globalScores[i][1]);
                }
            }
        }
    }
    
}
