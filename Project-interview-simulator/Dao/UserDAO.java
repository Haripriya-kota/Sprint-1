package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.User;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * Data Access Object (DAO) class for handling operations related to the User entity.
 */
public class UserDAO {

    private Validator validator;

    /**
     * Constructor that initializes the Hibernate validator.
     */
    public UserDAO() {
        // Create a ValidatorFactory and initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Checks if a user with the given email already exists in the database.
     *
     * @param email The email to check for existence.
     * @return true if the email exists, false otherwise.
     */
    public boolean emailExists(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {  // ✅ Fixed
            // Create a Hibernate query to check if a user with the given email exists
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return !query.getResultList().isEmpty();  // Return true if result is not empty
        }
    }

    /**
     * Saves a new user to the database after validating its fields.
     *
     * @param user The User entity to be saved.
     */
    public void saveUser(User user) {
        // Validate user fields before saving
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            // Print validation errors if any
            for (ConstraintViolation<User> violation : violations) {
                System.out.println("Validation error: " + violation.getMessage());
            }
            return;  // Stop saving the user if there are validation errors
        }

        // Check if the email already exists in the database
        if (emailExists(user.getEmail())) {
            System.out.println("❌ Email already exists! Please use a different email.");
            return;  // Stop the process if the email is already taken
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start a new transaction
            transaction = session.beginTransaction();
            // Persist the new user to the database
            session.persist(user);
            // Commit the transaction
            transaction.commit();
            System.out.println("✅ Account created successfully!");
        } catch (Exception e) {
            // Rollback transaction if any error occurs
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a user from the database by email.
     *
     * @param email The email of the user to retrieve.
     * @return The User object, or null if no user is found.
     */
    public User getUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Create a query to fetch a user by email
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            // Return the unique result (user) or null if not found
            return query.uniqueResult();
        }
    }

    /**
     * Retrieves a user from the database by user ID.
     *
     * @param userId The user ID to retrieve.
     * @return The User object, or null if no user is found.
     */
    public User getUserById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Retrieve the user by its primary key (userId)
            return session.get(User.class, userId);
        }
    }
}
