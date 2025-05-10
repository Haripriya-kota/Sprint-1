package com.InterviewSimulator.service;

import okhttp3.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Service class to interact with OpenAI's GPT model and generate suggestions, answers, and interview questions.
 */
public class ChatGPTService {

    // OpenAI API key (ensure to keep this secure in a real application)
    private static final String OPENAI_API_KEY = "your-openai-api-key-here";
    
    // OpenAI API endpoint for chat completions
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    // OkHttpClient instance to handle HTTP requests
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Method to get an improvement suggestion from the GPT model.
     * 
     * @param question The interview question.
     * @param userAnswer The user's answer to the question.
     * @param correctAnswer The correct answer to the question.
     * @return A string containing the improvement suggestion.
     */
    public String getImprovementSuggestion(String question, String userAnswer, String correctAnswer) {
        try {
            // Create the message to send to OpenAI's API
            JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", String.format(
                    "The question was: \"%s\"\nUser's answer: \"%s\"\nCorrect answer: \"%s\".\n" +
                    "Give an improvement suggestion to the user in 1-2 sentences.",
                    question, userAnswer, correctAnswer));

            // Build the JSON payload for the API request
            JSONObject json = new JSONObject()
                .put("model", "gpt-3.5-turbo") // Use the GPT-3.5 model
                .put("messages", new org.json.JSONArray().put(message)) // Attach the message to the API request
                .put("temperature", 0.7); // Set temperature for response randomness

            // Prepare the request body
            RequestBody body = RequestBody.create(
                json.toString(), MediaType.parse("application/json"));

            // Build the HTTP request
            Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY) // Add API key in header
                .post(body) // Set request type to POST
                .build();

            // Execute the request and process the response
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Failed: " + response);

                // Parse the response body to get the improvement suggestion
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                return responseJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim(); // Return the improvement suggestion
            }
        } catch (Exception e) {
            // Return error message if something goes wrong
            return "Error generating suggestion: " + e.getMessage();
        }
    }
    
    /**
     * Helper method to call the OpenAI API with a custom prompt.
     * 
     * @param prompt The prompt to be sent to the API.
     * @return The response from OpenAI's GPT model.
     */
    private String callOpenAIAPI(String prompt) {
        try {
            // Create the message to send to OpenAI's API
            JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", prompt);

            // Build the JSON payload for the API request
            JSONObject json = new JSONObject()
                .put("model", "gpt-3.5-turbo") // Use the GPT-3.5 model
                .put("messages", new org.json.JSONArray().put(message)) // Attach the message to the API request
                .put("temperature", 0.7); // Set temperature for response randomness

            // Prepare the request body
            RequestBody body = RequestBody.create(
                json.toString(), MediaType.parse("application/json"));

            // Build the HTTP request
            Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY) // Add API key in header
                .post(body) // Set request type to POST
                .build();

            // Execute the request and process the response
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Failed: " + response);

                // Parse the response body to get the content
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                return responseJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim(); // Return the content of the response
            }
        } catch (Exception e) {
            // Return error message if something goes wrong
            return "{\"error\": \"" + e.getMessage() + "\"}"; // Return error as JSON string
        }
    }
    
    /**
     * Method to fetch API response from a given URL.
     * 
     * @param apiUrl The API URL to call.
     * @return The API response as a string.
     */
    public String fetchApiResponse(String apiUrl) {
        try {
            // Create the connection to the URL
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Set the request method to GET
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            // Read the response from the API
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine); // Append each line of the response
            }
            in.close(); // Close the BufferedReader
            
            return response.toString(); // Return the API response as a string
        } catch (Exception e) {
            e.printStackTrace(); // Log any exception
            return null; // Return null if there's an error
        }
    }

    /**
     * Method to generate an interview question from the ChatGPT API.
     * 
     * @return A JSONObject containing the question and options in JSON format.
     */
    public JSONObject generateInterviewQuestionFromChatGPT() {
        // Define the prompt to ask ChatGPT to generate a technical interview question
        String prompt = "Generate 1 technical interview question for a software engineer. " +
                        "Include the question, 4 options (A, B, C, D), and the correct answer in JSON format like: " +
                        "{\"question\": \"...\", \"options\": {\"A\": \"...\", \"B\": \"...\", \"C\": \"...\", \"D\": \"...\"}, \"correct_answer\": \"B\"}";

        // Call the API with the prompt
        String response = callOpenAIAPI(prompt); // Call the existing API call method

        // Parse the response as JSON and return it
        return new JSONObject(response);
    }

}
