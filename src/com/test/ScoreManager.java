/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Pi_Joules
 */
public class ScoreManager extends FileManager {
    
    private final String SCORESFILE = "scores.txt";
    private final String DELIMETER = "#";
    private final String DELIMETER2 = "##";
    private String[][] scores;

    public ScoreManager(Context context, String fileName) {
        super(context, fileName);
        
        this.setFile(SCORESFILE);
        this.scores = parseScores();
    }
    
    private String[][] parseScores(){
        String[][] s = new String[5][2];
        //String[] data = readSavedData().split(DELIMETER2);
        String[] data = null;
        if (readSavedData()){
            data = this.getContents().split(DELIMETER2);
            for (int i = 0; i < data.length; i++){
                s[i] = data[i].split(DELIMETER);
            }
            return s;
        }
        else {
            clearData();
            return parseScores();
        }
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
        this.writeData(base);
    }
}
