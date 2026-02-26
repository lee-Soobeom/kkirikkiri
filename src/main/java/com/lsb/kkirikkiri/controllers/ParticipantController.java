package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postAdd(@SessionAttribute(value = "sessionUser") UserEntity sessionUser,
                                       @RequestParam(value = "articleId", defaultValue = "0") int articleId,
                                       @RequestParam(value = "participant", required = false, defaultValue = "") String participant,
                                       @RequestParam(value = "participantNickname", required = false, defaultValue = "") String participantNickname,
                                       @RequestParam(value = "isEntryChecked", required = false, defaultValue = "true") boolean isEntryChecked) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(isEntryChecked);
        CommonResult result = isEntryChecked
                ? this.participantService.modifyParticipants(sessionUser, articleId, participant, participantNickname)
                : this.participantService.modifyParticipants(sessionUser, articleId, sessionUser.getEmail(), sessionUser.getNickname());
        response.put("result", result.name());
        return response;
    }
}
