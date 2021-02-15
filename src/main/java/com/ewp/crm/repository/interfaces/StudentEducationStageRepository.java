package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.StudentEducationStage;

import java.util.List;

public interface StudentEducationStageRepository extends CommonGenericRepository<StudentEducationStage>, StudentEducationStageRepositoryCustom {
    List<StudentEducationStage> findStudentEducationStageById(Long id);
}
