package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.StudentEducationStage;

import java.util.List;

public interface StudentEducationStageService {
    void add(StudentEducationStage studentEducationStage, Course course);
    void update(StudentEducationStage studentEducationStage, Course course);
    void delete(Long id);
    List<StudentEducationStage> getAll();
    StudentEducationStage getStudentEducationStage(Long id);
    List<StudentEducationStage> getStudentEducationStageByCourse(Course course);
    public void deleteCustom(StudentEducationStage studentEducationStage);
}
