package com.ewp.crm.service.impl;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.configs.inteface.ContractConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.ClientsContractLinkRepository;
import com.ewp.crm.service.interfaces.ContractService;
import com.ewp.crm.service.interfaces.GoogleTokenService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.util.converters.DocxRemoveBookMark;
import com.ibm.icu.text.Transliterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {

    private static Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);

    private final static String GOOGLE_DOC_MIME_TYPE = "application/vnd.google-apps.document";
    private final static String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final static String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";
    private final String uploadUri;
    private final String updateUri;
    private final String folderId;
    private final String docsUri;
    private final String uploadUriOld;
    private final String viewUri;
    private final String deleteUri;

    private final ProjectPropertiesService projectPropertiesService;
    private final GoogleTokenService googleTokenService;
    private final ContractConfig contractConfig;
    private final ClientsContractLinkRepository clientsContractLinkRepository;

    @Autowired
    public ContractServiceImpl(ProjectPropertiesService projectPropertiesService,
                               GoogleTokenService googleTokenService,
                               ContractConfig contractConfig,
                               GoogleAPIConfigImpl googleAPIConfig,
                               ClientsContractLinkRepository clientsContractLinkRepository) {
        this.projectPropertiesService = projectPropertiesService;
        this.googleTokenService = googleTokenService;
        this.contractConfig = contractConfig;
        this.uploadUri = googleAPIConfig.getDriveUploadUri();
        this.updateUri = googleAPIConfig.getDriveUpdateUri();
        this.folderId = googleAPIConfig.getFolderId();
        this.docsUri = googleAPIConfig.getDocsUri();
        this.viewUri = googleAPIConfig.getViewUri();
        this.uploadUriOld = googleAPIConfig.getDriveUploadUriOld();
        this.clientsContractLinkRepository = clientsContractLinkRepository;
        this.deleteUri = googleAPIConfig.getDeleteFile();
    }

    @Override
    public boolean updateContractLink(ContractLinkData contractLinkData) {
        if (googleTokenService.getRefreshedToken(GoogleToken.TokenType.DRIVE).isPresent()) {
            String token = googleTokenService.getRefreshedToken(GoogleToken.TokenType.DRIVE).get().getAccessToken();
            String url = updateUri +
                    "?q='" + folderId + "'+in+parents" +
                    "&access_token=" + token;
            try {
                String searchFileName = contractLinkData.getContractName();
                String oldLink = contractLinkData.getContractLink();
                if (oldLink.contains(viewUri)) {
                    return false;
                }
                HttpGet httpGet = new HttpGet(url);
                HttpClient httpClient = getHttpClient();
                HttpResponse response = httpClient.execute(httpGet);
                String res = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(res);
                JSONArray array = json.getJSONArray("files");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String docId = obj.getString("id");
                    String mimeType = obj.getString("mimeType");
                    if (obj.getString("name").equals(searchFileName) && !oldLink.contains(docId) && mimeType.equals(GOOGLE_DOC_MIME_TYPE)) {
                        contractLinkData.setContractLink(docsUri + docId + "/edit?usp=sharing");
                        uploadFileAccessOnGoogleDrive(docId, token, httpClient);
                        clientsContractLinkRepository.saveAndFlush(contractLinkData);
                        return true;
                    }
                }
            } catch (IOException e) {
                logger.error("Error upload updating contract link request", e);
            } catch (JSONException e) {
                logger.error("Error parsing json", e);
            }
        }
        logger.info("not found link to update");
        return false;
    }

    @Override
    public void deleteContractFromGoogleDrive(String linkFromContractLinkData) {
        String idFileInGoogleDrive = getFilenameFromGoogleDrive(linkFromContractLinkData);
        if (googleTokenService.getRefreshedToken(GoogleToken.TokenType.DRIVE).isPresent()) {
            String token = googleTokenService.getRefreshedToken(GoogleToken.TokenType.DRIVE).get().getAccessToken();
            String delUri = deleteUri + idFileInGoogleDrive + "?access_token=" + token;
            HttpClient httpClient = getHttpClient();
            HttpDelete httpDelete = new HttpDelete(delUri);
            try {
                httpClient.execute(httpDelete);
            } catch (IOException e) {
                logger.info("Can not execute request to delete contract " + e);
            }
        }
    }

    @Override
    public Map<String,String> getContractIdByFormDataWithSetting(ContractDataForm data, ContractSetting setting) {
        Optional<File> fileOptional = createFileWithDataAndSetting(data, setting);
        if (fileOptional.isPresent()) {
            Optional<GoogleToken> googleTokenOptional = googleTokenService.getRefreshedToken(GoogleToken.TokenType.DRIVE);
            if (googleTokenOptional.isPresent()) {
                File file = fileOptional.get();
                String token = googleTokenOptional.get().getAccessToken();
                HttpClient httpClient = getHttpClient();
                Map<String,String> contractDataMap = new HashMap<>();

                String id = uploadFileAndGetFileId(file, token, httpClient, setting.isStamp());
                if (!id.isEmpty()) {
                    String fileName = file.getName().replaceAll("\\.docx","") + projectPropertiesService.getOrCreate().getContractLastId();
                    updateFileNameAndFolderOnGoogleDrive(id, fileName, token, httpClient);
                    uploadFileAccessOnGoogleDrive(id, token, httpClient);
                    contractDataMap.put("contractName", fileName);
                    contractDataMap.put("contractId", id);
                }
                if (file.delete()) {
                    logger.info("File deleting " + file.getName());
                }
                return contractDataMap;
            } else {
                if (fileOptional.get().delete()) {
                    logger.info("Google Token not relevant. File deleting " + fileOptional.get().getName());
                }
            }
        }
        return new HashMap<>();
    }

    private String uploadFileAndGetFileId(File file, String token, HttpClient httpClient, boolean isStamp) {
        try {
            String uri;
            if (isStamp) {
                uri = uploadUriOld +
                        "?uploadType=media&" +
                        "convert=true&" +
                        "access_token=" + token;
            } else {
                uri = uploadUri +
                        "?uploadType=media&" +
                        "access_token=" + token;
            }
            HttpPost httpPostMessages = new HttpPost(uri);
            httpPostMessages.setHeader("Content-type", DOCX_MIME_TYPE);
            EntityBuilder builder = EntityBuilder.create();
            builder.setFile(file);
            httpPostMessages.setEntity(builder.build());
            HttpResponse response = httpClient.execute(httpPostMessages);
            String res = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(res);
            return json.getString("id");
        } catch (IOException e) {
            logger.error("Error load file", e);
        } catch (JSONException e) {
            logger.error("Error parsing json", e);
        }
        return StringUtils.EMPTY;
    }

    private void updateFileNameAndFolderOnGoogleDrive(String id, String fileName, String token, HttpClient httpClient) {
        try {
            HttpPatch httpPatch = new HttpPatch(updateUri + "/" + id + "?access_token=" + token + "&addParents=" + folderId);
            httpPatch.setHeader("Content-type", "application/json");
            String jsonUpdateName = "{ \"name\":\"" + fileName + "\"}";
            httpPatch.setEntity(new StringEntity(jsonUpdateName));
            httpClient.execute(httpPatch);
        } catch (IOException e) {
            logger.error("Error with upload jsonEntity", e);
        }
    }

    private void uploadFileAccessOnGoogleDrive(String id, String token, HttpClient httpClient) {
        try {
            HttpPost httpPost = new HttpPost(updateUri + "/" + id + "/permissions?access_token=" + token);
            httpPost.setHeader("Content-type", "application/json");
            String permission = "{" +
                    "  \"role\": \"reader\"," +
                    "  \"type\": \"anyone\" " +
                    "}";
            httpPost.setEntity(new StringEntity(permission));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            logger.error("Error with upload jsonEntity", e);
        }
    }

    private Optional<File> createFileWithDataAndSetting(ContractDataForm data, ContractSetting setting) {
        try {
            String templatePath = contractConfig.getFilePath();
            String templateName;
            if (setting.isStamp()) {
                templateName = contractConfig.getFileNameWithStamp();
            } else {
                templateName = contractConfig.getFileName();
            }
            WordprocessingMLPackage mlp = WordprocessingMLPackage.load(new File(templatePath + templateName));
            HashMap<String, String> map = new HashMap<>();
            ProjectProperties projectProperties = projectPropertiesService.get();
            Long lastId = projectProperties.getContractLastId();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            map.put("contractNumber", String.valueOf(++lastId));
            map.put("date", LocalDate.now().format(formatter));
            map.put("name", data.getInputLastName() + " " + data.getInputFirstName() + " " + data.getInputMiddleName());
            map.put("passportSeries", data.getPassportData().getSeries());
            map.put("passportNumber", data.getPassportData().getNumber());
            map.put("passportIssued", data.getPassportData().getIssuedBy());
            map.put("passportDate", data.getPassportData().getDateOfIssue().format(formatter));
            map.put("passportRegistration", data.getPassportData().getRegistration());
            map.put("birthday", data.getInputBirthday().format(formatter));
            map.put("email", data.getInputEmail());
            map.put("phoneNumber", data.getInputPhoneNumber());
            //Месячная опл
            if (setting.isMonthPayment()) {
                map.put("period",contractConfig.getMonthPointThreeTwoPeriod());
                map.put("point3.3", contractConfig.getMonthPointThreeThree());
                map.put("point3.4", contractConfig.getMonthPointThreeFour());
                map.put("point4.2", contractConfig.getMonthPointFourTwo());
                map.put("point4.3", contractConfig.getMonthPointFourThree());
            } else if (setting.isOneTimePayment()){
                map.put("period",contractConfig.getOnetimePointThreeTwoPeriod());
                map.put("point3.3", " ");
                map.put("point3.4", contractConfig.getOnetimePointThreeFour());
                map.put("point4.2", " ");
                map.put("point4.3", contractConfig.getOnetimePointFourThree());
            }
            if (setting.isDiploma()) {
                map.put("diploma", contractConfig.getDiploma());
            } else {
                map.put("diploma", "");
            }
            map.put("inn", projectProperties.getInn());
            map.put("checkingAccount", projectProperties.getCheckingAccount());
            map.put("correspondentAccount", projectProperties.getCorrespondentAccount());
            map.put("bankIdentificationCode", projectProperties.getBankIdentificationCode());
            map.put("summa", setting.getPaymentAmount());
            mlp.getMainDocumentPart().variableReplace(map);
            Document document = mlp.getMainDocumentPart().getJaxbElement();
            Body body = document.getBody();
            DocxRemoveBookMark.fixRange(body.getContent(), "CTBookmark", "CTMarkupRange");
            File file = new File(templatePath + renameFileToLatin(data) + contractConfig.getFormat());
            if (file.createNewFile()) {
                logger.info("Creating file " + file.getName());
            }
            Docx4J.save(mlp, file);
            projectProperties.setContractLastId(lastId);
            projectPropertiesService.update(projectProperties);
            return Optional.of(file);
        } catch (Exception e) {
            logger.info("Error with create file", e);
        }
        return Optional.empty();
    }

    private String renameFileToLatin(ContractDataForm data) {
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String fileName = toLatinTrans.transliterate(data.getInputLastName() + data.getInputFirstName());
        return fileName.replaceAll("ʹ", "");
    }

    private String getFilenameFromGoogleDrive(String link){
        int idStart = link.indexOf("d/");
        int idFinish = link.indexOf("/e");
        return link.substring(idStart + 2,idFinish);
    }

    private HttpClient getHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }
}
