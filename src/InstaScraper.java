import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class InstaScraper {
    public static void main(String[] args) {
        String cookiePath = "C:\\Users\\zoya\\Documents\\WhaleFood\\api\\cookies.txt";
        String archivePath = "C:\\Users\\zoya\\Documents\\WhaleFood\\api\\gallery-archive.db";
        String userProfileURL = "https://www.instagram.com/zoyaa_b";

        List<String> instaCommands = Arrays.asList("gallery-dl",
                "--cookies", cookiePath, "--download-archive", archivePath,
                userProfileURL);

        runCL(instaCommands);

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

