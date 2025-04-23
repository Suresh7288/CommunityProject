package com.java.communityproject.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String content;


    @ManyToOne
    @JoinColumn(name = "community_id",nullable = false)
    private Community community;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
