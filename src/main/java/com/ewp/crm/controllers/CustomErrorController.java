package com.ewp.crm.controllers;

import com.ewp.crm.models.Client;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ModelAndView modelAndView = new ModelAndView("error");

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            modelAndView.addObject("statusCode", statusCode);
        }
        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
