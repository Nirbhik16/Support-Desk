package com.supportdesk.repository;

import com.supportdesk.entity.Attachment;
import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // All attachments for a ticket
    List<Attachment> findByTicket(Ticket ticket);

    // All attachments uploaded by a user
    List<Attachment> findByUploadedBy(User user);

    // Check file count for a ticket — useful for limiting uploads
    Long countByTicket(Ticket ticket);
}
