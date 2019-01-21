package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface CaptchaService {

    Optional<String> captchaImgResolver(String captchaImgUrl);

}
