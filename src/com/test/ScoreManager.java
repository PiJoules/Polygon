/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import android.content.Context;
import static android.content.Context.MODE_WORLD_READABLE;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Pi_Joules
 */
public class ScoreManager {
    
    private final String SCORESFILE = "scores.txt";
    private final String DELIMETER = "#";
    private final String DELIMETER2 = "##";
    private Context context;
    private String[][] scores;
    
    public ScoreManager(Context context){
        this.context = context;
        this.scores = parseScores();
    }
    
    private String readSavedData(){
        // note: stringbuffer is synchronized, stringbuilder is not, but both essentially do same
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream in = this.context.openFileInput(SCORESFILE);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            
            String readString = br.readLine();
            while (readString != null){
                datax.append(readString);
                readString = br.readLine();
            }
            
            isr.close();
        }
        // may not be able to read file because it contains nothing or does not exist
        catch (IOException e){
            clearData();
            return readSavedData();
        }
        return datax.toString();
    }
    
    private String[][] parseScores(){
        String[][] s = new String[5][2];
        String[] data = readSavedData().split(DELIMETER2);
        for (int i = 0; i < data.length; i++){
            s[i] = data[i].split(DELIMETER);
        }
        return s;
    }
    
    public String[][] getParsedScores(){
        return this.scores;
    }
    
    // check if new score is high enough to be saved
    public void checkNewScore(String name, String score){
        ArrayList<String[]> tempScores = new ArrayList<String[]>(Arrays.asList(scores));
        for (int i = 0; i < tempScores.size(); i++){
            if (Double.parseDouble(score) > Double.parseDouble(tempScores.get(i)[1])){
                String[] nextScore = {name,score};
                tempScores.add(i,nextScore);
                break;
            }
        }
        for (int i = 0; i < scores.length; i++){
            scores[i] = tempScores.get(i);
        }
        encodeScores();
    }
    
    private void encodeScores(){
        String nextScores = "";
        for (String[] score : this.scores) {
            nextScores += DELIMETER2 + score[0] + DELIMETER + score[1];
        }
        nextScores = nextScores.substring(2);
        writeData(nextScores);
    }
    
    public void clearData(){
        String base = "";
        for (int i = 0; i < 5; i++){
            base += DELIMETER2 + "..." + DELIMETER + "0";
        }
        base = base.substring(2);
        writeData(base);
    }
    
    private void writeData(String contents){
        try{
            FileOutputStream fOut = this.context.openFileOutput(SCORESFILE, MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut); 

            // Write the string to the file
            osw.write(contents);

            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();
        }
        catch (IOException e){}
    }
}
