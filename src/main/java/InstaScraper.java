import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// ImageKit imports
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;

public class InstaScraper {
    // user related
    private String username;
    private String userProfileURL;

    //paths
    private final String archivePath = "C:\\Users\\zoya\\Documents\\WhaleFood\\api\\gallery-archive.db";
    private String cookiesURL;

    // basic api command through CLI
    private List<String> basicCommand;

    // ImageKit credentials - REPLACE THESE WITH YOUR ACTUAL CREDENTIALS
    private static final String IMAGEKIT_PUBLIC_KEY = "public_SkCXpVJtajLLPTIkvwz6ZXwqTcw=";
    private static final String IMAGEKIT_PRIVATE_KEY = "private_7/9l9tYz8KRqGVqJYHCKypU85sk=";
    private static final String IMAGEKIT_URL_ENDPOINT = "https://ik.imagekit.io/whalefood2025";

    public InstaScraper() {
        username = "";
        cookiesURL = "C:\\Users\\zoya\\Documents\\WhaleFood\\api\\cookies.txt";
        basicCommand = new ArrayList<>();
    }

    public InstaScraper(String user) {
        cookiesURL = "C:\\Users\\zoya\\Documents\\WhaleFood\\api\\cookies.txt";
        username = user;
        userProfileURL = "https://www.instagram.com/" + user;

        // basic command to call Gallery-dl API
        // note: for insta specific options, need to append them to -o
        basicCommand = Arrays.asList("gallery-dl",
                "--cookies", cookiesURL, "--download-archive", archivePath,
                "-o", "include=stories,highlights,posts", "-o", "order-posts=asc", "-o", "videos=false",  userProfileURL);
    }

    /**
     * Method that updates username folder with any new posts,stories,or highlights.
     * You can rerun this method as much as you like, it won't delete anything it just adds to the folder
     */
    public void pullFromInsta() {
        runCL(basicCommand);
    }

    /**
     * Converts JPG images in the user folder to ImageKit URLs and writes to a CSV file.
     */
    public void convertImagesToCSV() {
        String userFolderPath = "C:\\Users\\zoya\\Documents\\WhaleFood\\gallery-dl\\instagram\\" + username;
        String outputCsvPath = "imageUrls.csv";

        File folder = new File(userFolderPath);
        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No JPG images found in the directory.");
            return;
        }

        // Initialize ImageKit
        ImageKit imageKit = ImageKit.getInstance();
        Configuration config = new Configuration(IMAGEKIT_PUBLIC_KEY, IMAGEKIT_PRIVATE_KEY, IMAGEKIT_URL_ENDPOINT);
        imageKit.setConfig(config);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvPath))) {
            for (File imageFile : imageFiles) {
                String webUrl = uploadToImageKit(imageKit, imageFile);

                if (webUrl != null) {
                    writer.write("\"" + webUrl + "\"");
                    writer.newLine();
                    System.out.println("Successfully processed: " + imageFile.getName());
                } else {
                    System.out.println("Failed to upload: " + imageFile.getName());
                }
            }
            System.out.println("Image URLs written to " + outputCsvPath);
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Upload image to ImageKit following official documentation
     */
    private String uploadToImageKit(ImageKit imageKit, File imageFile) {
        try {
            // Read the image file as bytes
            byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());

            // Create FileCreateRequest - this matches the official documentation
            FileCreateRequest fileCreateRequest = new FileCreateRequest(imageBytes, imageFile.getName());

            // Optional: Set folder and other options
            fileCreateRequest.setFolder("/instagram-scraper/");
            fileCreateRequest.setUseUniqueFileName(true);

            // Add response fields (following the documentation example)
            List<String> responseFields = new ArrayList<>();
            responseFields.add("thumbnail");
            responseFields.add("tags");
            responseFields.add("customCoordinates");
            fileCreateRequest.setResponseFields(responseFields);

            // Add tags
            List<String> tags = new ArrayList<>();
            tags.add("instagram");
            tags.add("scraper");
            tags.add(username);
            fileCreateRequest.setTags(tags);

            // Upload the image
            Result result = imageKit.upload(fileCreateRequest);

            if (result != null && result.getUrl() != null) {
                System.out.println("Upload successful!");
                System.out.println("File ID: " + result.getFileId());
                System.out.println("URL: " + result.getUrl());
                return result.getUrl();
            } else {
                System.out.println("Upload failed - no URL returned");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error uploading " + imageFile.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // Create the scraper object for the user
        InstaScraper scrap = new InstaScraper("zoyaa_b");

        // First, update the folder with any new images/stories
        scrap.pullFromInsta();

        // Then convert images to web URLs
        scrap.convertImagesToCSV();
    }

    /**
     * Helper method to execute commands in terminal and prints the result
     * @param command List of args + parameters for the call
     */
    public static void runCL(List<String> command) {
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);

            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}