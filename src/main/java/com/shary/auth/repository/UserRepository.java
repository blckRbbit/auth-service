package com.shary.auth.repository;

import com.github.shary2023.docs.model.OwnerSchema;
import com.github.shary2023.docs.model.RenterSchema;
import com.shary.auth.repository.entity.user.SharyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SharyUser, Long> {
    Optional<SharyUser> findByEmail(String email);
    Optional<SharyUser> findByPhoneNumber(String phone);

    @Query(value =
            "UPDATE User u set u.id = :model.id, u.firstName = :model.firstName, u.secondName = :model.secondName, " +
                    "u.givenName = :model.givenName, u.isValid = :model.isValid, u.telegramId = :model.telegramId, " +
                    "u.password = :model.password" +
                    "u.renter = :model.renter, u.owner = :model.owner" +
                    "u.chatId = :model.chatId where u.id = :model.id",
            nativeQuery = true)
    Optional<SharyUser> update(@Param("model") RenterSchema model);

    @Query(value =
            "UPDATE User u set u.id = :model.id, u.firstName = :model.firstName, " +
                    "u.secondName = :model.secondName, u.givenName = :model.givenName, " +
                    "u.inn = :model.inn, u.birthday = :model.birthday, " +
                    "u.password = :model.password" +
                    "u.passportNumber = :model.passportNumber, " +
                    "u.passportSeries = :model.passportSeries, " +
                    "u.phoneNumber = :model.phoneNumber, " +
                    "u.registrationAddress = :model.registrationAddress, " +
                    "u.residenceAddress = :model.residenceAddress, " +
                    "u.isValid = :model.isValid, " +
                    "u.renter = :model.renter" +
                    "u.owner = :model.owner" +
                    "u.telegramId = :model.telegramId, u.chatId = :model.chatId where u.id = :model.id",
            nativeQuery = true)
    Optional<SharyUser> update(@Param("model") OwnerSchema model);

}
