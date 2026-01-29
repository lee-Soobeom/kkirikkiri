package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.ServiceEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value = "/service")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getService(@RequestParam(value = "id", required = false, defaultValue = "0") int id,
                                   ModelAndView modelAndView) {
        modelAndView.addObject("service", this.serviceService.getServiceById(id));
        modelAndView.setViewName("service/service");
        return modelAndView;
    }

    @RequestMapping(value = "/write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public String write() {
        return "service/write";
    }

    @RequestMapping(value = "/write",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> post(ServiceEntity serviceEntity) {
        Map<String, Object> response = new HashMap<>();
        CommonResult result = this.serviceService.write(serviceEntity);
        response.put("result", result.name());
        return response;
    }

}
