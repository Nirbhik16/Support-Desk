package com.supportdesk.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCommentRequest {
    @NotBlank
    private String message;

    Boolean isInternal = false;
}
