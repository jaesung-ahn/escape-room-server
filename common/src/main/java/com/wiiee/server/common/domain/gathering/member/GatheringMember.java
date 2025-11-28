package com.wiiee.server.common.domain.gathering.member;

import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "gathering_member", indexes = {})
@Entity
public class GatheringMember {

    @Id
    @Column(name = "gathering_member_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    @ManyToOne(fetch = LAZY)
    private User user;

    @JoinColumn(name = "gathering_id",
            referencedColumnName = "gathering_id",
            nullable = false)
    @ManyToOne(fetch = LAZY)
    private Gathering gathering;

    private Boolean isLeader;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public GatheringMember(User user, Gathering gathering, Boolean isLeader) {
        this.user = user;
        this.gathering = gathering;
        this.isLeader = isLeader;
        this.status = Status.APPROVAL;
    }

    public void updateStatus(int code) {
        this.status = Status.valueOf(code);
    }
}
