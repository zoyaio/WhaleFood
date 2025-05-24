import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstaScraper {
    // user related
    private String username;
    private String userProfileURL;

    //paths
    private final String archivePath = "C:\\Users\\minty\\Documents\\WhaleFood\\api\\gallery-archive.db";
    private String cookiesURL;

    // basic api command through CLI
    private List<String> basicCommand;


    public InstaScraper() {
        username = "";
        cookiesURL = "C:\\Users\\minty\\Documents\\WhaleFood\\api\\cookies.txt";
        basicCommand = new ArrayList<>();
    }
    public InstaScraper(String user) {
        cookiesURL = "C:\\Users\\minty\\Documents\\WhaleFood\\api\\cookies.txt";
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
     * Converts JPG images in the user folder to file:// URLs and writes to a CSV file.
     */
    public void convertImagesToCSV() {
        String userFolderPath = "C:\\Users\\minty\\Documents\\WhaleFood\\gallery-dl\\instagram\\" + username;
        String outputCsvPath = "image_urls.csv";

        File folder = new File(userFolderPath);
        // this just makes sure it's a jpg
        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        // check for if theres jpgs at all
        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No JPG images found in the directory.");
            return;
        }

        // loops through the imagefiles in the profile folder, does something and idk what it is to generate a file url
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvPath))) {
            for (File imageFile : imageFiles) {
                Path path = imageFile.toPath().toAbsolutePath();
                String url = path.toUri().toString(); // Generates file:// URL
                writer.write("\"" + url + "\""); // Wrap in quotes for CSV safety
                writer.newLine();
            }
            // check for if it actually worked or not -- delete later
            System.out.println("Image URLs written to " + outputCsvPath);
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }


//    TODO: public void pullFromInsta(String cursorPos);

    public static void main(String[] args) {
        // TODO this is where you create the scraper object for the user
        InstaScraper scrap = new InstaScraper("zoyaa_b");

        // updates the zoyaa_b folder with any new images/stories I posted
        scrap.pullFromInsta();

    }


    /**
     * Helper method to execute commands in terminal and prints the result
     * @param command List of args + parameters for the call
     */
    public static void runCL(List<String> command) {
        try {
            // A builder that constructs a command-line call
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);  // Merge stdout and stderr

            Process process = builder.start(); // executes the command

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            // basically prints out every line of the command line output after its done running
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

