package com.supportdesk.dto.request;

import com.supportdesk.enums.Priority;
import com.supportdesk.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketRequest {
    private String title;
    private String description;
    private Priority priority;
    private TicketStatus ticketStatus;
}
