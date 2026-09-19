package com.supportdesk.controller;

import com.supportdesk.dto.request.AddCommentRequest;
import com.supportdesk.dto.response.CommentResponse;
import com.supportdesk.entity.User;
import com.supportdesk.service.CommentService;
import com.supportdesk.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Ticket comment thread APIs")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody AddCommentRequest request
            ) {
        User currentUser = SecurityUtil.getCurrentUser();
        CommentResponse response = commentService.addComment(ticketId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long ticketId) {
        User currentUser = SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(commentService.getComments(ticketId, currentUser));
    }
}
