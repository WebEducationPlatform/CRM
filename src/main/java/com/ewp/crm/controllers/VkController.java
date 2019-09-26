package com.ewp.crm.controllers;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.models.User;
import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.models.vkcampaigns.VkUser;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.vkcampaigns.VkCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER', 'HR')")
public class VkController {
    private static Logger logger = LoggerFactory.getLogger(VkController.class);

    private final UserService userService;
    private final VKService vkService;
    private final ProjectPropertiesService projectPropertiesService;
    private final VkCampaignService vkCampaignService;
    private final VKConfig vkConfig;

    private ProjectProperties projectProperties;

    @Autowired
    public VkController(VKService vkService,
                        UserService userService,
                        ProjectPropertiesService projectPropertiesService,
                        VkCampaignService vkCampaignService,
                        VKConfig vkConfig) {
        this.vkService = vkService;
        this.userService = userService;
        this.projectPropertiesService = projectPropertiesService;
        this.vkCampaignService = vkCampaignService;
        this.vkConfig = vkConfig;
    }

    @GetMapping(value = "/vk-auth")
    public String vkAuthPage() {
        String uri = vkService.receivingTokenUri();
        return "redirect:" + uri;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'HR')")
    @PostMapping(value = "/vk-auth")
    public String vkGetAccessToken(@RequestParam("token") String token, @AuthenticationPrincipal User userFromSession) {
        String applicationToken = vkService.replaceApplicationTokenFromUri(token);
        projectProperties = projectPropertiesService.getOrCreate();
        projectProperties.setTechnicalAccountToken(applicationToken);
        projectPropertiesService.update(projectProperties);
        userFromSession.setVkToken(applicationToken);
        userService.update(userFromSession);
        return "redirect:/client";
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    @GetMapping(value = "/vk/campaigns/all")
    public ModelAndView showVkCampaignsPage(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("all-vk-friends-campaigns");
        modelAndView.addObject("campaignsList", vkCampaignService.getAll());
        modelAndView.addObject("stats", vkCampaignService.getRequestsStats());
        modelAndView.addObject("user", userFromSession);
        return modelAndView;
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    @GetMapping(value = "/vk/campaigns/create")
    public ModelAndView createNewVkCampaignPage(@AuthenticationPrincipal User userFromSession,
                                                @RequestParam("name") String name,
                                                @RequestParam("appid") String appid,
                                                @RequestParam("text") String text,
                                                @RequestParam("duplicates") Boolean duplicates) {
        ModelAndView modelAndView = new ModelAndView("vk-campaign-create");
        modelAndView.addObject("user", userFromSession);
        modelAndView.addObject("name", name);
        modelAndView.addObject("appid", appid);
        modelAndView.addObject("text", text);
        modelAndView.addObject("duplicates", duplicates);
        return modelAndView;
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    @GetMapping(value = "/vk/campaigns/edit/{id}")
    public ModelAndView editVkCampaignPage(@AuthenticationPrincipal User userFromSession,
                                           @PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("vk-campaign-edit");
        modelAndView.addObject("user", userFromSession);
        modelAndView.addObject("campaignId", id);
        return modelAndView;
    }

    @GetMapping(value = "/vk-campaign-auth")
    public String vkCampaignAuthPage(@RequestParam("appid") String appId) {
        String uri = "https://oauth.vk.com/authorize" + "?client_id=" + appId
                + "&display=" + "popup"
                + "&redirect_uri=" + "https://oauth.vk.com/blank.html"
                + "&scope=" + "wall,offline,friends"
                + "&response_type=" + "token"
                + "&v" + "5.92";
        return "redirect:" + uri;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'HR')")
    @PostMapping(value = "/vk-campaign-new")
    public String vkCampaignCreateNew(@RequestParam("token") String urlString,
                                           @RequestParam("appid") Long appId,
                                           @RequestParam("name") String campaignName,
                                           @RequestParam("text") String addText,
                                           @RequestParam("duplicates") Boolean duplicates,
                                           @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal User userFromSession) {
        String token = urlString.replaceAll(".+(access_token=)", "")
                .replaceAll("&.+", "");
        Long userId = Long.parseLong(urlString.replaceAll(".+(user_id=)", "")
                .replaceAll("&.+", ""));
        Set<VkUser> usersSet = processUploadedFile(file);
        VkAddFriendsCampaign newCampaign = new VkAddFriendsCampaign(campaignName, appId, userId, token, addText,
                false, false, duplicates,
                usersSet);
        vkCampaignService.add(newCampaign);
        return "redirect:/vk/campaigns/all";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'HR')")
    @PostMapping(value = "/vk-campaign-edit")
    public String vkCampaignEdit(@RequestParam("id") Long id,
                                      @RequestParam("name") String campaignName,
                                      @RequestParam("text") String addText,
                                      @RequestParam(value = "duplicates", defaultValue = "false") Boolean duplicates,
                                      @RequestParam("file") MultipartFile file,
                                      @AuthenticationPrincipal User userFromSession) {
        Set<VkUser> usersSet = processUploadedFile(file);
        VkAddFriendsCampaign campaign = vkCampaignService.get(id);
        campaign.setCampaignName(campaignName);
        campaign.setRequestText(addText);
        campaign.setAllowDuplicates(duplicates);
        campaign.getVkUsersToAdd().addAll(usersSet);
        vkCampaignService.update(campaign);
        return "redirect:/vk/campaigns/all";
    }

    @GetMapping("/vk-ads")
    public String getAccessTokenVkAds() {
        String robotClientId = vkConfig.getRobotClientId();
        String redirectUri = vkConfig.getRedirectUri();
        String version = vkConfig.getVersion();
        StringBuilder stb = new StringBuilder("https://oauth.vk.com/authorize")
                .append("?client_id=")
                .append(robotClientId)
                .append("&display=page&redirect_uri=")
                .append(redirectUri)
                .append("&scope=ads,offline,groups")
                .append("&response_type=token")
                .append("&v=")
                .append(version)
                .append("&state=");
        return "redirect:" + stb.toString();
    }

    private Set<VkUser> processUploadedFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Stream<String> stream =
                    new BufferedReader(new InputStreamReader(inputStream)).lines();
            return stream
                    .flatMap(line -> Stream.of(line.split("[\\p{Blank}]+")))
                    .map(s -> (s.replaceAll("\uFEFF", "")))
                    .filter(c -> c.matches("[0-9]*"))
                    .mapToLong(Long::parseLong).boxed()
                    .map(c -> new VkUser(c, new HashMap<>()))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.error("Ошибка с файлом {}", file.getName());
            return Collections.emptySet();
        }
    }
}
