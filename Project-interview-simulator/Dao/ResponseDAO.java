package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Response;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;  // Import this for the new query API

/**
 * Data Access Object (DAO) class for handling operations related to the Response entity.
 */
public class ResponseDAO {

    /**
     * Saves a response to the database.
     * If the response text is empty or null, it is populated with a default value.
     * If the response already exists for the same user and question, it is not saved again.
     *
     * @param response The Response entity to be saved.
     * @return The saved Response entity if successful, otherwise null.
     */
    public Response saveResponse(Response response) {
        // Ensure responseText is populated with a default value if it's null or empty
        if (response.getResponseText() == null || response.getResponseText().isEmpty()) {
            response.setResponseText("No response provided"); // Default response text
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start a new transaction
            transaction = session.beginTransaction();

            // Log the response before saving
            System.out.println("✅ Saving response: " + response);

            // Use the modern query API: createQuery returns Query<Response> now
            String hql = "FROM Response WHERE user = :user AND question = :question";
            Query<Response> query = session.createQuery(hql, Response.class);
            query.setParameter("user", response.getUser());  // Set the user parameter for the query
            query.setParameter("question", response.getQuestion());  // Set the question parameter for the query

            // Execute the query and check if the response already exists for this user and question
            Response existingResponse = query.uniqueResult();

            if (existingResponse == null) {
                // If the response doesn't exist, save it to the database
                session.persist(response);
            } else {
                // Log if a response already exists for this user and question
                System.out.println("❌ Response already exists for this question.");
            }

            // Flush to ensure the ID is generated (if it's a new entity)
            session.flush();

            // Commit the transaction to save changes to the database
            transaction.commit();
            System.out.println("✅ Response saved successfully: " + response);

            // Return the saved response
            return response;
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            // Handle constraint violation (e.g., duplicate entries) by rolling back the transaction
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            System.out.println("❌ Constraint violation: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions by rolling back the transaction and logging the error
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            System.out.println("❌ Error saving Response: " + e.getMessage());
        }

        // Return null if there was an error during saving
        return null;
    }
}
