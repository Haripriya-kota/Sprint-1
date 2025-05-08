package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Answer;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object (DAO) class for handling operations related to the Answer entity.
 */
public class AnswerDAO {

    /**
     * Saves an Answer entity to the database.
     * It ensures the Answer object is correctly populated before persisting.
     * If an exception occurs, the transaction is rolled back to maintain database consistency.
     *
     * @param answer The Answer entity to be saved.
     */
    public void saveAnswer(Answer answer) {
        // Open a new session to interact with the Hibernate session factory
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            // Begin a new transaction
            transaction = session.beginTransaction();
            
            // Persist the Answer entity to the database
            session.persist(answer);  // Make sure the answer object is populated correctly before this
            
            // Commit the transaction to save the changes to the database
            transaction.commit();
        } catch (Exception e) {
            // If an error occurs, rollback the transaction to maintain database integrity
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();  // Print the error for debugging purposes
        } finally {
            // Ensure the session is closed to release database resources
            session.close();
        }
    }
}
