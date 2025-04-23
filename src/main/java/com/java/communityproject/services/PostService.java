package com.java.communityproject.services;

import com.java.communityproject.models.Community;
import com.java.communityproject.models.Post;
import com.java.communityproject.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post createPost(String content,Community community) {
        Post post = new Post();
        post.setId(UUID.randomUUID()); // Generate a unique ID for the post
        post.setContent(content);
        post.setCommunity(community);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

}
