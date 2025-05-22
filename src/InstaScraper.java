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

        // for insta specific options, need to append them to -o
        basicCommand = Arrays.asList("gallery-dl",
                "--cookies", cookiesURL, "--download-archive", archivePath,
                "-o", "include=stories,highlights,posts", "-o", "order-posts=asc", "-o", "videos=false",  userProfileURL);

        // --include "stories,highlights,posts"    --order-posts "asc" --videos false
    }

    public void pullFromInsta() {
        runCL(basicCommand);

    }
//    TODO: public void pullFromInsta(String cursorPos);



    public static void main(String[] args) {
        InstaScraper scrap = new InstaScraper("zoyaa_b");
        scrap.pullFromInsta();

    }


    public static void runCL(List<String> command) {
        try {
            // Example command: ls -l (or use "cmd.exe", "/c", "dir" on Windows)
//            List<String> command = Arrays.asList("bash", "-c", "ls -l");
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);  // Merge stdout and stderr

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

