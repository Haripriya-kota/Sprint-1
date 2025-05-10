package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Report;
import com.InterviewSimulator.entity.Response;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object (DAO) class for handling operations related to the Report entity.
 */
public class ReportDAO {

    /**
     * Saves a Report entity to the database.
     * This method first ensures that the associated Response entity is properly persisted.
     * If the Response entity is detached (not attached to the session), it is merged before persisting the Report.
     * If an exception occurs, the transaction is rolled back to maintain database consistency.
     *
     * @param report The Report entity to be saved.
     */
    public void saveReport(Report report) {
        Transaction transaction = null;
        Session session = null;  // Declare session outside try block to handle it in finally block
        try {
            // Open a new session to interact with the Hibernate session factory
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Start a new transaction
            transaction = session.beginTransaction();

            // ✅ First, persist the associated Response entity
            Response response = report.getResponse();
            
            // If the Response entity is detached (it has an existing ID), merge it to the current session
            if (response.getId() != null) {
                session.merge(response);  // Merge to attach the Response entity to the session
            } else {
                session.persist(response);  // Otherwise, persist a new Response entity
            }
            
            // Ensure the ID is generated for the Response entity before persisting the Report
            session.flush();  // Flush the session to ensure that the ID is generated

            // ✅ Now persist the Report entity with the generated response_id
            session.persist(report);  // Save the Report entity along with its associated Response
            
            // Commit the transaction to save the changes to the database
            transaction.commit();
 
        } catch (Exception e) {
            // If an error occurs, rollback the transaction to ensure database consistency
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();  // Print the error for debugging purposes
            System.out.println("❌ Error saving Report: " + e.getMessage());
        } finally {
            // Ensure the session is closed to release database resources
            if (session != null) {
                session.close();  // Close session here to avoid potential errors
            }
        }
    }
}
