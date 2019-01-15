package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.vkcampaigns.VkCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/vk-campaigns")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
public class VkFriendsCampaignRestController {

    private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    private final VkCampaignService vkCampaignService;
    private final ProjectPropertiesService projectPropertiesService;

    @Autowired
    public VkFriendsCampaignRestController(VkCampaignService vkCampaignService,
                                           ProjectPropertiesService projectPropertiesService) {
        this.vkCampaignService = vkCampaignService;
        this.projectPropertiesService = projectPropertiesService;
    }

    @GetMapping
    @ResponseBody
    public List<VkAddFriendsCampaign> getAllCampaigns() {
        return vkCampaignService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public VkAddFriendsCampaign getCampaign(@PathVariable Long id) {
        return vkCampaignService.get(id);
    }

    @GetMapping("/isnameexists")
    @ResponseBody
    public Boolean isCampaignNameExists(@RequestParam("name") String name) {
        Boolean result = false;

        if (vkCampaignService.getByName(name) != null) {
            logger.info("Campaign with the name {} already exists", name);
            result = true;
        }

        return result;
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteCampaign(@PathVariable Long id) {
        HttpStatus result = HttpStatus.OK;
        vkCampaignService.delete(id);
        logger.info("Campaign with id {} deleted", id);
        return result;
    }

    @PatchMapping("/{id}/start")
    @ResponseBody
    public Map<String, String > startCampaign(@PathVariable Long id) {
        vkCampaignService.setActive(id, true);

        logger.info("Campaign with id {} been started", id);
        return Collections.singletonMap("response", "started");
    }

    @PatchMapping("/{id}/stop")
    @ResponseBody
    public Map<String, String > stopCampaign(@PathVariable Long id) {
        vkCampaignService.setActive(id, false);

        logger.info("Campaign with id {} been stopped", id);
        return Collections.singletonMap("response", "stopped");
    }

}
