package com.java.communityproject.services;

import com.java.communityproject.models.Community;
import com.java.communityproject.models.CommunityMember;
import com.java.communityproject.models.Notification;
import com.java.communityproject.models.User;
import com.java.communityproject.repositories.CommunityRepository;
import com.java.communityproject.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Community createCommunity(String name, String description, boolean isPrivate, User user) {
        Community community = Community.builder()
                .name(name)
                .description(description)
                .isPrivate(isPrivate)
                .createdBy(user.getId())
                .createdAt(LocalDateTime.now())
                .build();

        CommunityMember adminMember = CommunityMember.builder()
                .community(community)
                .user(user)
                .role("ADMIN")
                .status("APPROVED") // ✅ Fix: required field
                .joinedAt(LocalDateTime.now())
                .build();

        community.setMembers(Set.of(adminMember));

        return communityRepository.save(community);
    }

    public CommunityMember joinCommunity(UUID communityId, User user, boolean isPrivate) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        CommunityMember member = CommunityMember.builder()
                .community(community)
                .user(user)
                .role("MEMBER")
                .status(isPrivate ? "PENDING" : "APPROVED")
                .joinedAt(LocalDateTime.now())
                .build();

        community.getMembers().add(member);
        communityRepository.save(community);

        return member;
    }

    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    public List<Community> getJoinedCommunities(User user) {
        return communityRepository.findByMembersUser(user);
    }

    public void leaveCommunity(UUID communityId, User user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        CommunityMember member = community.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not a member of this community"));

        community.getMembers().remove(member);
        communityRepository.save(community);
    }

    public CommunityMember approveJoinRequest(UUID communityId, UUID userId, User moderator) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        CommunityMember moderatorMember = community.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(moderator.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Moderator is not a member of this community"));

        if (!moderatorMember.getRole().equals("ADMIN") && !moderatorMember.getRole().equals("MODERATOR")) {
            throw new RuntimeException("Only admins/moderators can approve join requests");
        }

        CommunityMember member = community.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in community"));

        member.setStatus("APPROVED");

        sendRealTimeNotification(member.getUser(), "Your join request for community " + community.getName() + " has been approved.");

        return communityRepository.save(community).getMembers().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to update member status"));
    }

    public void removeMember(UUID communityId, UUID userId, User moderator) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        CommunityMember moderatorMember = community.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(moderator.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Moderator is not a member of this community"));

        if (!moderatorMember.getRole().equals("ADMIN") && !moderatorMember.getRole().equals("MODERATOR")) {
            throw new RuntimeException("Only admins/moderators can remove members");
        }

        CommunityMember member = community.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in community"));

        community.getMembers().remove(member);
        sendRealTimeNotification(member.getUser(), "You have been removed from community " + community.getName() + ".");
        communityRepository.save(community);
    }

//    public void sendNotification(User user, String message) {
//        Notification notification = Notification()
//                .user(user)
//                .message(message)
//                .createdAt(LocalDateTime.now())
//                .isRead(false)
//                .build();
//
//        notificationRepository.save(notification);
//    }

    public void sendRealTimeNotification(User user, String message) {
        messagingTemplate.convertAndSendToUser(user.getId().toString(), "/queue/notifications", message);
    }

    public boolean isUserMemberOfCommunity(UUID communityId, UUID userId) {
        return communityRepository.existsCommunityMemberByCommunityIdAndUserId(communityId, userId);
    }

    public Community getCommunityById(UUID communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
    }

    public List<Community> searchCommunities(String query) {
        return communityRepository.searchCommunities(query);
    }
}
