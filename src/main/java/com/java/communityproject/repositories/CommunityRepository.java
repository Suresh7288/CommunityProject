package com.java.communityproject.repositories;

import com.java.communityproject.models.Community;
import com.java.communityproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, UUID> {
    List<Community> findByMembersUser(User user);
    Optional<Community> findById(UUID id);

    @Query("SELECT c FROM Community c WHERE LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(concat('%', :query, '%'))")
    List<Community> searchCommunities(@Param("query") String query);
    // Check if a user is a member of a community
    @Query("SELECT COUNT(m) > 0 FROM CommunityMember m WHERE m.community.id = :communityId AND m.user.id = :userId")
    boolean existsCommunityMemberByCommunityIdAndUserId(
            @Param("communityId") UUID communityId,
            @Param("userId") UUID userId
    );
}
