package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.StudentEducationStage;

import java.util.List;

public interface StudentEducationStageRepositoryCustom {
    void update(StudentEducationStage studentEducationStage);
    List<StudentEducationStage> getStudentEducationStageByCourse(Course course);
    void add(StudentEducationStage studentEducationStage, Course course);
    void deleteCustom(StudentEducationStage studentEducationStage);
}
