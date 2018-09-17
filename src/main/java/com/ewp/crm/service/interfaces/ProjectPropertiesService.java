package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ProjectProperties;

public interface ProjectPropertiesService {
    ProjectProperties get();
    ProjectProperties saveAndFlash(ProjectProperties entity);
}
