package com.ewp.crm.controllers;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.models.User;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.models.vkcampaigns.VkUser;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
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
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER')")
public class VkController {
    private static Logger logger = LoggerFactory.getLogger(VkController.class);

    private final UserService userService;
    private final VKService vkService;
    private final VkTrackedClubService vkTrackedClubService;
    private final ProjectPropertiesService projectPropertiesService;
    private final VkCampaignService vkCampaignService;
    private final VKConfig vkConfig;

    private ProjectProperties projectProperties;

    @Autowired
    public VkController(VKService vkService,
                        UserService userService,
                        VkTrackedClubService vkTrackedClubService,
                        ProjectPropertiesService projectPropertiesService,
                        VkCampaignService vkCampaignService,
                        VKConfig vkConfig) {
        this.vkService = vkService;
        this.userService = userService;
        this.vkTrackedClubService = vkTrackedClubService;
        this.projectPropertiesService = projectPropertiesService;
        this.vkCampaignService = vkCampaignService;
        this.vkConfig = vkConfig;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @GetMapping(value = "/admin/vkontakte/trackedclub")
    public ModelAndView trackingGroupInfo() {
        ModelAndView modelAndView = new ModelAndView("vk-trackedclub-info");
        modelAndView.addObject("vkTrackedClub", vkTrackedClubService.getAll());
        modelAndView.addObject("newVkTrackedClub", new VkTrackedClub());
        return modelAndView;
    }

    @GetMapping(value = "/vk-auth")
    public String vkAuthPage() {
        String uri = vkService.receivingTokenUri();
        return "redirect:" + uri;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @PostMapping(value = "/vk-auth")
    public String vkGetAccessToken(@RequestParam("token") String token, @AuthenticationPrincipal User userFromSession) {
        String applicationToken = vkService.replaceApplicationTokenFromUri(token);
        if ((projectProperties = projectPropertiesService.get()) == null) {
            projectProperties = new ProjectProperties();
        }
        projectProperties.setTechnicalAccountToken(applicationToken);
        projectPropertiesService.saveAndFlash(new ProjectProperties(applicationToken));
        userFromSession.setVkToken(applicationToken);
        userService.update(userFromSession);
        return "redirect:/client";
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @GetMapping(value = "/vk/campaigns/all")
    public ModelAndView showVkCampaignsPage(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("all-vk-friends-campaigns");
        modelAndView.addObject("campaignsList", vkCampaignService.getAll());
        modelAndView.addObject("stats", vkCampaignService.getRequestsStats());
        modelAndView.addObject("user", userFromSession);
        return modelAndView;
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
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
      String robotClientId=null;
      String redirectUri=null;
      try{
          robotClientId = vkConfig.getRobotClientId();
          redirectUri = vkConfig.getRedirectUri();
          if(robotClientId.isEmpty() || redirectUri.isEmpty()) {
              throw new NullPointerException();
          }
      } catch (NullPointerException npe) {
          logger.error("Check the robotClientId or redirectUri from vk.properties -> vk.robot.app.clientId or vk.app.redirect_uri", npe);
      }
        StringBuilder stb = new StringBuilder("https://oauth.vk.com/authorize");
        stb.append("?client_id=");
        stb.append(robotClientId);
        stb.append("&display=page&redirect_uri=");
        stb.append(redirectUri);
        stb.append("&scope=ads,offline,groups");
        stb.append("&response_type=token");
        stb.append("&v=5.92&state=");
        return "redirect:" + stb.toString();
    }

    private Set<VkUser> processUploadedFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Stream<String> stream =
                    new BufferedReader(new InputStreamReader(inputStream)).lines();
            return stream
                    .flatMap(line -> Stream.of(line.split("[\\p{Blank}]+")))
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
