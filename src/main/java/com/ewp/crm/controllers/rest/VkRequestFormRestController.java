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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
public class VkRequestFormRestController {
    private final VkRequestFormService vkRequestFormService;

    @Autowired
    public VkRequestFormRestController(VkRequestFormService vkRequestFormService) {
        this.vkRequestFormService = vkRequestFormService;
    }

    @GetMapping(value = "/vk/request/form")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ModelAndView vkRequestForm(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("vk-request-form");
        modelAndView.addObject("userCustomize", userFromSession);
        List<VkRequestForm> vkRequestFormList = vkRequestFormService.getAllVkRequestForm();
        vkRequestFormList.sort(Comparator.comparingInt(VkRequestForm::getNumberVkField));
        modelAndView.addObject("vkRequest", vkRequestFormList);
        return modelAndView;
    }

    @RequestMapping(value = "/vk/request/create", method = RequestMethod.POST)
    public ResponseEntity createVkRequestForm(@RequestBody VkRequestForm vkRequestForm) {
        vkRequestFormService.addVkRequestForm(vkRequestForm);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/vk/request/{id}", method = RequestMethod.GET)
    public ResponseEntity<VkRequestForm> getVkRequestFormById(@PathVariable("id") Long id) {
        Optional<VkRequestForm> requestForm = vkRequestFormService.getVkRequestFormById(id);
        return requestForm.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @RequestMapping(value = "/vk/request/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<VkRequestForm> updateVkRequestForm(@PathVariable("id") Long id, @RequestBody VkRequestForm vkRequestForm) {
        vkRequestForm.setId(id);
        vkRequestFormService.updateVkRequestForm(vkRequestForm);
        return new ResponseEntity<>(vkRequestForm, HttpStatus.OK);
    }

    @RequestMapping(value = "/vk/request/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<Long> deleteVkRequestFormById(@PathVariable("id") Long id) {
        vkRequestFormService.deleteVkRequestFormById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


