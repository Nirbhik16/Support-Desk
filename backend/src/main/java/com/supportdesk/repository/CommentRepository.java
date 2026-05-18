package com.supportdesk.repository;

import com.supportdesk.entity.Comment;
import com.supportdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // All comments for a ticket — ordered oldest first
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);

    // Only public comments — for customer view
    List<Comment> findByTicketAndIsInternalFalseOrderByCreatedAtAsc(Ticket ticket);

    // Only internal notes — for agent view
    List<Comment> findByTicketAndIsInternalTrueOrderByCreatedAtAsc(Ticket ticket);

    // Count comments on a ticket
    Long countByTicket(Ticket ticket);
}
