// import classes
import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatGPT {
    private static String url = "https://api.openai.com/v1/responses";
    private static String apiKey = "";
    private static String model = "gpt-4.1-mini";

    private static class Pair {
        public String promptString;
        public String imgURL;
        public Pair(String prompt, String img){
            promptString = prompt;
            imgURL = img;
        }
     }


    public static String chatGPT (String prompt, String imageURL) throws IOException {
        String reqBody = "{\n" +
                "  \"model\": \"" + model + "\",\n" +
                "  \"input\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": [\n" +
                "        {\n" +
                "          \"type\": \"input_text\",\n" +
                "          \"text\": \"" + prompt +  "\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"input_image\",\n" +
                "          \"image_url\": \"" + imageURL + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"text\": {\n" +
                "    \"format\": {\n" +
                "      \"type\": \"text\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"reasoning\": {},\n" +
                "  \"tools\": [],\n" +
                "  \"temperature\": 1,\n" +
                "  \"max_output_tokens\": 2048,\n" +
                "  \"top_p\": 1,\n" +
                "  \"store\": true\n" +
                "}";

        return callGPT(reqBody);

    }
    private static String callGPT(String bodyPrompt) throws IOException {
        URL obj = new URL(url);
        // Make the HTTP connection (with necessary parameters)
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        try {
            // sets the metadata
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // adds the body prompt to the request
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(bodyPrompt);
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
            return extractMessageFromJSONResponse(String.valueOf(response));

            // prints the error message specifically
        } catch (IOException e) {

            int status = connection.getResponseCode();
            InputStream responseStream = (status >= 200 && status < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            // Read message
            try (BufferedReader in = new BufferedReader(new InputStreamReader(responseStream))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("Response message:");
                System.out.println(response.toString());
            }

            throw new RuntimeException(e);
        }

    }

    public static String chatGPT(String prompt) throws IOException {
        // constants
        String body = "{\n" +
                "  \"model\": \"gpt-4.1\",\n" +
                "  \"input\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": [\n" +
                "        {\n" +
                "          \"type\": \"input_text\",\n" +
                "          \"text\": \"" + prompt +  "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"text\": {\n" +
                "    \"format\": {\n" +
                "      \"type\": \"text\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"reasoning\": {},\n" +
                "  \"tools\": [],\n" +
                "  \"temperature\": 1,\n" +
                "  \"max_output_tokens\": 2048,\n" +
                "  \"top_p\": 1,\n" +
                "  \"store\": true\n" +
                "}";

        return callGPT(body);

    }

    public static String extractMessageFromJSONResponse(String json) {
        // Locate the "text": " start
        String key = "\"text\": \"";
        int textStart = json.indexOf(key);
        if (textStart == -1) {
            System.out.println("Text key not found.");
            return "Error";
        }

        textStart += key.length(); // Move past the key to the actual text value
        int textEnd = json.indexOf("\"", textStart);

        // Handle escaped quotes (optional basic fix)
        while (textEnd != -1 && json.charAt(textEnd - 1) == '\\') {
            textEnd = json.indexOf("\"", textEnd + 1);
        }

        if (textEnd == -1) {
            System.out.println("Text value not properly closed.");
            return "Error";
        }

        String extractedText = json.substring(textStart, textEnd)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        return extractedText;
    }

    public static void main(String[] args, String userInfo) throws IOException {


        String basePrompt = "Analyze the following images, looking for answers to the given security questions and corresponding answers. If you were able to make an approximation or guess that you believe could be accurate to any of the questions that matches at least somewhat with the provided answer, output the question that you could answer with your approximate answer, in the following example format: Q5: What's your high school best friend's name?\\nA5: Based on these (insert specific features) found in this image (insert image url), the answer is (insert name).\\nIf you can't discern anything, please just return 'N/A'. \\nThese are the provided questions/answers: ";

        //
        // file initializations
        String imageURLS = "imageUrls.csv";
        String chatGPTresponses = "chatGPTresponses.csv";

        // clear chatgpt csv file
        try {
            FileWriter clearWriter = new FileWriter(chatGPTresponses, false); // false = overwrite mode
            clearWriter.close(); // This creates an empty file
            System.out.println("Cleared " + chatGPTresponses);
        } catch (IOException e) {
            System.out.println("Error clearing " + chatGPTresponses + ": " + e.getMessage());
        }

        List<Pair> prompts = new ArrayList<>();



        // read img urls from csv
        try (BufferedReader br = new BufferedReader(new FileReader(imageURLS))) {
            String line;
            while ((line = br.readLine()) != null) {
                // cleans string of special characters
                String clean = line.trim().replaceAll("^\"|\"$", "");
                if (!clean.isEmpty()) {
                    // adds prompt to prompt queue
                    prompts.add(new Pair(basePrompt, clean));
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading CSV inputs: " + e.getMessage());
            return;
        }
        List<Pair> promptsTruncated = prompts;
        // feed each url into chatgpt and store the responses into the new csv file for responses
        try (FileWriter csvWriter = new FileWriter(chatGPTresponses, true)) {
            for (Pair prompt : promptsTruncated) {
                // prompts gpt

                String response = chatGPT(prompt.promptString, prompt.imgURL);
                System.out.println("CHATGPT: " + response);
                // cleans response
                String sanitized = "\"" + response.replace("\"", "\"\"") + "\"";
                // writes to csv
                csvWriter.append(sanitized).append(",");
            }
            csvWriter.append("\n");
        }
        catch (IOException e) {
            System.out.println("Error writing output to CSV: " + e.getMessage());
        }

    }
}