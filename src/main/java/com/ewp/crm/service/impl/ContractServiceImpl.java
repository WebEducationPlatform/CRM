package com.ewp.crm.service.google;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.configs.inteface.ContractConfig;
import com.ewp.crm.models.ContractDataForm;
import com.ewp.crm.models.ContractSetting;
import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.repository.interfaces.GoogleTokenRepository;
import com.ewp.crm.service.interfaces.ContractService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
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
public class ContractServiceImpl implements ContractService {

    private static Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);

    private final static String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    private final ProjectPropertiesService projectPropertiesService;
    private final GoogleTokenRepository googleTokenRepository;
    private final ContractConfig contractConfig;
    private final GoogleAPIConfigImpl googleAPIConfig;

    @Autowired
    public ContractServiceImpl(ProjectPropertiesService projectPropertiesService, GoogleTokenRepository googleTokenRepository, ContractConfig contractConfig, GoogleAPIConfigImpl googleAPIConfig) {
        this.projectPropertiesService = projectPropertiesService;
        this.googleTokenRepository = googleTokenRepository;
        this.contractConfig = contractConfig;
        this.googleAPIConfig = googleAPIConfig;
    }

    @Override
    public Optional<String> getContractIdByFormDataWithSetting(ContractDataForm data, ContractSetting setting) {
        Optional<File> fileOptional = getFileWithDataAndSetting(data, setting);
        if (fileOptional.isPresent()) {
            try {
                File file = fileOptional.get();
                String token = googleTokenRepository.getOne(1L).getValue();

                String uri = googleAPIConfig.getDriveUploadUri() +
                        "?uploadType=media&" +
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

                String updateNameUri = googleAPIConfig.getDriveUpdateUri();

                HttpPatch httpPatch = new HttpPatch(updateNameUri + id + "?access_token=" + token);
                httpPatch.setHeader("Content-type", "application/json");
                String fileName = "{ \"name\":\"" + file.getName().replaceAll("ʹ", "") + "\"}";
                httpPatch.setEntity(new StringEntity(fileName));
                httpClient.execute(httpPatch);

                HttpPost httpPost = new HttpPost(updateNameUri + id + "/permissions?access_token=" + token);
                httpPost.setHeader("Content-type", "application/json");
                String permission =
                        "{" +
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

    private Optional<File> getFileWithDataAndSetting(ContractDataForm data, ContractSetting setting) {
        if (!googleTokenRepository.getOne(1L).getValue().isEmpty()) {
            try {
                String templatePath = contractConfig.getFilePath();
                String templateName = contractConfig.getFileName();
                WordprocessingMLPackage mlp = WordprocessingMLPackage.load(new File(templatePath + templateName));
                HashMap<String, String> map = new HashMap<>();
                VariablePrepare.prepare(mlp);
                Date currentDate = new Date();
                String dateStr = new SimpleDateFormat("dd MM yyyy").format(currentDate);
                ProjectProperties projectProperties = projectPropertiesService.get();
                long lastId = projectProperties.getContractLastId();
                lastId++;
                map.put("contract-number", String.valueOf(lastId));
                map.put("date", dateStr);
                map.put("name", data.getInputLastName() + " " + data.getInputFirstName() + " " + data.getInputMiddleName());
                map.put("passport-series", data.getPassportData().getSeries());
                map.put("passport-number", data.getPassportData().getNumber());
                map.put("passport-issued", data.getPassportData().getIssuedBy());
                map.put("passport-date", data.getPassportData().getDateOfIssue());
                map.put("passport-registration", data.getPassportData().getIssuedBy());
                map.put("birthday", data.getInputBirthday());
                map.put("email", data.getInputEmail());
                map.put("phone-number", data.getInputPhoneNumber());
                if (!setting.isOneTimePayment()) {
                    map.put("onetime", contractConfig.getMonth());
                    map.put("point", contractConfig.getMonthPoint());
                } else {
                    map.put("onetime", "");
                    map.put("point", contractConfig.getOnetimePoint());
                }
                if (setting.isDiploma()) {
                    map.put("diploma", contractConfig.getDiploma());
                } else {
                    map.put("diploma", "");
                }
                map.put("summa", setting.getPaymentAmount());
                mlp.getMainDocumentPart().variableReplace(map);
                String fileName = renameFileToLatin(data);
                File file = new File(templatePath + fileName + ".docx");
                file.createNewFile();
                Docx4J.save(mlp, file);
                projectProperties.setContractLastId(lastId);
                projectPropertiesService.update(projectProperties);
                return Optional.of(file);
            } catch (Exception e) {
                logger.info("Error with create file");
            }
        }
        return Optional.empty();
    }

    private String renameFileToLatin(ContractDataForm data) {
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        return toLatinTrans.transliterate(data.getInputLastName() + data.getInputFirstName());
    }

    private HttpClient getHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }
}
