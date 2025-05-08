package com.InterviewSimulator;

import com.InterviewSimulator.service.InterviewService;

import java.util.Scanner;

/*
  Main application class for the AI Interview Simulator.
  This class provides a menu-driven interface for the user to interact with the system.
  It includes options to create an account, schedule an interview, start an instant interview, or exit the application.
 */
public class App {
    public static void main(String[] args) {
        // Initialize the Scanner object to read user input from the console
        Scanner scanner = new Scanner(System.in);

        // Initialize the InterviewService object to handle business logic
        InterviewService interviewService = new InterviewService();

        // Infinite loop for displaying the menu and taking user input
        while (true) {
            // Display the main menu options to the user
            System.out.println("\n==== AI Interview Simulator ====");
            System.out.println("1. Create Account");
            System.out.println("2. Schedule Interview Session");
            System.out.println("3. Start Instant Interview");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            // Get the valid integer input from the user (validated input)
            int choice = getValidIntegerInput(scanner);

            // Handle the user's choice using a switch statement
            switch (choice) {
                case 1:
                    // Option 1: Call createAccount method in InterviewService to handle account creation
                    interviewService.createAccount(scanner);
                    break;
                case 2:
                    // Option 2: Call scheduleInterviewSession method in InterviewService to handle interview scheduling
                    interviewService.scheduleInterviewSession(scanner);
                    break;
                case 3:
                    // Option 3: Start an instant interview by asking the user for their email and passing it to the service
                    System.out.print("Enter your Email: ");
                    String email = scanner.nextLine();
                    interviewService.startInstantInterview(email, scanner);
                    break;
                case 4:
                    // Option 4: Exit the application
                    System.out.println("Exiting... Thank you for using AI Interview Simulator.");
                    scanner.close();  // ✅ Close the scanner to release resources
                    System.exit(0);  // Exit the program
                default:
                    // If the user enters an invalid option, show an error message
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    /*
      Helper method to get a valid integer input from the user.
      This method ensures that the user enters a valid number.
      @param scanner The scanner object to read user input
      @return The valid integer input entered by the user
     */
    private static int getValidIntegerInput(Scanner scanner) {
        // Infinite loop until a valid integer input is provided
        while (true) {
            try {
                // Attempt to parse the user's input as an integer
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // If a NumberFormatException is thrown, ask the user to enter a valid number
                System.out.print("Invalid input! Please enter a number: ");
            }
        }
    }
}
