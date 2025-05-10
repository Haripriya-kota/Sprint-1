package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Answer_Id")
    private int answerId;

    @ManyToOne
    @JoinColumn(name = "Q_Id", referencedColumnName = "Q_Id")
    private Question question;

    @Column(name = "Answer_Text", nullable = false)
    private String answerText;

    @Column(name = "Question_Text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    // 🔧 Add the missing user field and mapping
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
