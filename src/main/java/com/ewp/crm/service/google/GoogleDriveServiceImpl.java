package com.ewp.crm.service.google;

import com.ewp.crm.models.ContractFormData;
import com.ewp.crm.models.LastContractId;
import com.ewp.crm.repository.interfaces.GoogleTokenRepository;
import com.ewp.crm.repository.interfaces.LastContractIdRepository;
import com.ewp.crm.service.interfaces.GoogleDriveService;
import com.ibm.icu.text.Transliterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private static Logger logger = LoggerFactory.getLogger(GoogleDriveServiceImpl.class);

    private final static String templateFileUrl = "contract-template.docx";
    private final static String templatePathUrl = "src/main/resources/";
    private final static String uploadUrl = "https://www.googleapis.com/upload/drive/v3/files?";
    private final static String updateNameUrl = "https://www.googleapis.com/drive/v3/files/";
    private final static String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    private final LastContractIdRepository lastContractIdRepository;
    private final GoogleTokenRepository googleTokenRepository;

    @Autowired
    public GoogleDriveServiceImpl(LastContractIdRepository lastContractIdRepository, GoogleTokenRepository googleTokenRepository) {
        this.lastContractIdRepository = lastContractIdRepository;
        this.googleTokenRepository = googleTokenRepository;
    }

    @Override
    public Optional<String> createContractWithData(ContractFormData data) {
        Optional<File> fileOptional = getFileWithData(data);
        if (fileOptional.isPresent()) {
            try {
                File file = fileOptional.get();
                String token = googleTokenRepository.getOne(1L).getValue();

                String uri = uploadUrl + "uploadType=media&" +
                        "access_token=" + token;

                HttpPost httpPostMessages = new HttpPost(uri);
                httpPostMessages.setHeader("Content-type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                EntityBuilder builder = EntityBuilder.create();
                builder.setFile(file);
                httpPostMessages.setEntity(builder.build());
                HttpClient httpClient = getHttpClient();
                HttpResponse response = httpClient.execute(httpPostMessages);
                String res = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(res);
                String id = json.getString("id");

                HttpPatch httpPatch = new HttpPatch(updateNameUrl + id + "?access_token=" + token);
                httpPatch.setHeader("Content-type", "application/json");
                String fileName = "{ \"name\":\"" + file.getName().replaceAll("[ʹ\\s]","") + "\"}";
                httpPatch.setEntity(new StringEntity(fileName));
                httpClient.execute(httpPatch);

                HttpPost httpPost = new HttpPost(updateNameUrl + id + "/permissions?access_token=" + token);
                httpPost.setHeader("Content-type", "application/json");
                String permission = "{" +
                        "  \"role\": \"reader\"," +
                        "  \"type\": \"anyone\" " +
                        "}";
                httpPost.setEntity(new StringEntity(permission));
                httpClient.execute(httpPost);
                file.delete();
                return Optional.of(id);
            } catch (IOException | JSONException e) {
                logger.error("Error with upload file to Google Drive");
            }
        }
        return Optional.empty();
    }

    private Optional<File> getFileWithData(ContractFormData data) {
        if (!googleTokenRepository.getOne(1L).getValue().isEmpty()) {
            try {
                WordprocessingMLPackage mlp = WordprocessingMLPackage.load(new File(templatePathUrl + templateFileUrl));
                HashMap<String, String> map = new HashMap<>();
                VariablePrepare.prepare(mlp);
                Date currentDate = new Date();
                String dateStr = new SimpleDateFormat("dd MM yyyy").format(currentDate);
                LastContractId lastContractId = lastContractIdRepository.getById(1L);
                long lastId = lastContractId.getLastId();
                lastId++;
                lastContractId.setLastId(lastId);
                map.put("contract-number", String.valueOf(lastId));
                map.put("date", dateStr);
                map.put("name", data.getInputName());
                map.put("passport-series", data.getPassportData().getSeries());
                map.put("passport-number", data.getPassportData().getNumber());
                map.put("passport-issued", data.getPassportData().getIssuedBy());
                map.put("passport-date", data.getPassportData().getDateOfIssue());
                map.put("passport-registration", data.getPassportData().getIssuedBy());
                map.put("birthday", data.getInputBirthday());
                map.put("email", data.getInputEmail());
                map.put("phone-number", data.getInputPhoneNumber());
                mlp.getMainDocumentPart().variableReplace(map);
                Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
                String fileName = toLatinTrans.transliterate(data.getInputName());
                File file = new File(templatePathUrl + fileName + ".docx");
                file.createNewFile();
                Docx4J.save(mlp, file);
                lastContractIdRepository.save(lastContractId);
                return Optional.of(file);
            } catch (Exception e) {
                logger.info("Error with load file");
            }
        }
        return Optional.empty();
    }

    private HttpClient getHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }
}
