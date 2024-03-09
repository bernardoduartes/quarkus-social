package br.shizuca.social.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowerRequest {
    @NotNull(message = "Follow is required.")
    private Long followerId;
}