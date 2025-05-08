package com.InterviewSimulator.entity;

import jakarta.persistence.*;


	@Entity
	@Table(name = "answer")
	public class Answer {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int Answer_Id;
	    
	    @ManyToOne
	    @JoinColumn(name = "Q_Id")
	    private Question question;
	    
	    @Column(name = "Answer_Text")
	    private String answerText;
	    
	    @Column(name = "Question_Text")
	    private String questionText;
	    
	    // Getters and setters...
	
    // Getters and Setters


    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;  // Set the question text here
    }
}

