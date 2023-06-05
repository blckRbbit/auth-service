package com.shary.auth.util.mapper;

import com.github.shary2023.docs.model.*;
import com.shary.auth.repository.entity.user.SharyUser;
import com.shary.auth.util.mapper.phone.DefaultPhoneNumberMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "phoneNumber", source = "schema", qualifiedByName = "convertPhone")
    SharyUser toUser(UserResponseSchema schema);

    @Mapping(target = "phoneNumber", source = "schema", qualifiedByName = "convertPhone")
    SharyUser toUser(UserSchema schema);

    UserSchema toSchema(SharyUser sharyUser);

    UserResponseSchema toResponse(SharyUser sharyUser);

    List<UserResponseSchema> toResponse(List<SharyUser> sharyUser);

    UserResponseSchema schemaToResponse(UserSchema schema);

    @Mapping(target = "phoneNumber", source = "schema", qualifiedByName = "convertPhone")
    SharyUser toUser(GuestSchema schema);

    @Mapping(target = "phoneNumber", source = "schema", qualifiedByName = "convertPhone")
    SharyUser toUser(OwnerSchema schema);

    @Mapping(target = "phoneNumber", source = "schema", qualifiedByName = "convertPhone")
    SharyUser toUser(RenterSchema schema);

    OwnerSchema toOwner(SharyUser sharyUser);

    RenterSchema toRenter(SharyUser sharyUser);

    @Named("convertPhone")
    default String convertPhone(UserResponseSchema schema) {
        PhoneNumberMapper mapper = new DefaultPhoneNumberMapper();
        String phone = schema.getPhoneNumber();
        return  phone == null ? null : mapper.convert(phone);
    }

    @Named("convertPhone")
    default String convertPhone(UserSchema schema) {
        PhoneNumberMapper mapper = new DefaultPhoneNumberMapper();
        String phone = schema.getPhoneNumber();
        return  phone == null ? null : mapper.convert(phone);
    }

    @Named("convertPhone")
    default String convertPhone(GuestSchema schema) {
        PhoneNumberMapper mapper = new DefaultPhoneNumberMapper();
        String phone = schema.getPhoneNumber();
        return  phone == null ? null : mapper.convert(phone);
    }

    @Named("convertPhone")
    default String convertPhone(RenterSchema schema) {
        PhoneNumberMapper mapper = new DefaultPhoneNumberMapper();
        String phone = schema.getPhoneNumber();
        return  phone == null ? null : mapper.convert(phone);
    }

    @Named("convertPhone")
    default String convertPhone(OwnerSchema schema) {
        PhoneNumberMapper mapper = new DefaultPhoneNumberMapper();
        String phone = schema.getPhoneNumber();
        return  phone == null ? null : mapper.convert(phone);
    }
}

