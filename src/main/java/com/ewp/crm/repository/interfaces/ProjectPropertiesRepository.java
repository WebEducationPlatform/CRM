package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ProjectProperties;

public interface ProjectPropertiesRepository extends CommonGenericRepository<ProjectProperties> {
    ProjectProperties getProjectPropertiesById (Long Long);
}
