package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report") // Maps this class to the "report" table in the database
public class Report {

    // Primary key with auto-increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Report_Id")
    private int id;

    // Many-to-one relationship with InterviewSession
    @ManyToOne
    @JoinColumn(name = "Session_Id") // Foreign key referencing InterviewSession
    private InterviewSession interviewSession;

    // Many-to-one relationship with Response
    @ManyToOne
    @JoinColumn(name = "Response_Id") // Foreign key referencing Response
    private Response response;

    // Many-to-one relationship with Question
    @ManyToOne
    @JoinColumn(name = "Q_Id") // Foreign key referencing Question
    private Question question;

    // Many-to-one relationship with User
    @ManyToOne
    @JoinColumn(name = "User_Id") // Foreign key referencing User
    private User user;

    // Field to store improvement suggestions for the user's response
    @Column(name = "Improvement_Suggestions")
    private String improvementSuggestions;

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InterviewSession getInterviewSession() {
        return interviewSession;
    }

    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }
}
