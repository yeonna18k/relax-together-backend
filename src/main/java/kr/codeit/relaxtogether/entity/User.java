package kr.codeit.relaxtogether.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String name;
    private String companyName;
    private String profileImage;
    private boolean isDeleted;

    @Builder
    public User(String email, String password, String name, String companyName, String profileImage) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.companyName = companyName;
        this.profileImage = profileImage;
    }

    public void update(String companyName, String profileImage) {
        this.companyName = companyName;
        this.profileImage = profileImage;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
