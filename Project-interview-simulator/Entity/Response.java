package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "response") // Maps this class to the "response" table in the database
public class Response {

    // Primary key with auto-increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Response_Id")
    private Long id;

    // Many-to-one relationship with User entity
    @ManyToOne
    @JoinColumn(name = "User_Id") // Foreign key column in "response" table referencing "user"
    private User user;

    // Many-to-one relationship with Question entity
    @ManyToOne
    @JoinColumn(name = "Q_Id") // Foreign key referencing question
    private Question question;

    // The response text provided by the user
    @Column(name = "Response_Text", nullable = false)
    private String responseText;

    // AI-generated score for the response (precision: 5 digits total, 2 after decimal)
    @Column(name = "AI_Score", precision = 5, scale = 2)
    private double aiScore;

    // AI-generated feedback text for the response
    @Column(name = "AI_Feedback")
    private String aiFeedback;

    // Correct answer to the question
    @Column(name = "Correct_Answer", nullable = false)
    private String correctAnswer;

    // Many-to-one relationship with Answer entity
    @ManyToOne
    @JoinColumn(name = "Answer_Id") // Foreign key referencing answer
    private Answer answer;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getResponseText() {
        return responseText;
    }

    // Sets response text with a default if null or empty
    public void setResponseText(String responseText) {
        if (responseText == null || responseText.isEmpty()) {
            this.responseText = "No response provided"; // Default message
        } else {
            this.responseText = responseText;
        }
    }

    public double getAiScore() {
        return aiScore;
    }

    public void setAiScore(double aiScore) {
        this.aiScore = aiScore;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
