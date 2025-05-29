import java.util.*;
import java.io.*;

public class Website {
    // store user answers
    private String userAnswers;
    private String instaUser;

    // initialize class + user input
    public Website() {
        collectUserInput();
        instagramUser();


    }

    private boolean isValidHandle(String handle) {
        return handle != null && handle.matches("^[a-zA-Z0-9._]+$");
    }
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        Website website = new Website();

        System.out.println("\nYour private information: ");
        System.out.println(website.userInfo());

        // instascraper run
        System.out.println("\nStarting Instagram scraping process...");
        String[] argument = {website.getInstaUser()}; // Pass username as argument
        InstaScraper.main(argument);

        // run chatGPT stuff
        System.out.println("\nStarting AI analysis of your images...");
        ChatGPT chatGPT = new ChatGPT();

        // main method of chatgpt thing for csv file
        website.runChatGPTAnalysis();

        // print csv file related stuff
        System.out.println("\nWe've found that your information is at risk of leaking answers to the following common security questions based on " +
                "image analysis of your instagram profile.");

        // print csv file actually forreal this time
        website.displayAnalysisResults();
    }

    /**
     * Run ChatGPT analysis by calling the main method logic from ChatGPT class
     */
    private void runChatGPTAnalysis() throws IOException {
        // Simply call the main method from ChatGPT class which handles everything
        String[] args = {}; // Empty args array
        ChatGPT.main(args);
    }

    /**
     * Display the analysis results from the CSV file
     */
    private void displayAnalysisResults() {
        String chatGPTresponses = "chatGPTresponses.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(chatGPTresponses))) {
            String line;
            System.out.println("\n=== SECURITY ANALYSIS RESULTS ===");
            while ((line = br.readLine()) != null) {
                // Parse and display the responses
                String[] responses = line.split("\",\"");
                for (String response : responses) {
                    String cleaned = response.replaceAll("^\"|\"$", "").replace("\"\"", "\"");
                    if (!cleaned.trim().isEmpty()) {
                        System.out.println("- " + cleaned);
                        System.out.println();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read analysis results: " + e.getMessage());
        }
    }

    /**
     * Getter method for Instagram username
     */
    public String getInstaUser() {
        return instaUser;
    }

    public String instagramUser() {
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("What is your Instagram username?");
            instaUser = scan.next();

            if (instaUser == null || instaUser.trim().isEmpty()) {
                System.out.println("Error: Instagram handle cannot be empty.\n");
            } else if (!isValidHandle(instaUser)) {
                System.out.println("Error: Invalid handle. Only letters, numbers, dots, and underscores are allowed.\n");
            } else {
                break;  // valid input
            }
        }

        return instaUser;
    }

    /**
     * Method to collect user input for all security questions
     */
    private void collectUserInput() {
        Scanner scan = new Scanner(System.in);
        StringBuilder answers = new StringBuilder();

        System.out.println("Please answer the following security questions:");

        // questions in an array
        String[] questions = getSecQuestionsArray();

        // ask questions, assign answer to q
        for (int i = 0; i < questions.length; i++) {
            System.out.print((i + 1) + ". " + questions[i] + " ");
            String answer = scan.nextLine().trim();

            // store q + a
            answers.append("Q").append(i + 1).append(": ").append(questions[i]).append("\\n");
            answers.append("A").append(i + 1).append(": ").append(answer).append("\\n\\n");
        }

        // store
        userAnswers = answers.toString();

        System.out.println("Thank you! Please hold while we process our security checks and make sure your information is safe and secure.");
    }

    /**
     * Returns the stored user information
     * @return String containing all user answers
     */
    public String userInfo() {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return "No user information collected yet.";
        }
        return userAnswers;
    }

    /**
     * Returns security questions as a formatted string (original method)
     */
    public static String secQuestions() {
        String questions = "What's your favorite color?\n" +
                "What's your mother's maiden name?\n" +
                "Where were you born?\n" +
                "What was your first pet called?\n" +
                "What's your high school best friend's name?\n" +
                "What's your high school mascot?\n" +
                "What street did you grow up on?\n" +
                "What is your (relative)'s first name?\n" +
                "What is the name of your favorite music album?\n" +
                "What was your childhood nickname?\n" +
                "Where did you go for high school?\n" +
                "What's your favorite hobby?\n" +
                "Where is your favorite place to relax?\n" +
                "What's your favorite movie?\n" +
                "Who is your favorite author?\n" +
                "What's your favorite food?\n" +
                "Who is your favorite singer?\n" +
                "Who was your childhood best friend?\n";

        return questions;
    }

    /**
     * Helper method to get questions as an array for easier processing
     */
    private String[] getSecQuestionsArray() {
        return new String[] {
                "What's your favorite color?",
                "What's your mother's maiden name?",
                "Where were you born?",
                "What was your first pet called?",
                "What's your high school best friend's name?",
                "What's your high school mascot?",
                "What street did you grow up on?",
                "What is your (relative)'s first name?",
                "What is the name of your favorite music album?",
                "What was your childhood nickname?",
                "Where did you go for high school?",
                "What's your favorite hobby?",
                "Where is your favorite place to relax?",
                "What's your favorite movie?",
                "Who is your favorite author?",
                "What's your favorite food?",
                "Who is your favorite singer?",
                "Who was your childhood best friend?"
        };
    }

    /**
     * Method to get a specific answer by question number
     * @param questionNumber The question number (1-based)
     * @return The answer to that question, or null if not found
     */
    public String getSpecificAnswer(int questionNumber) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return null;
        }

        String searchPattern = "A" + questionNumber + ": ";
        int startIndex = userAnswers.indexOf(searchPattern);
        if (startIndex == -1) {
            return null;
        }

        startIndex += searchPattern.length();
        int endIndex = userAnswers.indexOf("\n", startIndex);
        if (endIndex == -1) {
            endIndex = userAnswers.length();
        }

        return userAnswers.substring(startIndex, endIndex);
    }

    public String personalInfo() {
        String info = "Full Name\n" +
                "\n" +
                "Home Address\n" +
                "\n" +
                "Phone Number(s)\n" +
                "\n" +
                "Email Address\n" +
                "\n" +
                "Date of Birth\n" +
                "\n" +
                "Social Security Number (SSN)\n" +
                "\n" +
                "Driver's License Number\n" +
                "\n" +
                "Passport Number\n" +
                "\n" +
                "Credit Card Information\n" +
                "\n" +
                "Bank Account Numbers";
        return info;
    }
}