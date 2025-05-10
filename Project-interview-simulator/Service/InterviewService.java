package com.InterviewSimulator.service;

import com.InterviewSimulator.dao.*;
import com.InterviewSimulator.entity.*;
import com.InterviewSimulator.util.EmailUtil;
import com.InterviewSimulator.util.HibernateUtil;

import javax.swing.*;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Timer;
import java.time.LocalTime;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;


public class InterviewService {

    // DAO objects to interact with the database for different entities
    private final UserDAO userDAO = new UserDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final InterviewSessionDAO sessionDAO = new InterviewSessionDAO();
    private final ResponseDAO responseDAO = new ResponseDAO();
// Method to securely read a password from the user using a GUI dialog
    public String readPasswordSecurely(Scanner scanner) {
        // Create a password input field (text hidden as dots)
        JPasswordField passwordField = new JPasswordField();

        // Create a temporary JFrame to ensure the dialog appears on top
        JFrame parentFrame = new JFrame();
        parentFrame.setAlwaysOnTop(true); // Keep dialog on top of other windows
        parentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose frame after closing
        parentFrame.setLocationRelativeTo(null); // Center the frame on the screen

        // Message array for the dialog, including the password field
        Object[] message = { "Enter your password:", passwordField };

        // Show a confirm dialog with OK and Cancel options
        int option = JOptionPane.showConfirmDialog(
            parentFrame,
            message,
            "Password Input",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            // If user clicks OK, retrieve password input
            char[] password = passwordField.getPassword();

            // Convert char array to String
            String passwordString = new String(password);

            // Clear the char array from memory for security
            Arrays.fill(password, ' ');

            // Dispose the temporary frame
            parentFrame.dispose();

            // Return the entered password
            return passwordString;
        } else {
            // If user cancels input
            System.out.println("❌ Cancelled by user.");

            // Dispose the frame
            parentFrame.dispose();

            // Return an empty string
            return "";
        }
    }

	public void createAccount(Scanner scanner) {
	    String name, email, mobileNo, password, experienceLevel;

	    // Validate user's name
	    while (true) {
	        System.out.print("Enter Name: ");
	        name = scanner.nextLine().trim();

	        // Name should be at least 3 characters long
	        if (name.length() < 3) {
	            System.out.println("❌ Name must be at least 3 characters long.");
	        }
	        // Name must contain only alphabets and spaces
	        else if (!name.matches("[a-zA-Z ]+")) {
	            System.out.println("❌ Name must contain only alphabets and spaces.");
	        } 
	        else {
	            break; // Valid name
	        }
	    }

	    // Validate user's email
	    while (true) {
	        System.out.print("Enter Email: ");
	        email = scanner.nextLine().trim();

	        // Check email format
	        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	            System.out.println("❌ Invalid email format! Please enter a valid email.");
	        } 
	        // Check if email already exists in the database
	        else if (userDAO.emailExists(email)) {
	            System.out.println("❌ Email already exists! Please use a different email.");
	        } 
	        else {
	            break; // Valid and unique email
	        }
	    }

	    // Validate user's mobile number
	    while (true) {
	        System.out.print("Enter Mobile No: ");
	        mobileNo = scanner.nextLine().trim();

	        // Mobile number must be exactly 10 digits
	        if (mobileNo.matches("\\d{10}"))
	            break;
	        System.out.println("❌ Mobile number must be exactly 10 digits. Try again.");
	    }

	    // Validate user's password
	    while (true) {
	        password = readPasswordSecurely(scanner).trim();

	        // Password must be at least 6 characters long
	        if (password.length() >= 6)
	            break;
	        System.out.println("❌ Password must be at least 6 characters long. Try again.");
	    }

	    // Validate experience level input
	    while (true) {
	        System.out.print("Enter Experience Level (beginner/intermediate/expert): ");
	        experienceLevel = scanner.nextLine().trim().toLowerCase();

	        // Accept only specific values
	        if (experienceLevel.matches("beginner|intermediate|expert"))
	            break;
	        System.out.println("❌ Invalid experience level! Choose 'beginner', 'intermediate', or 'expert'.");
	    }

	    // Create new user object with validated inputs
	    User user = new User(name, email, mobileNo, password, experienceLevel);

