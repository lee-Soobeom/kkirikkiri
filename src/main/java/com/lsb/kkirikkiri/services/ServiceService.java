package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ServiceEntity;
import com.lsb.kkirikkiri.mappers.ServiceMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.ServiceValidator;
import com.lsb.kkirikkiri.vos.ServiceBoardPageVo;
import com.lsb.kkirikkiri.vos.ServiceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceMapper serviceMapper;

    public CommonResult write(ServiceEntity serviceEntity) {
        if (serviceEntity == null ||
                !ServiceValidator.validateQuestion(serviceEntity) ||
                !ServiceValidator.validateAnswer(serviceEntity) ||
                !ServiceValidator.validateFilter(serviceEntity)) {
            return CommonResult.FAILURE;
        }

        // sessionUser validate
        serviceEntity.setCreatedAt(LocalDateTime.now());
        return this.serviceMapper.insert(serviceEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public int getAllCount() {
        return this.serviceMapper.getAllCount();
    }

    public int getAllCountByFilter(String filter) {
        return this.serviceMapper.getAllCountByFilter(filter);
    }

    public ServiceEntity getServiceById(int id) {
        if (id < 0) {
            return null;
        }
        return this.serviceMapper.getServiceById(id);
    }

    public ServiceVo[] getAllServices(ServiceBoardPageVo serviceBoardPageVo) {
        return this.serviceMapper.getAllServices(serviceBoardPageVo);
    }

    public ServiceVo[] getAllServicesByFilter(ServiceBoardPageVo serviceBoardPageVo, String filter) {
        if (!ServiceValidator.validateFilter(filter)) {
            return new ServiceVo[0];
        }
        return this.serviceMapper.getAllServicesByFilter(serviceBoardPageVo, filter);
    }
}
