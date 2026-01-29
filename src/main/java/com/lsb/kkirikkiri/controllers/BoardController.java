package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.ServiceEntity;
import com.lsb.kkirikkiri.services.ServiceService;
import com.lsb.kkirikkiri.vos.ServiceBoardPageVo;
import com.lsb.kkirikkiri.vos.ServiceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
@RequestMapping(value = "/board")
@RequiredArgsConstructor
public class BoardController {
    private final ServiceService serviceService;

    @RequestMapping(value = "/list",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getBoard(ModelAndView modelAndView) {
        modelAndView.setViewName("board/list");
        return modelAndView;
    }

    @RequestMapping(value = "/service",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getService(@RequestParam(value = "page", required = false, defaultValue = "1") int requestPage,
                                   @RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
                                   ModelAndView modelAndView) {
        int totalCount = filter.equals("all")
                ? this.serviceService.getAllCount()
                : this.serviceService.getAllCountByFilter(filter);
        ServiceBoardPageVo serviceBoardPageVo = new ServiceBoardPageVo(totalCount, requestPage);
        ServiceVo[] services = filter.equals("all")
                ? this.serviceService.getAllServices(serviceBoardPageVo)
                : this.serviceService.getAllServicesByFilter(serviceBoardPageVo, filter);
        modelAndView.addObject("filter", filter);
        modelAndView.addObject("services", services);
        modelAndView.addObject("boardPageVo", serviceBoardPageVo);
        modelAndView.setViewName("board/service");
        return modelAndView;
    }
}
