package com.ewp.crm.service.google;

import com.ewp.crm.service.interfaces.GoogleAdsService;
import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201809.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201809.billing.BudgetOrder;
import com.google.api.ads.adwords.axis.v201809.billing.BudgetOrderServiceInterface;
import com.google.api.ads.adwords.axis.v201809.cm.Selector;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.factory.AdWordsServicesInterface;
import com.google.api.ads.adwords.lib.jaxb.v201809.DownloadFormat;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinition;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinitionDateRangeType;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinitionReportType;
import com.google.api.ads.adwords.lib.selectorfields.v201809.cm.BudgetOrderField;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.v201809.ReportDownloaderInterface;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.client.auth.oauth2.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GoogleAdsServiceImpl implements GoogleAdsService {

    private static Logger logger = LoggerFactory.getLogger(GoogleAdsServiceImpl.class);
    public static final String ERROR_MESSAGE = "Ошибка";
    private static final double VAT_VALUE = 1.2;
    private static final double MONEY_TO_ROUBLES_RATIO = 1_000_000;

    private AdWordsSession session;
    private final AdWordsServicesInterface adWordsServices;

    public GoogleAdsServiceImpl() {
        initSession();
        adWordsServices = AdWordsServices.getInstance();
    }

    private void initSession() {
        session = null;
        try {
            Credential oAuth2Credential = new OfflineCredentials.Builder()
                    .forApi(OfflineCredentials.Api.ADWORDS)
                    .fromFile("./ads.properties")
                    .build()
                    .generateCredential();
            session = new AdWordsSession.Builder()
                    .fromFile("./ads.properties")
                    .withOAuth2Credential(oAuth2Credential)
                    .build();
        } catch (OAuthException | ValidationException e) {
            logger.error("Google ads API OAuth error!", e);
        } catch (ConfigurationLoadException e) {
            logger.error("Google ads configuration load error!", e);
        }
    }

    @Override
    public String getAccountBalance() throws Exception {
        initSession();
        if (session != null) {
            // Получаем поле "SpendingLimit" из "BudgetOrderService.Money"
            // Значение - общий бюджет кампании за все время
            long bosTotal = 0L;
            BudgetOrderServiceInterface bos = adWordsServices.get(session, BudgetOrderServiceInterface.class);
            Selector selector = new SelectorBuilder()
                    .fields(BudgetOrderField.SpendingLimit)
                    .build();
            for (BudgetOrder order : bos.get(selector)) {
                bosTotal += order.getSpendingLimit().getMicroAmount();
            }
            // Получаем поле "Cost" из отчета "Account performance report"
            // Значение - общая сумма потреченных средств за все время
            com.google.api.ads.adwords.lib.jaxb.v201809.Selector selectorReport =
                    new com.google.api.ads.adwords.lib.jaxb.v201809.Selector();
            selectorReport.getFields().addAll(Arrays.asList("Cost"));
            ReportDefinition reportDefinition = new ReportDefinition();
            reportDefinition.setReportName("Account performance report #" + System.currentTimeMillis());
            reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.ALL_TIME);
            reportDefinition.setReportType(ReportDefinitionReportType.ACCOUNT_PERFORMANCE_REPORT);
            reportDefinition.setDownloadFormat(DownloadFormat.CSV);
            ReportingConfiguration reportingConfiguration =
                    new ReportingConfiguration.Builder()
                            .skipReportHeader(true)
                            .skipColumnHeader(true)
                            .skipReportSummary(true)
                            .includeZeroImpressions(true)
                            .build();
            session.setReportingConfiguration(reportingConfiguration);
            reportDefinition.setSelector(selectorReport);
            ReportDownloaderInterface reportDownloader =
                    adWordsServices.getUtility(session, ReportDownloaderInterface.class);
            ReportDownloadResponse response = reportDownloader.downloadReport(reportDefinition);
            String s = response.getAsString();
            long reportCost = Long.parseLong(s.split("\n")[0]);
            // Вычитаем Cost из SpendingLimit - получаем текущий баланс аккаунта без НДС
            double accountBalance = ((bosTotal - reportCost) / MONEY_TO_ROUBLES_RATIO) * VAT_VALUE;
            return String.format("%.2f", accountBalance);
        }
        return ERROR_MESSAGE;
    }

    @Override
    public String getYesterdaySpentAmount() throws Exception {
        initSession();
        if (session != null) {
            // Получаем поле "Cost" из отчета "Account performance report"
            // Значение - общая сумма потреченных средств за вчера
            com.google.api.ads.adwords.lib.jaxb.v201809.Selector selectorReport = new com.google.api.ads.adwords.lib.jaxb.v201809.Selector();
            selectorReport.getFields().addAll(Arrays.asList("Cost"));
            ReportDefinition reportDefinition = new ReportDefinition();
            reportDefinition.setReportName("Account performance report #" + System.currentTimeMillis());
            reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.YESTERDAY);
            reportDefinition.setReportType(ReportDefinitionReportType.ACCOUNT_PERFORMANCE_REPORT);
            reportDefinition.setDownloadFormat(DownloadFormat.CSV);
            ReportingConfiguration reportingConfiguration =
                    new ReportingConfiguration.Builder()
                            .skipReportHeader(true)
                            .skipColumnHeader(true)
                            .skipReportSummary(true)
                            .includeZeroImpressions(true)
                            .build();
            session.setReportingConfiguration(reportingConfiguration);
            reportDefinition.setSelector(selectorReport);
            ReportDownloaderInterface reportDownloader =
                    adWordsServices.getUtility(session, ReportDownloaderInterface.class);
            ReportDownloadResponse response = reportDownloader.downloadReport(reportDefinition);
            String s = response.getAsString();
            long reportCost = Long.parseLong(s.split("\n")[0]);
            return String.format("%.2f", (reportCost / MONEY_TO_ROUBLES_RATIO));
        }
        return ERROR_MESSAGE;
    }
}
