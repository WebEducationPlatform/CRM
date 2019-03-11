package com.ewp.crm.ads;

import static com.google.api.ads.common.lib.utils.Builder.DEFAULT_CONFIGURATION_FILENAME;

import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.v201806.cm.CampaignPage;
import com.google.api.ads.adwords.axis.v201806.cm.CampaignServiceInterface;
import com.google.api.ads.adwords.axis.v201806.cm.Selector;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.client.auth.oauth2.Credential;



public class GetCamp {
    // Contains the data classes and service classes.


    public static void main(String[] args) throws Exception {
        /**
         * Create an AdWordsSession instance, loading credentials from the
         * properties file:
         */

        // Get an OAuth2 credential.
        Credential credential = new OfflineCredentials.Builder()
                .forApi(Api.ADWORDS)
                .fromFile()
                .build()
                .generateCredential();

        // Construct an AdWordsSession.
        AdWordsSession session = new AdWordsSession.Builder()
                .fromFile()
                .withOAuth2Credential(credential)
                .build();

        /**
         * Alternatively, you can specify your credentials in the constructor:
         */

        // Get an OAuth2 credential.




        /**
         * Instantiate the desired service class using the AdWordsServices utility and
         * a Class object representing your service.
         */

        // Get the CampaignService.
        CampaignServiceInterface campaignService =
                new AdWordsServices().get(session, CampaignServiceInterface.class);

        /**
         * Create data objects and invoke methods on the service class instance. The
         * data objects and methods map directly to the data objects and requests for
         * the corresponding web service.
         */

        // Create selector.
        Selector selector = new Selector();
        selector.setFields(new String[] {"Id", "Name"});

        // Get all campaigns.
        CampaignPage page = campaignService.get(selector);
        System.out.println(page.getEntries().toString());
    }
}
