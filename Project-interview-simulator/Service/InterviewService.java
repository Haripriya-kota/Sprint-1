package com.InterviewSimulator.service;

import com.InterviewSimulator.dao.*;
import com.InterviewSimulator.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class InterviewService {
    // DAOs for database operations
    private final UserDAO userDAO = new UserDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final InterviewSessionDAO sessionDAO = new InterviewSessionDAO();
    private final AIService aiService = new AIService();
    private final ResponseDAO responseDAO = new ResponseDAO();

    // Read password securely (console or fallback to normal input)
    private String readPasswordSecurely(Scanner scanner) {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            return scanner.nextLine(); // IDE fallback
        }
    }

    // Handles account creation and validation
    public void createAccount(Scanner scanner) {
        String name, email, mobileNo, password, experienceLevel;

        System.out.print("Enter Name: ");
        name = scanner.nextLine().trim();

        // Email validation and uniqueness check
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("❌ Invalid email format! Please enter a valid email.");
                continue;
            }
            if (userDAO.emailExists(email)) {
                System.out.println("❌ Email already exists! Please use a different email.");
                continue;
            }
            break;
        }

        // Validate mobile number
        while (true) {
            System.out.print("Enter Mobile No: ");
            mobileNo = scanner.nextLine().trim();
            if (mobileNo.matches("\\d{10}")) break;
            System.out.println("❌ Mobile number must be exactly 10 digits. Try again.");
        }

        // Password validation
        while (true) {
            System.out.print("Enter Password: ");
            password = readPasswordSecurely(scanner).trim();
            if (password.length() >= 6) break;
            System.out.println("❌ Password must be at least 6 characters long. Try again.");
        }

        // Experience level validation
        while (true) {
            System.out.print("Enter Experience Level (beginner/intermediate/expert): ");
            experienceLevel = scanner.nextLine().trim().toLowerCase();
            if (experienceLevel.matches("beginner|intermediate|expert")) break;
            System.out.println("❌ Invalid experience level! Choose 'beginner', 'intermediate', or 'expert'.");
        }

        // Save user to database
        User user = new User(name, email, mobileNo, password, experienceLevel);
        userDAO.saveUser(user);
        System.out.println("✅ Account created successfully!");
    }

    // Schedule an interview session after authenticating user
    public void scheduleInterviewSession(Scanner scanner) {
        System.out.print("Enter your registered email: ");
        String email = scanner.nextLine().trim();
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            System.out.println("❌ Email not found! Please create an account first.");
            return;
        }

        // Authenticate user by password
        int attempts = 3;
        while (attempts-- > 0) {
            System.out.print("Enter Password: ");
            String password = readPasswordSecurely(scanner).trim();
            if (user.getPassword().trim().equals(password)) {
                System.out.println("✅ Authentication successful!");
                break;
            }
            System.out.println("❌ Incorrect password! Attempts left: " + attempts);
            if (attempts == 0) return;
        }

        InterviewSession interviewSession = new InterviewSession();
        interviewSession.setUser(user);

        // Date input for scheduling
        java.sql.Date scheduleDate = null;
        while (true) {
            System.out.print("Enter Schedule Date (yyyy-MM-dd): ");
            String input = scanner.nextLine().trim();
            try {
                java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(input);
                scheduleDate = new java.sql.Date(parsedDate.getTime());
                break;
            } catch (Exception e) {
                System.out.println("❌ Invalid date format! Please use yyyy-MM-dd.");
            }
        }
        interviewSession.setScheduleDate(scheduleDate);

        // Start time input
        Time startTime;
        while (true) {
            System.out.print("Enter Start Time (HH:mm:ss): ");
            try {
                startTime = Time.valueOf(scanner.nextLine().trim());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid time format! Use HH:mm:ss.");
            }
        }
        interviewSession.setStartTime(startTime);

        // End time input
        Time endTime;
        while (true) {
            System.out.print("Enter End Time (HH:mm:ss): ");
            try {
                endTime = Time.valueOf(scanner.nextLine().trim());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid time format! Use HH:mm:ss.");
            }
        }

        interviewSession.setEndTime(endTime);
        sessionDAO.saveSession(interviewSession);
        System.out.println("✅ Interview session scheduled successfully!");
    }

    // Starts an instant interview session with 5 AI-based questions
    public void startInstantInterview(String email, Scanner scanner) {
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            System.out.println("❌ Email not found! Please create an account first.");
            return;
        }

        // Authenticate user
        int attempts = 3;
        while (attempts-- > 0) {
            System.out.print("Enter Password: ");
            String password = readPasswordSecurely(scanner).trim();
            if (user.getPassword().trim().equals(password)) {
                System.out.println("✅ Authentication successful!");
                break;
            }
            System.out.println("❌ Incorrect password! Attempts left: " + attempts);
            if (attempts == 0) return;
        }

        // Fetch latest session or create a new one
        InterviewSession interviewSession = sessionDAO.getLatestSessionForUser(user.getId());
        if (interviewSession == null) {
            interviewSession = new InterviewSession();
            interviewSession.setUser(user);
            interviewSession.setScheduleDate(new Date(System.currentTimeMillis()));
            sessionDAO.saveSession(interviewSession);
        }

        int totalScore = 0;
        StringBuilder reportCard = new StringBuilder("\n📋 Interview Report Card\n");

        // Loop through 5 AI-generated questions
        for (int i = 1; i <= 5; i++) {
            JSONObject questionData = null;

            // Retry up to 3 times if API fails
            for (int retry = 0; retry < 3; retry++) {
                questionData = aiService.fetchTriviaQuestionData();
                if (questionData != null) break;
                System.out.println("⚠️ API error. Retrying...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (questionData == null || questionData.getString("correct_answer").isEmpty()) {
                System.out.println("❌ Skipping question due to API error.");
                continue;
            }

            // Extract question and answer
            String questionText = questionData.getString("question");
            String correctAnswer = questionData.getString("correct_answer");
            String questionType = questionData.getString("type");

            // Save question to DB
            Question question = new Question();
            question.setQuestionText(questionText);
            question.setCorrectAnswer(correctAnswer);
            question.setInterviewSession(interviewSession);
            question.setDifficultyLevel("medium");
            questionDAO.saveQuestion(question);

            System.out.println("\n❓ Question " + i + ": " + questionText);

            // Capture user answer based on question type
            String userAnswer = "";
            if ("boolean".equals(questionType)) {
                System.out.print("💡 Your Answer (1. True / 2. False): ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                userAnswer = (choice == 1) ? "True" : "False";
            } else if ("multiple".equals(questionType)) {
                JSONArray incorrectAnswers = questionData.getJSONArray("incorrect_answers");
                System.out.println("1. " + correctAnswer);
                for (int j = 0; j < incorrectAnswers.length(); j++) {
                    System.out.println((j + 2) + ". " + incorrectAnswers.getString(j));
                }
                System.out.print("Your choice (1-" + (incorrectAnswers.length() + 1) + "): ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                userAnswer = (choice == 1) ? correctAnswer : incorrectAnswers.getString(choice - 2);
            }

            // Save answer to DB
            Answer answer = new Answer();
            answer.setAnswerText(userAnswer);
            answer.setQuestionText(questionText);
            answer.setQuestion(question);
            answerDAO.saveAnswer(answer);

            // Evaluate answer
            boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
            String feedback = isCorrect ? "Correct!" : "Incorrect.";
            totalScore += isCorrect ? 1 : 0;

            // Save response
            Response response = new Response();
            response.setUser(user);
            response.setQuestion(question);
            response.setCorrectAnswer(correctAnswer);
            response.setResponseText(userAnswer);
            response.setAnswer(answer);
            responseDAO.saveResponse(response);

            // Save report
            Report report = new Report();
            report.setUser(user);
            report.setQuestion(question);
            report.setResponse(response);
            report.setInterviewSession(interviewSession);
            report.setImprovementSuggestions(generateImprovementSuggestion(userAnswer, correctAnswer));
            reportDAO.saveReport(report);

            // Add to report card
            reportCard.append("\nQuestion ").append(i).append(": ").append(questionText)
                      .append("\nYour Answer: ").append(userAnswer)
                      .append("\nCorrect Answer: ").append(correctAnswer)
                      .append("\nResult: ").append(feedback)
                      .append("\nSuggestion: ").append(generateImprovementSuggestion(userAnswer, correctAnswer)).append("\n");
        }

        // Final results
        System.out.println("\n✅ Interview session completed! Your score: " + totalScore + "/5");
        System.out.println(reportCard);
    }

    // Suggest improvement if answer is incorrect
    private String generateImprovementSuggestion(String userAnswer, String correctAnswer) {
        return userAnswer.equalsIgnoreCase(correctAnswer)
                ? "Great job! Keep it up!"
                : "Review related concepts to improve understanding.";
    }
}
