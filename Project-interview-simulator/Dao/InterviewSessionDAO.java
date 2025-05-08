package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.InterviewSession;
import com.InterviewSimulator.util.HibernateUtil;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object (DAO) class responsible for interacting with the InterviewSession entity.
 * This class contains methods for saving, retrieving, and updating interview sessions.
 */
public class InterviewSessionDAO {

    /**
     * Saves an InterviewSession entity to the database.
     * This method uses Hibernate's session management and transaction handling to persist the session.
     * 
     * @param session The InterviewSession object to be saved.
     */
    public void saveSession(InterviewSession session) {
        Transaction transaction = null;
        try (Session sessionObj = HibernateUtil.getSessionFactory().openSession()) {
            // Start a transaction
            transaction = sessionObj.beginTransaction();

            // Log the ID of the session before saving
            System.out.println("🔍 Before saving, Session ID: " + session.getId());

            // Persist the InterviewSession object into the database
            sessionObj.persist(session);

            // Commit the transaction to save the changes to the database
            transaction.commit();

            // Log the successful save and the assigned session ID
            System.out.println("✅ Interview Session Saved! Assigned ID: " + session.getId());
        } catch (Exception e) {
            // If an error occurs, roll back the transaction to maintain consistency
            if (transaction != null) transaction.rollback();
            e.printStackTrace();  // Print the stack trace for debugging purposes
        }
    }

    /**
     * Retrieves the latest InterviewSession for a specific user.
     * The latest session is determined by ordering the sessions by sessionId in descending order.
     *
     * @param userId The ID of the user whose latest session is to be fetched.
     * @return The latest InterviewSession for the user, or null if no session exists.
     */
    public InterviewSession getLatestSessionForUser(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Create a query to fetch the latest session for the given userId
            Query<InterviewSession> query = session.createQuery(
                "FROM InterviewSession WHERE user.id = :userId ORDER BY sessionId DESC",
                InterviewSession.class
            );
            // Set the parameter for userId
            query.setParameter("userId", userId);
            // Limit the result to 1, as we only need the latest session
            query.setMaxResults(1);

            // Return the unique result (or null if no result is found)
            return query.uniqueResult();
        }
    }

    /**
     * Updates an existing InterviewSession in the database.
     * This method merges the updated session with the existing session in the database.
     *
     * @param updatedSession The InterviewSession object with updated details.
     */
    public void updateSession(InterviewSession updatedSession) {
        Transaction transaction = null;
        try (Session sessionObj = HibernateUtil.getSessionFactory().openSession()) {
            // Start a transaction
            transaction = sessionObj.beginTransaction();

            // Merge the updated session with the existing session in the database (based on ID)
            sessionObj.merge(updatedSession);

            // Commit the transaction to save the changes to the database
            transaction.commit();
            System.out.println("✅ Interview Session updated successfully.");
        } catch (Exception e) {
            // If an error occurs, roll back the transaction to maintain consistency
            if (transaction != null) transaction.rollback();
            e.printStackTrace();  // Print the stack trace for debugging purposes
        }
    }

    /**
     * Retrieves all upcoming interview sessions from the database.
     * An interview session is considered "upcoming" if its scheduled date is today or in the future.
     *
     * @return A list of upcoming InterviewSession objects, ordered by schedule date.
     */
    public List<InterviewSession> getAllUpcomingSessions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Create a query to fetch all upcoming sessions
            Query<InterviewSession> query = session.createQuery(
                "FROM InterviewSession WHERE scheduleDate >= CURRENT_DATE ORDER BY scheduleDate ASC",
                InterviewSession.class
            );
            // Return the list of upcoming sessions
            return query.list();
        }
    }
}
