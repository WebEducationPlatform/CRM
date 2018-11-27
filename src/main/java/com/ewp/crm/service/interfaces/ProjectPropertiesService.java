package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ProjectProperties;

public interface ProjectPropertiesService extends CommonService<ProjectProperties> {

    ProjectProperties get();

    ProjectProperties getOrCreate();

    ProjectProperties saveAndFlash(ProjectProperties entity);
}
