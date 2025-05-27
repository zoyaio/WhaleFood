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
        String apiKey = "";
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

        String imageURLS = "imageUrls.csv";
        String chatGPTresponses = "chatGPTresponses.csv";
        List<String> prompts = new ArrayList<>();

        // website object for pi questions and user info
        Website security = new Website();
        String userInfo = security.userInfo();



        // prompt chatgpt to analyze instagram photo for PI
        System.out.println(chatGPT("Analyze the following images, looking for answers to the given security questions " +
                "and corresponding answers. If you were able to make an approximation or guess that you believe could be accurate" +
                " to any of the questions that matches at least somewhat with the provided answer, " +
                "output the question that you could answer with your approximate answer, in the following example format: " +
                "Q5: What's your high school best friend's name?\n " +
                "A5: Based on these (insert specific features) found in this image (insert image url), the answers is (insert name).\n" +
                "These are the provided questions/answers: " + userInfo));

        // read img urls from csv
        try (BufferedReader br = new BufferedReader(new FileReader(imageURLS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String clean = line.trim().replaceAll("^\"|\"$", "");
                if (!clean.isEmpty()) {
                    prompts.add(clean);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading CSV inputs: " + e.getMessage());
            return;
        }

        // feed each url into chatgpt and store the responses into the new csv file for responses
        try (FileWriter csvWriter = new FileWriter(chatGPTresponses, true)) {
            for (String prompt : prompts) {
                String response = chatGPT(prompt);
                System.out.println("CHATGPT: " + response);
                String sanitized = "\"" + response.replace("\"", "\"\"") + "\"";
                csvWriter.append(sanitized).append(",");
            }
            csvWriter.append("\n");
        }
        catch (IOException e) {
            System.out.println("Error writing output to CSV: " + e.getMessage());
        }

    }
}