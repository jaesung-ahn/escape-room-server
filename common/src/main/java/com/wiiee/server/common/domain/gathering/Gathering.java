package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gathering", indexes = {})
@Entity
public class Gathering extends BaseEntity {

    @Id
    @Column(name = "gathering_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "content_id",
            referencedColumnName = "content_id",
            nullable = false)
    private Content content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User leader;

    @Embedded
    private GatheringInfo gatheringInfo;

    @OneToMany(mappedBy = "gathering", cascade = {PERSIST, REMOVE})
    private Set<GatheringMember> gatheringMembers = new HashSet<>();

    public Gathering(Content content, User user, GatheringInfo gatheringInfo) {
        this.content = content;
        this.leader = user;
        this.gatheringInfo = gatheringInfo;
    }

    public boolean isContainUser(Long userId) {
        return gatheringMembers.stream().anyMatch(member -> member.getUser().getId().equals(userId));
    }

    public GatheringMember addMember(User user) {
        boolean isLeader = this.leader.equals(user);
        final var memberToAdd = new GatheringMember(user, this, isLeader);
        gatheringMembers.add(memberToAdd);
        return memberToAdd;
    }

    public void deleteMember(GatheringMember gatheringMember) {
        gatheringMembers.remove(gatheringMember);
    }

    public void deleteAllMember(Set<GatheringMember> gatheringMembers) {
        gatheringMembers.removeAll(gatheringMembers);
    }

    public void delete(Long userId) {
        if (!leader.getId().equals(userId)) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
