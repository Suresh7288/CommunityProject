package com.java.communityproject.controllers;

import com.java.communityproject.models.Community;
import com.java.communityproject.models.CommunityMember;
import com.java.communityproject.models.User;
import com.java.communityproject.repositories.CommunityRepository;
import com.java.communityproject.services.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityRepository communityRepository;

    @PostMapping
    public ResponseEntity<Community> createCommunity(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam boolean isPrivate,
            @AuthenticationPrincipal User user) {
        Community created = communityService.createCommunity(name, description, isPrivate, user);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<CommunityMember> joinCommunity(
            @PathVariable UUID communityId,
            @AuthenticationPrincipal User user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        CommunityMember member = communityService.joinCommunity(communityId, user, community.isPrivate());
        return ResponseEntity.ok(member);
    }

    @GetMapping
    public ResponseEntity<List<Community>> getAllCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/joined")
    public ResponseEntity<List<Community>> getJoinedCommunities(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(communityService.getJoinedCommunities(user));
    }

    @DeleteMapping("/{communityId}/leave")
    public ResponseEntity<Void> leaveCommunity(
            @PathVariable UUID communityId,
            @AuthenticationPrincipal User user) {
        communityService.leaveCommunity(communityId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{communityId}/approve/{userId}")
    public ResponseEntity<CommunityMember> approveJoinRequest(
            @PathVariable UUID communityId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal User moderator) {
        CommunityMember approved = communityService.approveJoinRequest(communityId, userId, moderator);
        return ResponseEntity.ok(approved);
    }

    @DeleteMapping("/{communityId}/remove/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID communityId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal User moderator) {
        communityService.removeMember(communityId, userId, moderator);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Community>> searchCommunities(@RequestParam String query) {
        return ResponseEntity.ok(communityService.searchCommunities(query));
    }
}
