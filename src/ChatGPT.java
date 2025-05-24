// import classes
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatGPT {

    public static String chatGPT(String prompt) {
        // constants
        String url = "https://api.openai.com/v1/chat/completions"; // TODO: pick endpoint
        String apiKey = "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"; // TODO: API key
        String model = "gpt-3.5-turbo"; // TODO: pick model

        try {
            URL obj = new URL(url);
            // Make the HTTP connection (with necessary parameters)
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The JSON request body
            // TODO: include images in this message body (if needed and idk how)
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            // I think this adds it to the HTTP request connection
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            // Process response
            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content")+ 11;

        int end = response.indexOf("\"", start);

        return response.substring(start, end);

    }



    public static void main(String[] args) {

        // take image url csv file
        String imageURLs = "imageUrls.csv";
        String chatGPTresponses = "chatGPTresponses.csv";

        List<String> prompts = new ArrayList<>();

        // read img urls from csv
        try (BufferedReader br = new BufferedReader(new FileReader(imageURLs))) {
            String line;
            while ((line = br.readLine()) != null) {
                String clean = line.trim().replaceAll("^\"|\"$", ""); // remove quotes if any
                if (!clean.isEmpty()) {
                    prompts.add(clean);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading input CSV: " + e.getMessage());
            return;
        }

        // feed each url into chatgpt and store the responses into the new csv file for chatgptresponses
        try (FileWriter csvWriter = new FileWriter(chatGPTresponses, true)) {
            for (String prompt : prompts) {
                String response = chatGPT(prompt);
                System.out.println("ChatGPT response: " + response);
                String sanitized = "\"" + response.replace("\"", "\"\"") + "\"";
                csvWriter.append(sanitized).append(","); // comma-separated
            }
            csvWriter.append("\n"); // separate each batch
        } catch (IOException e) {
            System.out.println("Error writing to output CSV: " + e.getMessage());
        }
    }


}