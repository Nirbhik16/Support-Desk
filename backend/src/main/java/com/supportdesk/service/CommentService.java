package com.supportdesk.service;

import com.supportdesk.dto.request.AddCommentRequest;
import com.supportdesk.dto.response.CommentResponse;
import com.supportdesk.dto.response.UserResponse;
import com.supportdesk.entity.Comment;
import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.User;
import com.supportdesk.enums.Role;
import com.supportdesk.repository.CommentRepository;
import com.supportdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    public CommentResponse addComment(Long ticketId, AddCommentRequest request, User currentUser){
        Ticket ticket = ticketRepository.findById(ticketId)
                .filter(t->!t.getIsDeleted())
                .orElseThrow(()->new RuntimeException("Ticket not found"));
        if(request.getIsInternal() && currentUser.getRole() == Role.CUSTOMER) {
            throw new RuntimeException("Customers cannot post internal comments");
        }

        Comment comment = Comment.builder()
                .message(request.getMessage())
                .isInternal(request.getIsInternal())
                .ticket(ticket)
                .author(currentUser)
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    public List<CommentResponse> getComments(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .filter(t->!t.getIsDeleted())
                .orElseThrow(()->new RuntimeException("Ticket not found"));

        // customers see only public comments
        // agents and admins see everything including internal notes
        List<Comment> comments = currentUser.getRole() == Role.CUSTOMER ?
                commentRepository.findByTicketAndIsInternalFalseOrderByCreatedAtAsc(ticket) :
                commentRepository.findByTicketOrderByCreatedAtAsc(ticket);

        return comments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .isInternal(comment.isInternal())
                .ticketId(comment.getTicket().getId())
                .createdAt(comment.getCreatedAt())
                .author(UserResponse.builder()
                        .id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getName())
                        .email(comment.getAuthor().getEmail())
                        .role(comment.getAuthor().getRole())
                        .build())
                .build();
    }
}
