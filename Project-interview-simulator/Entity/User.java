package com.InterviewSimulator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "user") // Maps this class to the "user" table in the database
public class User {

    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_Id")
    private int id;

    // Name field: cannot be null or blank, max length 100
    @Column(name = "Name", nullable = false, length = 100)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    // Email field: must be valid and unique
    @Column(name = "Email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    // Mobile number: must be exactly 10 digits, unique
    @Column(name = "Mobile_No", nullable = false, unique = true, length = 15)
    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be exactly 10 digits")
    private String mobileNo;

    // Password: at least 6 characters
    @Column(name = "Password", nullable = false, length = 255)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // Experience level: must be one of the three specified values
    @Column(name = "Experience_Level", length = 50, nullable = false)
    @NotBlank(message = "Experience level cannot be empty")
    @Pattern(regexp = "beginner|intermediate|expert", message = "Experience level must be 'beginner', 'intermediate', or 'expert'")
    private String experienceLevel;

    // One-to-many relationship with InterviewSession
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InterviewSession> interviewSessions;

    
    // Default constructor (no-argument constructor)
    public User() {
        // Hibernate requires a no-argument constructor
    }
    
    // Constructor to create User with all required fields (except ID and sessions)
    public User(String name, String email, String mobileNo, String password, String experienceLevel) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.experienceLevel = experienceLevel;
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
}
