package kr.codeit.relaxtogether.entity.gathering;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gatherings")
@Entity
public class Gathering extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_id")
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private Location location;

    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    private LocalDateTime dateTime;
    private LocalDateTime registrationEnd;
    private int capacity;
    private boolean isDeleted;

    @Builder
    private Gathering(String name, Location location, String imageUrl, Type type, LocalDateTime dateTime,
        LocalDateTime registrationEnd, int capacity) {
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
        this.type = type;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.capacity = capacity;
        this.isDeleted = false;
    }
}
