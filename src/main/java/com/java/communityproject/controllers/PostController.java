package com.java.communityproject.controllers;

import com.java.communityproject.models.Community;

import com.java.communityproject.models.Post;
import com.java.communityproject.services.CommunityService;
import com.java.communityproject.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts/{communityId}")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommunityService communityService;
    @PostMapping
    public ResponseEntity<Post> createPost(
            @PathVariable UUID communityId,
            @RequestParam String content)
//            @AuthenticationPrincipal User user)
{

        // Check if the user is a member of the community
//        if (!communityService.isUserMemberOfCommunity(communityId, user.getId())) {
//            return ResponseEntity.status(403).build(); // 403 Forbidden
//        }

        // Fetch the community
        Community community = communityService.getCommunityById(communityId);

        // Create the post
        Post post = postService.createPost(content, community);
        return ResponseEntity.ok(post);
    }

}
