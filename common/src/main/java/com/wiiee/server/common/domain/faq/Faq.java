package com.wiiee.server.common.domain.faq;

import com.wiiee.server.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "faq", indexes = {})
@Entity
public class Faq extends BaseEntity {

    @Id
    @Column(name = "faq_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    public Faq(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
