package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Question;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object (DAO) class for handling operations related to the Question entity.
 */
public class QuestionDAO {

    /**
     * Saves a Question object to the database.
     * 
     * @param question The Question entity to be persisted.
     */
    public void saveQuestion(Question question) {
        Transaction transaction = null;

        // Try-with-resources block to open a Hibernate session
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start a transaction
            transaction = session.beginTransaction();

            // Save the Question object to the database
            session.persist(question);

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            // Rollback in case of an exception to avoid partial commits
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the stack trace for debugging
            e.printStackTrace();
        }
    }
}
