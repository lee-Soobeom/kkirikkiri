package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.dtos.ResetPasswordRequestDTO;
import com.lsb.kkirikkiri.entities.user.EmailAuthEntity;
import com.lsb.kkirikkiri.entities.user.EmailTokenEntity;
import com.lsb.kkirikkiri.entities.user.StoreEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.results.Result;
import com.lsb.kkirikkiri.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController extends AbstractGeneralController{

    private final UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/";
        }
        return "/user/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postLogin(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            HttpSession session) {

        Pair<Result, UserEntity> result = this.userService.login(email, password);
        if (result.getLeft() == CommonResult.SUCCESS) {
            session.setAttribute("sessionUser", result.getRight());

            LocalDate today = LocalDate.now();
            LocalDate birth = result.getRight().getBirth();
            int age = today.getYear() - birth.getYear();
            if (birth.plusYears(age).isAfter(today)) age--;
            session.setAttribute("isAdult", age >= 19);
        }
        Map<String, Object> response = prepareJsonResponse(result.getLeft());
        response.put("user", result.getRight());
        return response;

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogout(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser, HttpSession session) {
        if (sessionUser != null) {
            session.invalidate();
        }
        return "redirect:/user/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/";
        }

        return "user/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUser(
            @RequestParam(value = "termMarketingAgreed", required = false, defaultValue = "false") boolean termMarketingAgreed,
            @RequestParam(value = "isBoss", required = false, defaultValue = "false") boolean boss,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "salt", required = false) String salt,
            UserEntity user,
            StoreEntity store,
            @RequestParam(value = "licenseFile", required = false) MultipartFile licenseFile,
            @RequestParam(value = "reportCardFile", required = false) MultipartFile reportCardFile) {

        user.setBoss(boss);

        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setEmail(email);
        emailToken.setCode(code);
        emailToken.setSalt(salt);

        Result result;
        try {
            result = this.userService.register(user, store, emailToken, boss, termMarketingAgreed, licenseFile, reportCardFile);
        } catch (TransactionalException e) {
            result = (Result) e.result;
        }

        String resultName = ((Enum<?>) result).name();
        return String.format("{\"result\": \"%s\"}", resultName);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> deleteUser(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser, HttpSession session) {

        if (sessionUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put(Result.KEY, CommonResult.FAILURE.name());
            return response;
        }

        Result result = this.userService.deleteUser(sessionUser, sessionUser.getEmail());
        if (result == CommonResult.SUCCESS) {
            session.invalidate();
        }

        Map<String, Object> response = new HashMap<>();
        response.put(Result.KEY, result.name());
        return response;
    }

    @RequestMapping(value = "/social-register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String socialRegisterPage(HttpSession session) {
        if (session.getAttribute("sessionUser") == null) {
            return "redirect:/user/login";
        }
        return "user/social-register";
    }

    @RequestMapping( value = "/social-register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postSocialRegister(UserEntity updatedInfo, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        UserEntity currentUser = (UserEntity) session.getAttribute("sessionUser");

        if (currentUser == null) {
            response.put("result", "FAILURE");
            return response;
        }

        try {
            this.userService.registerSocialExtraInfo(currentUser, updatedInfo);
            session.removeAttribute("needsAdditionalInfo");
            session.setAttribute("sessionUser", currentUser);
            response.put("result", "SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("result", "FAILURE");
        }

        return response;
    }


    @RequestMapping(value = "/my", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getMyPage(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser, Model model) {
        if (sessionUser == null) {
            return "redirect:/user/login";
        }
        if (sessionUser.isBoss()) {
            StoreEntity store = this.userService.getStoreByEmail(sessionUser.getEmail());
            model.addAttribute("store", store);
        } else {
            model.addAttribute("store", new StoreEntity());
        }
        return "user/my";
    }

    @RequestMapping(value = "/my", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> patchMyPage(@SessionAttribute(value = "sessionUser") UserEntity sessionUser, UserEntity user, MultipartFile profileImage, HttpSession session) {
        user.setEmail(sessionUser.getEmail());
        Result result = userService.modify(user, null, profileImage, null);

        if (result == CommonResult.SUCCESS) {
            UserEntity updatedUser = userService.getUserByEmail(user.getEmail());
            session.setAttribute("sessionUser", updatedUser);
        }

        return prepareJsonResponse(result);
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postEmail(@RequestParam(value = "email", required = false) String email) throws MessagingException {
        Pair<Result, EmailTokenEntity> result = this.userService.sendEmail(email);
        Map<String, Object> response = prepareJsonResponse(result.getLeft());
        if (result.getLeft() == CommonResult.SUCCESS) {
            response.put("salt", result.getRight().getSalt());
        }
        return response;
    }

    @RequestMapping(value = "/verify-business", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getVerifyBusiness(@RequestParam String businessNumber) {
        boolean isValid = userService.verifyBusinessNumber(businessNumber);
        Map<String, Object> response = new HashMap<>();
        response.put("result", isValid ? "SUCCESS" : "FAILURE");
        return response;
    }

    @RequestMapping(value = "/session-status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getSessionStatus(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        Map<String, Object> response = new HashMap<>();
        if (sessionUser == null) {
            response.put("result", false);
        } else {
            response.put("result", true);
            response.put("email", sessionUser.getEmail());
        }
        return response;
    }

    @RequestMapping(value = "/nickname-status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getNicknameStatus(@RequestParam(value = "nickname", required = false) String nickname) {
        Result result = this.userService.checkNickname(nickname);
        return prepareJsonResponse(result);
    }

    @RequestMapping(value = "/email", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> patchEmail(EmailTokenEntity emailToken) {
        Result result = this.userService.verifyEmail(emailToken);
        Map<String, Object> response = new HashMap<>(prepareJsonResponse(result));
        if (result == CommonResult.SUCCESS) {
            response.put("salt", emailToken.getSalt());
        }
        return response;
    }

    @RequestMapping(value = "/display", method = RequestMethod.GET,
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> display(@RequestParam(value = "fileName") String fileName) {
        String savePath = "C:/kkiri/uploads/profiles/";
        File file = new File(savePath + fileName);

        try {
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            return new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String findPage() {
        return "user/find";
    }

    @RequestMapping(value = "/find-email", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> findEmail(@RequestParam(value = "contact") String contact) {
        Map<String, Object> response = new HashMap<>();

        String email = userService.findEmailByContact(contact);

        if (email != null) {
            response.put("result", "success");
            response.put("email", email);
        } else {
            response.put("result", "error");
        }

        return response;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getResetPassword(@RequestParam(value = "email") String email, @RequestParam(value = "token") String token, Model model) {

        if (!userService.checkToken(email, token)) {
            return "redirect:/?error=invalid_token";
        }

        model.addAttribute("email", email);
        model.addAttribute("token", token);

        return "/user/reset-password";
    }

    @RequestMapping(value = "/send-auth-code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> sendAuthCode(@RequestBody ResetPasswordRequestDTO dto) {
        boolean isSent = userService.sendAuthCode(dto.getEmail());

        Map<String, Object> response = new HashMap<>();

        if (isSent) {
            response.put("result", "success");
        } else {
            response.put("result", "failure");
        }

        return response;
    }

    @RequestMapping(value = "/verify-code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> verifyCode(@RequestBody ResetPasswordRequestDTO dto) {
        String token = userService.verifyCode(dto.getEmail(), dto.getAuthCode());

        Map<String, Object> response = new HashMap<>();

        if (token != null) {
            response.put("result", "success");
            response.put("token", token);
        } else {
            response.put("result", "failure");
            response.put("message", "인증번호가 일치하지 않거나 만료되었습니다.");
        }

        return response;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
        boolean isSuccess = userService.resetPassword(dto.getEmail(), dto.getToken(), dto.getPassword());

        Map<String, Object> response = new HashMap<>();
        if (isSuccess) {
            response.put("result", "success");
        } else {
            response.put("result", "failure");
            response.put("message", "인증 정보가 유효하지 않거나 만료되었습니다.");
        }

        return response;
    }

}
