package com.birdbook.models;

public record UserSummaryDTO(
        String id,
        String username,
        String role,
        String profilePic,
        String[] friends,
        String[] posts,
        String[] groups
) {}
