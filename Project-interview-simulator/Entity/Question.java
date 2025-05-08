package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question") // Maps this class to the "question" table in the database
public class Question {
    
    // Primary key for the question table, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Q_Id")
    private int id;

    // Stores the actual question text, marked as NOT NULL and stored as TEXT
    @Column(name = "Question_Text", nullable = false, columnDefinition = "TEXT")
    public String questionText;

    // Indicates the difficulty level of the question (e.g., easy, medium, hard)
    @Column(name = "Difficulty_Level", length = 50)
    private String difficultyLevel;

    // Many-to-one relationship with InterviewSession table
    @ManyToOne
    @JoinColumn(name = "Session_Id") // Foreign key to InterviewSession
    private InterviewSession interviewSession;

    // Default constructor (required by JPA)
    public Question() {}

    // Parameterized constructor for convenience
    public Question(String questionText, String difficultyLevel, InterviewSession interviewSession) {
        this.questionText = questionText;
        this.difficultyLevel = difficultyLevel;
        this.interviewSession = interviewSession;
    }

    // Getter and Setter methods for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public InterviewSession getInterviewSession() { return interviewSession; }
    public void setInterviewSession(InterviewSession interviewSession) { this.interviewSession = interviewSession; }

    // Correct answer to the question (e.g., "True" or "False")
    @Column(name = "Correct_Answer", nullable = false)
    private String correctAnswer;

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
