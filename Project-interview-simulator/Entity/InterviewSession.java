package com.InterviewSimulator.entity;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "interview_session")
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Session_Id", updatable = false, nullable = false)
    private int sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "schedule_date", nullable = false)
    private Date scheduleDate;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "token", unique = true)
    private String token;

    // Getters and Setters
    public int getId() {
        return sessionId;
    }

    public void setId(int sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time time) {
        this.startTime = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
