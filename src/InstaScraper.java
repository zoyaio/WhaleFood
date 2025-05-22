import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstaScraper {
    // user related
    private String username;
    private String userProfileURL;

    //paths
    private final String archivePath = "C:\\Users\\zoya\\Documents\\WhaleFood\\api\\gallery-archive.db";
    private String cookiesURL;

    // basic api command through CLI
    private List<String> basicCommand;


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

