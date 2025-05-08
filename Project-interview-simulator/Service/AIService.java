package com.InterviewSimulator.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class AIService {
	
	// URL of the Open Trivia Database API with predefined parameters
    private static final String TRIVIA_API_URL = "https://opentdb.com/api.php?amount=10&category=18&difficulty=easy&type=multiple";
    // Logger for logging messages and errors
    private static final Logger logger = Logger.getLogger(AIService.class.getName());

    //Fetches an AI-generated question from Open Trivia Database API.
    public JSONObject fetchTriviaQuestionData() {
    	// Call the API and get the raw JSON response as a String
        String response = callTriviaAPI(TRIVIA_API_URL);
        
        // If no response was received, return null
        if (response == null) {
            return null;
        }

        // Parse the response into a JSONObject
        JSONObject jsonResponse = new JSONObject(response);
        
        // Extract the "results" array containing the questions
        JSONArray results = jsonResponse.getJSONArray("results");

        // Return the first question if available, otherwise null
        return results.length() > 0 ? results.getJSONObject(0) : null;
    }

    /**
     * Makes an HTTP GET request to the given Trivia API URL.
     * 
     * @param apiUrl the URL to send the GET request to
     * @return the response as a String if successful, otherwise null
     */
    private String callTriviaAPI(String apiUrl) {
        try {
            // Create a new URL object
            URL url = new URL(apiUrl);
            
            // Open a connection to the API
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // Set the HTTP method to GET
            conn.setRequestProperty("Accept", "application/json"); // Set Accept header

            // If response code is not 200 (OK), log a warning and return null
            if (conn.getResponseCode() != 200) {
                logger.warning("⚠️ Trivia API returned error: " + conn.getResponseCode());
                return null;
            }

            // Read the response from the API
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            // Append each line to the response
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close(); // Close the reader

            // Return the full response as a String
            return response.toString();
        } catch (Exception e) {
            // Log any exceptions that occur
            logger.severe("❌ Trivia API Request Failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generates an improvement suggestion based on the user's average score.
     * 
     * @param avgScore the average score of the user (out of 10)
     * @return a String containing motivational or improvement suggestions
     */
    public String generateImprovementSuggestion(double avgScore) {
        if (avgScore >= 8) {
            return "🌟 Excellent performance! Keep practicing to maintain your skills.";
        } else if (avgScore >= 5) {
            return "✅ Good effort! Focus on improving accuracy and confidence.";
        } else {
            return "❌ You need improvement. Revise the concepts and practice more.";
        }
    }
}