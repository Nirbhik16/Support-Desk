package com.supportdesk.service;

import com.supportdesk.dto.request.CreateTicketRequest;
import com.supportdesk.dto.request.UpdateTicketRequest;
import com.supportdesk.dto.response.TicketResponse;
import com.supportdesk.dto.response.UserResponse;
import com.supportdesk.entity.Ticket;
import com.supportdesk.entity.TicketHistory;
import com.supportdesk.entity.User;
import com.supportdesk.enums.Role;
import com.supportdesk.enums.TicketStatus;
import com.supportdesk.repository.TicketHistoryRepository;
import com.supportdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private TicketRepository ticketRepository;
    private TicketHistoryRepository ticketHistoryRepository;

    public TicketResponse createTicket(CreateTicketRequest request, User currentUser) {
        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .source(request.getSource())
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();

        Ticket saved = ticketRepository.save(ticket);

        logHistory(saved, currentUser, "status", null, TicketStatus.OPEN.name());

        return mapToResponse(saved);
    }

    public List<TicketResponse> getAllTickets(User currentUser) {
        List<Ticket> tickets = switch(currentUser.getRole()){
            case CUSTOMER ->  ticketRepository.findByCreatedBy(currentUser);
            case AGENT -> ticketRepository.findByAssignedTo(currentUser);
            case ADMIN -> ticketRepository.findAll();
        };
        return tickets.stream()
                .filter(ticket -> !ticket.getIsDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse getTicketById(Long id, User currentUser) {
        Ticket ticket = findTicketById(id);
        validateAccess(ticket, currentUser);
        return mapToResponse(ticket);
    }

    public TicketResponse updateTicket(Long id, UpdateTicketRequest request, User currentUser) {
        Ticket ticket = findTicketById(id);
        validateAccess(ticket, currentUser);

        if(request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }

        if(request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }

        if(request.getPriority() != null) {
            logHistory(ticket, currentUser, "priority",
                    ticket.getPriority().name(), request.getPriority().name());
            ticket.setPriority(request.getPriority());
        }

        if (request.getTicketStatus() != null) {
            validateStatusTransition(ticket.getStatus(), request.getTicketStatus());
            logHistory(ticket, currentUser, "status",
                    ticket.getStatus().name(), request.getTicketStatus().name());
            ticket.setStatus(request.getTicketStatus());

            if (ticket.getStatus() == TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }

        Ticket updated = ticketRepository.save(ticket);
        return mapToResponse(updated);
    }

    public void deleteTicketById(Long id, User currentUser) {
        Ticket ticket = findTicketById(id);

        // only admin or the customer who created it can delete
        if(currentUser.getRole() != Role.ADMIN &&
                !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        ticket.setIsDeleted(true);
        ticketRepository.save(ticket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .source(ticket.getSource())
                .createdBy(mapUserToResponse(ticket.getCreatedBy()))
                .assignedTo(ticket.getAssignedTo() != null
                        ? mapUserToResponse(ticket.getAssignedTo())
                        : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private Ticket findTicketById(Long id) {
        return ticketRepository.findById(id)
                .filter(t -> !t.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    private void validateAccess(Ticket ticket, User currentUser) {
        if(currentUser.getRole() == Role.ADMIN) return;

        if(currentUser.getRole() == Role.CUSTOMER
                && !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        if(currentUser.getRole() == Role.AGENT
                && ( ticket.getAssignedTo() == null
                || !ticket.getAssignedTo().getId().equals(currentUser.getId()) )) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateStatusTransition(TicketStatus current, TicketStatus next) {
        boolean valid = switch (current) {
            case OPEN -> next == TicketStatus.IN_PROGRESS
                    || next == TicketStatus.ON_HOLD
                    || next == TicketStatus.CLOSED;
            case IN_PROGRESS -> next == TicketStatus.ON_HOLD
                    || next == TicketStatus.RESOLVED;
            case ON_HOLD -> next == TicketStatus.IN_PROGRESS
                    || next == TicketStatus.CLOSED;
            case RESOLVED -> next == TicketStatus.CLOSED
                    || next == TicketStatus.IN_PROGRESS;
            case CLOSED -> false;
        };

        if(!valid) {
            throw new RuntimeException("Invalid status transition: " + current + "->" + next);
        }
    }

    // ── History Logger ────────────────────────────────────

    private void logHistory(Ticket ticket, User changedBy,
                            String field, String oldVal, String newVal) {
        TicketHistory history = TicketHistory.builder()
                .ticket(ticket)
                .changedBy(changedBy)
                .fieldChanged(field)
                .oldValue(oldVal)
                .newValue(newVal)
                .build();

        ticketHistoryRepository.save(history);
    }

}
