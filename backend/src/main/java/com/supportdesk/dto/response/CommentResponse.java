package com.supportdesk.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String message;
    private Boolean isInternal;
    private UserResponse author;
    private Long ticketId;
    private LocalDateTime createdAt;
}
