package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.models.VkRequestForm;
import com.ewp.crm.service.interfaces.VkRequestFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class VkRequestFormRestController {
    private final VkRequestFormService vkRequestFormService;

    @Autowired
    public VkRequestFormRestController(VkRequestFormService vkRequestFormService) {
        this.vkRequestFormService = vkRequestFormService;
    }

    @GetMapping(value = "/vk/request/form")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ModelAndView settingsVkBid(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("vk-request-form");
        modelAndView.addObject("userCustomize", userFromSession);
        modelAndView.addObject("vkRequest", vkRequestFormService.getAll());
        return modelAndView;
    }

    @RequestMapping(value = "/vk/request/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody VkRequestForm vkRequestForm) {
        vkRequestFormService.add(vkRequestForm);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/vk/request/{id}", method = RequestMethod.GET)
    public ResponseEntity<VkRequestForm> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vkRequestFormService.get(id));
    }


    @RequestMapping(value = "/vk/request/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<VkRequestForm> updateUser(@PathVariable("id") Long id, @RequestBody VkRequestForm vkRequestForm) {
        vkRequestForm.setId(id);
        vkRequestFormService.update(vkRequestForm);
        return new ResponseEntity<>(vkRequestForm, HttpStatus.OK);
    }

    @RequestMapping(value = "/vk/request/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<Long> deleteUser(@PathVariable("id") Long id) {
        vkRequestFormService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


