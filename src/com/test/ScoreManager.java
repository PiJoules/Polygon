package com.test;

// Android import
import android.content.Context;

// Java imports
import java.util.ArrayList;
import java.util.Arrays;

// An object to handle keeping track of high scores
public class ScoreManager extends FileManager {
    // File to save scores to    
    private final String SCORESFILE = "scores.txt";

    // Delimiters to use to format saved scores
    private final String DELIMETER = "#";
    private final String DELIMETER2 = "##";
    
    // The array of high scores and name of player that got each score
    private String[][] scores;

    // Constructor
    public ScoreManager(Context context, String fileName) {
        // Call FileManager constructor
        super(context, fileName);
        
        // Set file to save to
        this.setFile(SCORESFILE);

        // Create the high scores file
        this.scores = parseScores();
    }
    
    // Read files from file and return them in a structured list
    private String[][] parseScores(){
        String[][] s = new String[5][2];
        String[] data = null;

        // Try to read the high scores file
        if (readSavedData()){
            // Remove delimeters
            data = this.getContents().split(DELIMETER2);
            // Go through each formatted score from the file
            for (int i = 0; i < data.length; i++){
                // Remove delimeter and save the actual score and name
                s[i] = data[i].split(DELIMETER);
            }

            return s;
        }
        else {
            // High scores file doesn't exist. Create empty dummy list
            clearData();
            // Call this method again to format new dummy list
            return parseScores();
        }
    }

    // Getter method for high scores    
    public String[][] getParsedScores(){
        return this.scores;
    }
    
    // check if new score is high enough to be saved
    public void checkNewScore(String name, String score){
        ArrayList<String[]> tempScores = new ArrayList<String[]>(Arrays.asList(scores));

        // Go through the list of scores
        for (int i = 0; i < tempScores.size(); i++){
            // Check if new score is higher than current score
            if (Double.parseDouble(score) > Double.parseDouble(tempScores.get(i)[1])){
                // Score is high enough to be added to the list
                String[] nextScore = {name,score};
                // Add score to list
                tempScores.add(i,nextScore);
                break; // Score has been added, break out of loop
            }
        }

        // Save highest scores       
        for (int i = 0; i < scores.length; i++){
            scores[i] = tempScores.get(i);
        }
        // Format the scores to be written to a file
        encodeScores();
    }

    // Format the scores to be saved to the high scores file    
    private void encodeScores(){
        String nextScores = "";

        // Go through all scores and format them into one string
        for (String[] score : this.scores) {
            nextScores += DELIMETER2 + score[0] + DELIMETER + score[1];
        }

        // Remove the first delimiter
        nextScores = nextScores.substring(2);

        // Call the writeData method to write the text to a file
        writeData(nextScores);
    }
    
    // Delete all saved scores and replace them with dummy scores
    public void clearData(){
        String base = "";
        for (int i = 0; i < 5; i++){
            base += DELIMETER2 + "..." + DELIMETER + "0";
        }

        // Remove the first delimiter
        base = base.substring(2);

        // Call the writeData method to write the text to a file
        this.writeData(base);
    }
}
