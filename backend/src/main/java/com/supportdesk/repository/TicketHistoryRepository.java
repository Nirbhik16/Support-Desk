package com.supportdesk.repository;

import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketHistory;
import com.supportdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

    // Full history for a ticket — ordered oldest first
    List<TicketHistory> findByTicketOrderByCreatedAtAsc(Ticket ticket);

    // Who changed what on a ticket
    List<TicketHistory> findByTicketAndFieldChanged(Ticket ticket, String fieldChanged);

    // All changes made by a specific user
    List<TicketHistory> findByChangedBy(User user);
}
