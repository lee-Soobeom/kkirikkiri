package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.dtos.AdminStoreDTO;
import com.lsb.kkirikkiri.dtos.AdminUserDTO;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<AdminStoreDTO> selectPendingBosses();

    int updateStoreStatus(@Param("email") String email, @Param("status") String status);

    int updateUserStatus(@Param("email") String email, @Param("status") String status);

    List<AdminUserDTO> selectAllUsers(@Param(value = "searchType") String searchType, @Param(value = "keyword") String keyword);

    int countAllUsers();

    int countTodayJoin();

    int updateDormantUsers();
}
