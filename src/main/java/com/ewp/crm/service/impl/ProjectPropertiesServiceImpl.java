package com.ewp.crm.service.impl;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.repository.interfaces.ProjectPropertiesRepository;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectPropertiesServiceImpl extends CommonServiceImpl<ProjectProperties> implements ProjectPropertiesService {

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
        return get(1L);
    }

    @Override
    public synchronized ProjectProperties getOrCreate() {
        ProjectProperties result = get(1L);
        if (result == null) {
            result = add(new ProjectProperties());
        }
        return result;
    }
}
