// import classes
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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

//        System.out.println(chatGPT("hello, how are you? Can you tell me what's a Fibonacci Number?")); // TODO: change prompt

    }
}