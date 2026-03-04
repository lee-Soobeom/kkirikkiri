package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.PaymentEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.results.PaymentResult;
import com.lsb.kkirikkiri.results.Result;
import com.lsb.kkirikkiri.services.PaymentService;
import com.lsb.kkirikkiri.vos.PaymentVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String API_SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R";
    private final Map<String, String> billingKeyMap = new HashMap<>();

    @RequestMapping(value = "/confirm/payment")
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
        String secretKey = request.getRequestURI().contains("/confirm/payment") ? API_SECRET_KEY : WIDGET_SECRET_KEY;
        JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");
        int statusCode = response.containsKey("error") ? 400 : 200;
        return ResponseEntity.status(statusCode).body(response);
    }

    @RequestMapping(value = "/confirm-billing")
    public ResponseEntity<JSONObject> confirmBilling(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        String billingKey = billingKeyMap.get(requestData.get("customerKey"));
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, "https://api.tosspayments.com/v1/billing/" + billingKey);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/issue-billing-key")
    public ResponseEntity<JSONObject> issueBillingKey(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, "https://api.tosspayments.com/v1/billing/authorizations/issue");

        if (!response.containsKey("error")) {
            billingKeyMap.put((String) requestData.get("customerKey"), (String) response.get("billingKey"));
        }

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/callback-auth", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> callbackAuth(@RequestParam String customerKey, @RequestParam String code) throws Exception {
        JSONObject requestData = new JSONObject();
        requestData.put("grantType", "AuthorizationCode");
        requestData.put("customerKey", customerKey);
        requestData.put("code", code);

        String url = "https://api.tosspayments.com/v1/brandpay/authorizations/access-token";
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);

        logger.info("Response Data: {}", response);

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/confirm/brandpay", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<JSONObject> confirmBrandpay(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        String url = "https://api.tosspayments.com/v1/brandpay/payments/confirm";
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            logger.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    private JSONObject sendRequest(@NonNull JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            logger.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    @RequestMapping(value = "/charge", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView index(ModelAndView modelAndView,
                              @SessionAttribute(value = "sessionUser") UserEntity sessionUser,
                              @RequestParam(value = "amount", defaultValue = "0") int amount) {
        modelAndView.setViewName("/tosspayments/widget/checkout");
        if (this.paymentService.checkUser(sessionUser)) {
            modelAndView.addObject("sessionUser", sessionUser);
        }
        modelAndView.addObject("amount", amount);
        return modelAndView;
    }

    @RequestMapping(value = "/charge/success", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getSuccess(ModelAndView modelAndView,
                                   @SessionAttribute(value = "sessionUser") UserEntity sessionUser,
                                   PaymentEntity paymentEntity) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("paymentKey",  paymentEntity.getPaymentKey());
        jsonObject.put("orderId", paymentEntity.getOrderId());
        jsonObject.put("amount", paymentEntity.getAmount());
        JSONObject response = sendRequest(jsonObject, WIDGET_SECRET_KEY, "https://api.tosspayments.com/v1/payments/confirm");

        // 결제 실패
        if (response.containsKey("error")) {
            modelAndView.setViewName("/tosspayments/fail");
            modelAndView.addObject("response", response);
            return modelAndView;
        }

        //결제 성공 > record update
        Pair<Result, PaymentVo> result = this.paymentService.updateCharge(sessionUser, paymentEntity);
        if (result.getLeft().equals(PaymentResult.FAILURE_CANCEL)) {
            modelAndView.setViewName("/tosspayments/fail");
            modelAndView.addObject("response", response);
            modelAndView.addObject("cancel", result.getRight());
            modelAndView.addObject("result", result.getLeft().name());
            return modelAndView;
        }

        modelAndView.setViewName("/tosspayments/widget/success");
        modelAndView.addObject("payment", paymentEntity);
        modelAndView.addObject("response", response);
        modelAndView.addObject("result", result.getLeft().name());
        return modelAndView;
    }

    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "/tosspayments/fail";
    }

    @RequestMapping(value = "/payment/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postPayment(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                                           PaymentEntity paymentEntity) {
        Map<String, Object> response = new HashMap<>();
        Pair<CommonResult, String> result = this.paymentService.createPayment(sessionUser, paymentEntity);
        response.put("result", result.getLeft().name());
        if (result.getLeft().equals(CommonResult.SUCCESS)) {
            response.put("orderId", result.getRight());
        }
        return response;
    }
}
