package kr.codeit.relaxtogether.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    private String location;
    private String type;
    private String name;
    private LocalDateTime dateTime;
    private int capacity;
    private String image;
    private LocalDateTime registrationEnd;
    private boolean isDeleted;

    @Builder
    public Gathering(String location, String type, String name, LocalDateTime dateTime, int capacity, String image,
        LocalDateTime registrationEnd) {
        this.location = location;
        this.type = type;
        this.name = name;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.image = image;
        this.registrationEnd = registrationEnd;
    }
}