	    // Save the user to the database using DAO
	    userDAO.saveUser(user);
	}


	public void scheduleInterviewSession(Scanner scanner) {
	    // Prompt user to enter registered email
	    System.out.print("Enter your registered email: ");
	    String email = scanner.nextLine().trim();
	    User user = userDAO.getUserByEmail(email);

	    // Check if user exists
	    if (user == null) {
	        System.out.println("❌ Email not found! Please create an account first.");
	        return;
	    }

	    // Password verification loop
	    int attempts = 3;
	    while (attempts-- > 0) {
	        String password = readPasswordSecurely(scanner).trim();
	        if (user.getPassword().trim().equals(password)) {
	            System.out.println("✅ Authentication successful!");
	            break;
	        }
	        System.out.println("❌ Incorrect password! Attempts left: " + attempts);
	        if (attempts == 0) return;
	    }

	    // Create a new InterviewSession object after successful authentication
	    InterviewSession interviewSession = new InterviewSession();
	    interviewSession.setUser(user);

	    // Declare variables to hold the scheduled date and start time of the interview
	    java.sql.Date scheduleDate = null;
	    Time startTime = null;

	    // Date input and validation (same as before)
	    while (true) {
	        System.out.print("Enter Schedule Date (yyyy-MM-dd): ");
	        String dateInput = scanner.nextLine().trim();
	        if (!dateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
	            System.out.println("❌ Invalid date format. Please use yyyy-MM-dd.");
	            continue;
	        }

	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);

	        java.util.Date parsedDate = null;
	        try {
	            parsedDate = dateFormat.parse(dateInput);
	        } catch (java.text.ParseException e) {
	            System.out.println("❌ Error parsing date. Please enter a valid date.");
	            continue;
	        }

	        scheduleDate = new java.sql.Date(parsedDate.getTime());
	        LocalDate enteredDate = scheduleDate.toLocalDate();

	        if (enteredDate.isBefore(LocalDate.now())) {
	            System.out.println("❌ Date must be today or in the future.");
	            continue;
	        }

	        break;
	    }

	    // Time input and validation (same as before)
	    while (true) {
	        System.out.print("Enter Start Time (HH:mm:ss): ");
	        String timeInput = scanner.nextLine().trim();

	        try {
	            LocalTime localTime = LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("HH:mm:ss"));
	            startTime = Time.valueOf(localTime);

	            LocalDate interviewDate = scheduleDate.toLocalDate();
	            LocalDateTime interviewDateTime = LocalDateTime.of(interviewDate, localTime);
	            if (interviewDateTime.isBefore(LocalDateTime.now())) {
	                System.out.println("❌ Interview time must be in the future.");
	                continue;
	            }

	            break;
	        } catch (DateTimeParseException e) {
	            System.out.println("❌ Invalid time format or value! Use HH:mm:ss (e.g., 14:30:00).");
	        }
	    }

	    interviewSession.setScheduleDate(scheduleDate);
	    interviewSession.setStartTime(startTime);

	    // Generate a unique token for the interview session
	    String token = UUID.randomUUID().toString();
	    interviewSession.setToken(token);

	    // Save the session to the database
	    sessionDAO.saveSession(interviewSession);
	    System.out.println("✅ Interview session scheduled successfully!");

	    // Reminder time logic (using 10-minute reminder)
	    LocalDate interviewDate = scheduleDate.toLocalDate();
	    LocalTime interviewStartTime = startTime.toLocalTime();
	    LocalDateTime interviewDateTime = LocalDateTime.of(interviewDate, interviewStartTime);
	    LocalDateTime reminderTime = interviewDateTime.minusMinutes(10); // 10-minute reminder
	    long delayInMillis = Duration.between(LocalDateTime.now(), reminderTime).toMillis();

	    // Send the reminder email
	    if (delayInMillis > 0) {
	        Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                String subject = "⏰ Interview Reminder";
	                String message = "Hi " + user.getName()
	                        + ",\n\nYour interview is scheduled. Click the link below to start your interview after logging in:\n\n"
	                        + "http://interviewsimulator.com/startInterview?token=" + token + "\n\nGood luck!\n- Interview Simulator Team";
	                EmailUtil.sendEmail(user.getEmail(), subject, message);
	                System.out.println("⏳ Reminder email sent to " + user.getEmail());
	            }
	        }, delayInMillis);
	    } else {
	        System.out.println("⚠️ Reminder time is already past. No reminder will be sent.");
	    }
	}

	public void startInstantInterview(String email, Scanner scanner) throws InterruptedException {
	    User user = userDAO.getUserByEmail(email);

	    if (user == null) {
	        System.out.println("\u274C Email not found! Please create an account first.");
	        return;
	    }

	    int attempts = 3;
	    while (attempts-- > 0) {
	        String password = readPasswordSecurely(scanner).trim();
	        if (user.getPassword().trim().equals(password)) {
	            System.out.println("\u2705 Authentication successful!");
	            break;
	        }
	        System.out.println("\u274C Incorrect password! Attempts left: " + attempts);
	        if (attempts == 0) return;
	    }

	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        session.beginTransaction();

	        // Prepare session metadata
	        LocalDate localDate = LocalDate.now();
	        Date scheduleDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

	        // Use LocalTime for start time, then convert to java.sql.Time
	        LocalTime startTime = LocalTime.now(); 
	        Time sqlStartTime = Time.valueOf(startTime); // Convert LocalTime to SQL Time

	        long sessionStartMillis = Instant.now().toEpochMilli();

	        InterviewSession interviewSession = new InterviewSession();
	        interviewSession.setUser(user);
	        interviewSession.setScheduleDate(scheduleDate);
	        interviewSession.setStartTime(sqlStartTime);  // Set the converted start time (sqlStartTime)
	        sessionDAO.saveSession(interviewSession);

	        long timeLimitInMinutes = 40;
	        long availableTime = timeLimitInMinutes * 40 * 1000; // in milliseconds (40 minutes)
	        int maxQuestions = 30; // Limit to 30 questions

	        System.out.println("\uD83D\uDD52 You have " + timeLimitInMinutes + " minutes to complete 30 questions.");

	        // Fetch questions from API
	        String apiUrl = "https://opentdb.com/api.php?amount=50&category=18&difficulty=medium&type=multiple";
	        URL url = new URL(apiUrl);
	        Scanner apiScanner = new Scanner(url.openStream());
	        StringBuilder jsonBuilder = new StringBuilder();
	        while (apiScanner.hasNext()) {
	            jsonBuilder.append(apiScanner.nextLine());
	        }
	        apiScanner.close();

	        JSONObject json = new JSONObject(jsonBuilder.toString());
	        JSONArray questions = json.getJSONArray("results");

	        int questionCount = 0;
	        int correctCount = 0;
	        List<String> reportCard = new ArrayList<>();

	        // Process questions
	        for (int i = 0; i < questions.length(); i++) {
	            long currentTime = Instant.now().toEpochMilli();
	            if (currentTime - sessionStartMillis > availableTime || questionCount >= maxQuestions) {
	                break;
	            }

	            JSONObject qObj = questions.getJSONObject(i);
	            String questionText = qObj.getString("question").replace("&quot;", "\"").replace("&#039;", "'");
	            String correctAnswer = qObj.getString("correct_answer");
	            JSONArray incorrectAnswers = qObj.getJSONArray("incorrect_answers");

	            List<String> options = new ArrayList<>();
	            options.add(correctAnswer);
	            for (int j = 0; j < incorrectAnswers.length(); j++) {
	                options.add(incorrectAnswers.getString(j));
	            }
	            Collections.shuffle(options);

	            System.out.println("\n❓ Question " + (questionCount + 1) + ": " + questionText);
	            for (int k = 0; k < options.size(); k++) {
	                System.out.println("  " + (k + 1) + ". " + options.get(k));
	            }

	            String userAnswer;
	            while (true) {
	                System.out.print("👉 Enter the number of your answer: ");
	                try {
	                    int answerIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
	                    if (answerIndex >= 0 && answerIndex < options.size()) {
	                        userAnswer = options.get(answerIndex);
	                        break;
	                    } else {
	                        System.out.println("\u274C Please choose a valid option.");
	                    }
	                } catch (NumberFormatException e) {
	                    System.out.println("\u274C Please enter a valid number.");
	                }
	            }

	            boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
	            if (isCorrect) correctCount++;
	            questionCount++;

	         // Simulate AI scoring and feedback
	            double aiScore;
	            String aiFeedback;

	            if (isCorrect) {
	                aiScore = 9.0 + Math.random(); // Between 9.0 and 10.0
	                aiFeedback = " Great job! You answered correctly with confidence and clarity.";
	            } else {
	                aiScore = 5.0 + Math.random() * 3; // Between 5.0 and 8.0
	                aiFeedback = " Incorrect answer. Review this topic to strengthen your understanding.";
	            }

	            
	            
	            
	            Question question = new Question();
	            question.setQuestionText(questionText);
	            question.setCorrectAnswer(correctAnswer);
	            question.setUser(user);
	            question.setInterviewSession(interviewSession);
	            question.setDifficultyLevel("medium");
	            questionDAO.saveQuestion(question);

	            // Persist answer using AnswerDAO
	            Answer answer = new Answer();
	            answer.setAnswerText(userAnswer);
	            answer.setQuestionText(questionText);
	            answer.setQuestion(question);
	            answer.setUser(user);
	            answerDAO.saveAnswer(answer);

	            // Persist response using ResponseDAO
	            Response response = new Response();
	            response.setUser(user);
	            response.setQuestion(question);
	            response.setCorrectAnswer(correctAnswer);
	            response.setResponseText(userAnswer);
	            response.setAnswer(answer);
	            response.setAiScore(aiScore);
	            response.setAiFeedback(aiFeedback);
	            responseDAO.saveResponse(response);

	            // Persist report using ReportDAO
	            Report report = new Report();
	            report.setUser(user);
	            report.setQuestion(question);
	            report.setResponse(response);
	            report.setAnswer(answer); // or report.setAnswerId(answer.getId());
	            report.setInterviewSession(interviewSession);
	            report.setImprovementSuggestions(generateImprovementSuggestion(userAnswer, correctAnswer));
	            reportDAO.saveReport(report);

	            // Save to report card
	            String result = "Question: " + questionText +
	                    "\nYour Answer: " + userAnswer +
	                    "\nCorrect Answer: " + correctAnswer +
	                    "\n" + (isCorrect ? "\u2705 Correct!" : "❌ Incorrect");
	            reportCard.add(result);

	            Thread.sleep(500);
	        }

	        session.getTransaction().commit();

	        // Summary
	        double percentage = ((double) correctCount / questionCount) * 100;

	        System.out.println("\n\uD83D\uDCCB Interview Completed!");
	        System.out.println("\u2705 Correct Answers: " + correctCount + " out of " + questionCount);
	        System.out.println("\n\uD83D\uDCDD Your performance report:");
	        for (String result : reportCard) {
	            System.out.println("\n" + result);
	        }

	        System.out.println("\n\uD83E\uDD16 Suggestion: Practice regularly and review any incorrect answers for improvement.");

	        // Summary report
	        System.out.println("\n==================================================");
	        System.out.println("| \uD83D\uDCCA REPORT CARD SUMMARY                         |");
	        System.out.println("==================================================");
	        System.out.printf("| %-25s | %-20d |%n", "Total Questions", questionCount);
	        System.out.printf("| %-25s | %-20d |%n", "Correct Answers", correctCount);
	        System.out.printf("| %-25s | %-20d |%n", "Incorrect Answers", questionCount - correctCount);
	        System.out.println("--------------------------------------------------");
	        System.out.printf("| %-25s | %19.2f%% |%n", "Score Percentage", percentage);
	        System.out.println("==================================================");
	        System.out.println("| \u2705 Keep up the good work!                       |");
	        System.out.println("==================================================");

	    } catch (Exception e) {
	        System.out.println("\n❌ Error during interview: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	private String generateImprovementSuggestion(String userAnswer, String correctAnswer) {
	    return userAnswer.equalsIgnoreCase(correctAnswer)
	            ? "Great job! Keep it up!"
	            : "Review related concepts to improve understanding.";
	}
}
/*
 * public void startInstantInterview(String email, Scanner scanner) throws
 * InterruptedException { User user = userDAO.getUserByEmail(email);
 * 
 * if (user == null) {
 * System.out.println("❌ Email not found! Please create an account first.");
 * return; }
 * 
 * int attempts = 3; while (attempts-- > 0) { String password =
 * readPasswordSecurely(scanner).trim(); if
 * (user.getPassword().trim().equals(password)) {
 * System.out.println("✅ Authentication successful!"); break; }
 * System.out.println("❌ Incorrect password! Attempts left: " + attempts); if
 * (attempts == 0) return; }
 * 
 * InterviewSession interviewSession =
 * sessionDAO.getLatestSessionForUser(user.getId()); if (interviewSession ==
 * null) { interviewSession = new InterviewSession();
 * interviewSession.setUser(user); interviewSession.setScheduleDate(new
 * Date(System.currentTimeMillis())); sessionDAO.saveSession(interviewSession);
 * }
 * 
 * int totalScore = 0; StringBuilder reportCard = new
 * StringBuilder("\n📋 Interview Report Card\n");
 * 
 * ChatGPTService chatGPTService = new ChatGPTService();
 * 
 * for (int i = 1; i <= 5; i++) { JSONObject questionData = null;
 * 
 * for (int retry = 0; retry < 3; retry++) { try { questionData =
 * chatGPTService.generateInterviewQuestionFromChatGPT(); if (questionData !=
 * null && !questionData.isEmpty()) break; } catch (Exception e) {
 * System.out.println("⚠️ AI error. Retrying..."); Thread.sleep(2000); } }
 * 
 * if (questionData == null || questionData.isEmpty()) {
 * System.out.println("❌ Skipping question due to AI error."); continue; }
 * 
 * // Log the question data for debugging
 * System.out.println("🔍 Received questionData: " + questionData.toString());
 * 
 * // Validate required keys if (!questionData.has("question") ||
 * !questionData.has("options") || !questionData.has("correct_answer")) {
 * System.out.println("❌ Invalid question format. Skipping this question.");
 * continue; }
 * 
 * String questionText = questionData.getString("question"); JSONObject options
 * = questionData.getJSONObject("options"); String correctAnswer =
 * questionData.getString("correct_answer");
 * 
 * Question question = new Question(); question.setQuestionText(questionText);
 * question.setCorrectAnswer(correctAnswer);
 * question.setInterviewSession(interviewSession);
 * question.setDifficultyLevel("medium"); questionDAO.saveQuestion(question);
 * 
 * System.out.println("\n❓ Question " + i + ": " + questionText); List<String>
 * keys = new ArrayList<>(options.keySet()); Collections.sort(keys); // A, B, C,
 * D for (int j = 0; j < keys.size(); j++) { System.out.println((j + 1) + ". " +
 * options.getString(keys.get(j))); }
 * 
 * System.out.print("Your choice (1-4): "); int choice = scanner.nextInt();
 * scanner.nextLine(); String userAnswer = keys.get(choice - 1); // A, B, C, or
 * D
 * 
 * Answer answer = new Answer(); answer.setAnswerText(userAnswer);
 * answer.setQuestionText(questionText); answer.setQuestion(question);
 * answerDAO.saveAnswer(answer);
 * 
 * boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer); String
 * feedback = isCorrect ? "Correct!" : "Incorrect."; totalScore += isCorrect ? 1
 * : 0;
 * 
 * Response response = new Response(); response.setUser(user);
 * response.setQuestion(question); response.setCorrectAnswer(correctAnswer);
 * response.setResponseText(userAnswer); response.setAnswer(answer);
 * responseDAO.saveResponse(response);
 * 
 * String suggestion; try { suggestion =
 * chatGPTService.getImprovementSuggestion(questionText, userAnswer,
 * correctAnswer); } catch (Exception e) { suggestion =
 * generateImprovementSuggestion(userAnswer, correctAnswer); }
 * 
 * Report report = new Report(); report.setUser(user);
 * report.setQuestion(question); report.setResponse(response);
 * report.setInterviewSession(interviewSession);
 * report.setImprovementSuggestions(suggestion); reportDAO.saveReport(report);
 * 
 * reportCard.append("\nQuestion ").append(i) .append(": ").append(questionText)
 * .append("\nYour Answer: ").append(userAnswer)
 * .append(" - ").append(options.getString(userAnswer))
 * .append("\nCorrect Answer: ").append(correctAnswer)
 * .append(" - ").append(options.getString(correctAnswer))
 * .append("\nResult: ").append(feedback)
 * .append("\nSuggestion: ").append(suggestion) .append("\n"); }
 * 
 * System.out.println("\n✅ Interview session completed! Your score: " +
 * totalScore + "/5"); System.out.println(reportCard);
 * System.out.println("🎯 Your Score: " + totalScore + "/5");
 * 
 * if (totalScore == 5) { System.out.
 * println("🏆 Excellent performance! You're ready for the real interview!"); }
 * else if (totalScore >= 3) {
 * System.out.println("👍 Good job! Keep practicing to improve further."); }
 * else {
 * System.out.println("📚 Keep practicing. Consider reviewing key CS topics.");
 * } }
 * 
 * private String generateImprovementSuggestion(String userAnswer, String
 * correctAnswer) { return
 * "Review the topic related to this question. Correct answer: " +
 * correctAnswer; }
 */