// import classes
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGPT {

    public static String chatGPT(String prompt) {
        // constants
        String url = "https://api.openai.com/v1/chat/completions"; // TODO: pick endpoint
        String apiKey = "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"; // TODO: API key
        String model = "gpt-4.1"; // TODO: pick model

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

        System.out.println(chatGPT("Analyze the photo and extract any personal information you can from it, for example names, locations, phone numbers, date of birth, driver's license numbers, emails, etc.")); //ADDED NEW PROMPT

    }
}