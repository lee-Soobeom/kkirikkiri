package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.ServiceEntity;
import com.lsb.kkirikkiri.vos.ServiceBoardPageVo;
import com.lsb.kkirikkiri.vos.ServiceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServiceMapper {
    int insert(@Param(value = "service") ServiceEntity serviceEntity);

    ServiceVo[] getAllServices(@Param(value = "boardPage")ServiceBoardPageVo serviceBoardPageVo);

    ServiceVo[] getAllServicesByFilter(@Param(value = "boardPage")ServiceBoardPageVo serviceBoardPageVo, @Param(value = "filter") String filter);

    ServiceEntity getServiceById(@Param(value = "id") int id);

    int getAllCount();

    int getAllCountByFilter(@Param(value = "filter") String filter);
}
