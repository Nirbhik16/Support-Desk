package com.supportdesk.dto.response;

import com.supportdesk.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
