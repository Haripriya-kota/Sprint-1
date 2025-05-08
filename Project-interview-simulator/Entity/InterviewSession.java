package com.InterviewSimulator.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.sql.Time;

@Entity
@Table(name = "interview_session") // Maps this class to the "interview_session" table in the database
public class InterviewSession {

    // Primary key for the interview session, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Session_Id", updatable = false, nullable = false)
    private int sessionId;

    // Many-to-one relationship with the User entity; each session is linked to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key referencing the User table
    private User user;

    // Date on which the interview is scheduled (only date part)
    @Column(name = "schedule_date", nullable = false)
    private Date scheduleDate;

    // Start time of the interview session
    @Column(name = "start_time", nullable = false)
    private Time startTime;

    // End time of the interview session
    @Column(name = "end_time", nullable = false)
    private Time endTime;

    // Flag to indicate whether a reminder has been sent to the user or not
    @Column(name = "reminder_sent")
    private boolean reminderSent = false;

    // Getter for session ID
    public int getId() {
        return sessionId;
    }

    // Setter for session ID
    public void setId(int sessionId) {
        this.sessionId = sessionId;
    }

    // Getter for user
    public User getUser() {
        return user;
    }

    // Setter for user
    public void setUser(User user) {
        this.user = user;
    }

    // Getter for schedule date
    public Date getScheduleDate() {
        return scheduleDate;
    }

    // Setter for schedule date
    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    // Getter for start time
    public Time getStartTime() {
        return startTime;
    }

    // Setter for start time
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    // Getter for end time
    public Time getEndTime() {
        return endTime;
    }

    // Setter for end time
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    // Getter for reminderSent flag
    public boolean isReminderSent() {
        return reminderSent;
    }

    // Setter for reminderSent flag
    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }
}
