package com.ewp.crm.service.impl;

import com.ewp.crm.service.interfaces.CaptchaService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

@Service
@PropertySource(value = "file:./anti-captcha.properties")
public class CaptchaServiceImpl implements CaptchaService {
    private static Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Value("${userKey}")
    private static String userKey;

    @Override
    public Optional<String> captchaImgResolver(String captchaURL) {

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Optional<String> array = getByteArrayFromImageURL(captchaURL);
        if (!array.isPresent()) {
            logger.error("Can't get byte array from image URL " + captchaURL);
            return Optional.empty();
        }
        JSONObject bodyForReport;
        try {
            bodyForReport = new JSONObject("{\n" +
                    "    \"clientKey\":\"" + userKey + "\",\n" +
                    "    \"task\":\n" +
                    "        {\n" +
                    "            \"type\":\"ImageToTextTask\",\n" +
                    "            \"body\":\"" + array.get() + "\",\n" +
                    "            \"phrase\":false,\n" +
                    "            \"case\":false,\n" +
                    "            \"numeric\":false,\n" +
                    "            \"math\":0,\n" +
                    "            \"minLength\":0,\n" +
                    "            \"maxLength\":0\n" +
                    "        }\n" +
                    "}");
        } catch (JSONException e) {
            logger.error("JSONException creating bodyForReport", e);
            return Optional.empty();
        }

        RequestBody bodyReport = RequestBody.create(JSON, bodyForReport.toString());
        Request requestReport = new Request.Builder()
                .url("https://api.anti-captcha.com/createTask")
                .post(bodyReport)
                .build();

        Response responseReport;
        try {
            responseReport = client
                    .newCall(requestReport)
                    .execute();
        } catch (IOException e) {
            logger.error("IOException during captcha responseReport request", e);
            return Optional.empty();
        }

        JsonObject convertedObject;
        try {
            convertedObject = new Gson().fromJson(responseReport.body().string(), JsonObject.class);
        } catch (IOException e) {
            logger.error("IOException during captcha json response parsing", e);
            return Optional.empty();
        }
        String taskId = convertedObject.get("taskId").getAsString();

        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        }

        Optional<String> solution = Optional.empty();
        try {
            solution = getResultCaptcha(taskId);
        } catch (NullPointerException | JSONException | IOException e) {
            logger.warn("Something bad had happen while getting captcha result, wait more...");
            try {
                Thread.sleep(40_000);
            } catch (InterruptedException e1) {
                logger.error("InterruptedException", e1);
            }
            try {
                solution = getResultCaptcha(taskId);
            } catch (JSONException | IOException e1) {
                logger.error("Unable to get captcha solution after 2 attempts, surrender...", e1);
            }
        }
        return solution;
    }

    @SuppressWarnings("deprecation")
    private static Optional<String> getByteArrayFromImageURL(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Optional.of(new String(Base64.encode(baos.toByteArray())));
        } catch (Exception e) {
            logger.error("Error converting captcha image to bytes", e);
        }
        return Optional.empty();
    }

    private static Optional<String> getResultCaptcha(String taskId) throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject bodyForResult = new JSONObject("{\n" +
                "    \"clientKey\":\"" + userKey + "\",\n" +
                "    \"taskId\": " + taskId + " \n" +
                "}");

        RequestBody bodyResult = RequestBody.create(JSON, bodyForResult.toString());
        Request requestResult = new Request
                .Builder()
                .url("https://api.anti-captcha.com/getTaskResult")
                .post(bodyResult)
                .build();

        Response responseResult = client.newCall(requestResult).execute();
        JsonObject convertedObjectResult = new Gson().fromJson(responseResult.body().string(), JsonObject.class);

        return Optional.of(convertedObjectResult.getAsJsonObject("solution").get("text").getAsString());
    }
}
