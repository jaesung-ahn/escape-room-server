package com.wiiee.server.common.domain.content.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "tag", indexes = {})
@Entity
public class Tag {

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "value", unique = true, nullable = false)
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return value.equals(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public Tag(String value) {
        this.value = value;
    }
}
