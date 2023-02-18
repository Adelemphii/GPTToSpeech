package io.github.adelemphii.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.adelemphii.objects.exceptions.GPTException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ChatGPT {
    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String parseCompletion(String json) throws IOException, GPTException {
        JsonNode node = mapper.readTree(json).get("choices");
        if(node == null) {
            return null;
        }

        for(JsonNode choice : node) {
            String parsed = choice.get("text").asText();
            if(parsed != null) {
                return parsed;
            }
        }

        throw new GPTException("No valid completion found when parsing response JSON!");
    }

    public static String getCompletion(String prompt, int maxTokens) throws IOException {
        System.out.println("Prompt: " + prompt);
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + System.getenv("CHATGPT_API_TOKEN"));

        connection.setDoOutput(true);
        connection.getOutputStream().write((
                "{\"model\": \"text-davinci-003\", "
                        + "\"prompt\": \"" + prompt
                        + "\", \"max_tokens\": " + maxTokens
                        + ", \"temperature\": 0}")
                .getBytes());

        connection.connect();

        if(connection.getResponseCode() != 200) {
            throw new IOException("Invalid response code from OpenAI API: "
                    + Arrays.toString(connection.getInputStream().readAllBytes()));
        }

        return new String(connection.getInputStream().readAllBytes());
    }

    public static String getCompletionFromTranscription(String transcription, int maxTokens) throws IOException, GPTException {
        return parseCompletion(getCompletion(transcription, maxTokens));
    }

    public static int calculateTokens(String text) {
        return text.length() / 4;
    }
}
