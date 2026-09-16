package com.supportdesk.dto.request;

import com.supportdesk.enums.Priority;
import com.supportdesk.enums.Source;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Ticket description is required")
    private String description;

    @NotNull(message = "Ticket Priority is required")
    private Priority priority;

    private Source source = Source.WEB;
}
