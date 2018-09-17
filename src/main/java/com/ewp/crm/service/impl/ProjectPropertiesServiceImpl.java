package com.ewp.crm.service.impl;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.repository.interfaces.ProjectPropertiesRepository;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectPropertiesServiceImpl implements ProjectPropertiesService {

    private final ProjectPropertiesRepository projectPropertiesRepository;

    @Autowired
    public ProjectPropertiesServiceImpl(ProjectPropertiesRepository projectPropertiesRepository) {
        this.projectPropertiesRepository = projectPropertiesRepository;
    }

    @Override
    public ProjectProperties saveAndFlash(ProjectProperties entity) {
        return projectPropertiesRepository.saveAndFlush(entity);
    }

    @Override
    public ProjectProperties get() {
        return projectPropertiesRepository.getProjectPropertiesById(1L);
    }
}
