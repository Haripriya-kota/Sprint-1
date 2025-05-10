package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report") // Maps this class to the "report" table in the database
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Report_Id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "Session_Id")
    private InterviewSession interviewSession;

    @ManyToOne
    @JoinColumn(name = "Response_Id")
    private Response response;

    @ManyToOne
    @JoinColumn(name = "Q_Id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "User_Id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

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

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }
}
