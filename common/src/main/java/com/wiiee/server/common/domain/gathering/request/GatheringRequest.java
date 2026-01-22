package com.wiiee.server.common.domain.gathering.request;

import com.wiiee.server.common.domain.DefaultEntity;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gathering_request", indexes = {
    @Index(name = "idx_gathering_request_gathering_status", columnList = "gathering_id, gathering_request_status")
})
@Entity
public class GatheringRequest extends DefaultEntity {

    @Id
    @Column(name = "gathering_request_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User requestUser;

    @JoinColumn(name = "gathering_id",
            referencedColumnName = "gathering_id",
            nullable = false)
    @ManyToOne(fetch = LAZY)
    private Gathering gathering;

    @Column(name = "gathering_request_status")
    @Enumerated(value = EnumType.STRING)
    private GatheringRequestStatus gatheringRequestStatus;

    @Column(length = 200)
    private String requestReason;

    @Builder
    public GatheringRequest(User requestUser, Gathering gathering, String requestReason) {
        this.requestUser = requestUser;
        this.gathering = gathering;
        this.gatheringRequestStatus = GatheringRequestStatus.UNVERIFIED;
        this.requestReason = requestReason;
    }

    public GatheringRequest(User requestUser, Gathering gathering, GatheringRequestStatus gatheringRequestStatus, String requestReason) {
        this.requestUser = requestUser;
        this.gathering = gathering;
        this.gatheringRequestStatus = gatheringRequestStatus;
        this.requestReason = requestReason;
    }

    public void updateRequestStatus(GatheringRequestStatus gatheringRequestStatus) {
        this.gatheringRequestStatus = gatheringRequestStatus;
    }
}
