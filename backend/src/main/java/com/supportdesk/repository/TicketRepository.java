package com.supportdesk.repository;

import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.User;
import com.supportdesk.enums.Category;
import com.supportdesk.enums.Priority;
import com.supportdesk.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository {

    // All tickets created by a specific customer
    List<Ticket> findByCreatedBy(User user);

    // All tickets assigned to a specific agent
    List<Ticket> findByAssignedTo(User agent);

    // All tickets with a specific status
    List<Ticket> findByStatus(TicketStatus status);

    // All tickets with a specific priority
    List<Ticket> findByPriority(Priority priority);

    // All tickets with a specific category
    List<Ticket> findByCategory(Category category);

    // Unassigned tickets — admin uses this to assign work
    List<Ticket> findByAssignedToIsNull();

    // Agent dashboard — tickets assigned to me with a specific status
    List<Ticket> findByAssignedToAndStatus(User agent, TicketStatus status);

    // Customer — their tickets with a specific status
    List<Ticket> findByCreatedByAndStatus(User customer, TicketStatus status);

    // Admin stats — count tickets by status
    Long countByStatus(TicketStatus status);

    // Count tickets assigned to a specific agent
    Long countByAssignedTo(User agent);

    // Tickets with pagination support — for large lists
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    Page<Ticket> findByAssignedTo(User agent, Pageable pageable);

    // Search tickets by title keyword
    @Query("SELECT t FROM Ticket t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))" )
    List<Ticket> searchByKeyword(@Param("keyword") String keyword);

    // Admin analytics — tickets created between two dates
    List<Ticket> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Tickets resolved between two dates — for performance reports
    List<Ticket> findByResolvedAtBetween(LocalDateTime start, LocalDateTime end);
}
