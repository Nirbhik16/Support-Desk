package com.supportdesk.dto.response;

import com.supportdesk.enums.Category;
import com.supportdesk.enums.Priority;
import com.supportdesk.enums.Source;
import com.supportdesk.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private Priority priority;
    private Category category;
    private Source source;

    private UserResponse createdBy;
    private UserResponse assignedTo;   // nullable

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;  // nullable
}
