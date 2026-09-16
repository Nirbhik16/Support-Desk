package com.supportdesk.controller;

import com.supportdesk.dto.request.CreateTicketRequest;
import com.supportdesk.dto.request.UpdateTicketRequest;
import com.supportdesk.dto.response.TicketResponse;
import com.supportdesk.entity.User;
import com.supportdesk.service.TicketService;
import com.supportdesk.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        TicketResponse response = ticketService.createTicket(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        User currentUser = SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(ticketService.getAllTickets(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id){
        User currentUser = SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(ticketService.getTicketById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @RequestBody UpdateTicketRequest ticket) {
        User currentUser = SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(ticketService.updateTicket(id, ticket, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
        User currentUser = SecurityUtil.getCurrentUser();
        ticketService.deleteTicketById(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
