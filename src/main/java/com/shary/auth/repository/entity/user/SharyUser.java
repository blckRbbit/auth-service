package com.shary.auth.repository.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.shary2023.core.EntityModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
@Table
@Entity(name = "users")
@NoArgsConstructor
@SequenceGenerator(allocationSize = 1, name = "user_seq", sequenceName = "user_seq")
public class SharyUser implements Serializable, EntityModel, Comparable<SharyUser> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;
    private boolean owner;
    private boolean renter;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String firstName;
    private String secondName;
    private String givenName;
    @Pattern(regexp = "^(([0-9]{12})|([0-9]{10}))?$")
    private String inn;
    private LocalDate birthday;
    @Pattern(regexp = "^([0-9]{6})?$")
    private String passportNumber;
    @Pattern(regexp = "^([0-9]{2}\\s{1}[0-9]{2})?$")
    private String passportSeries;
    @Column(unique = true)
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")
    private String phoneNumber;
    private String registrationAddress;
    private String residenceAddress;
    private boolean isAddressesMatch;
    private boolean isAgreeWithPublicOffer;
    private boolean isDishonestClient;
    private boolean isValid;
    private Long telegramId;
    private Long chatId;

    @ManyToMany
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "SharyUser{" +
                "id=" + id +
                ", owner=" + owner +
                ", renter=" + renter +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", inn='" + inn + '\'' +
                ", birthday=" + birthday +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportSeries='" + passportSeries + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationAddress='" + registrationAddress + '\'' +
                ", residenceAddress='" + residenceAddress + '\'' +
                ", isAddressesMatch=" + isAddressesMatch +
                ", isAgreeWithPublicOffer=" + isAgreeWithPublicOffer +
                ", isDishonestClient=" + isDishonestClient +
                ", isValid=" + isValid +
                ", telegramId=" + telegramId +
                ", chatId=" + chatId +
                ", roles=" + roles +
                '}';
    }

    @Override
    public int compareTo(SharyUser user) {
        return this.getId().compareTo(user.getId());
    }
}

// все фото документов и лица ? (фото - url до фото в папке сервера)
//private String passportMainPhoto
//private String passportWithFacePhoto
//private String passportRegistrationPhoto

// Все что ниже нужно для страховой (как минимум)