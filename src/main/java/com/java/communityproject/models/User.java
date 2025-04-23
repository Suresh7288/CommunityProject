package com.java.communityproject.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="user")
@JsonIgnoreProperties
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String bio;

    private String profilePicture;
    // Relationships
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> role; //Roles: Admin, Moderator, Member

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role)
                .toList();
    }
    @Override
    public String getUsername() {
        return email; // Use email as the username
    }

}
