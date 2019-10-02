package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.StudentEducationStage;
import com.ewp.crm.repository.interfaces.StudentEducationStageRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class StudentEducationStageRepositoryImpl implements StudentEducationStageRepositoryCustom {
    private static Logger logger = LoggerFactory.getLogger(StudentEducationStageRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void update(StudentEducationStage studentEducationStage, Course course) {
        if(course!=null && studentEducationStage!=null){
            StudentEducationStage studentEducationStageTemp = entityManager.find(StudentEducationStage.class,
                studentEducationStage.getId());
            if(studentEducationStageTemp.getCourse().equals(course)) {
                //Если не меняется уровень studentEducationStage
                if (studentEducationStage.getEducationStageLevel()!=null &&
                        studentEducationStage.getEducationStageLevel().equals(studentEducationStageTemp.getEducationStageLevel())) {
                    studentEducationStageTemp.setEducationStageName(studentEducationStage.getEducationStageName());
                    entityManager.merge(studentEducationStageTemp);
                } else
                //Если меняется уровень studentEducationStage
                    if (studentEducationStage.getEducationStageLevel()!=null) {
                        boolean isStudentEducationStageIsExist = false;
                        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
                        // проверка наличия в course StudentEducationStage уровня совпадающего с studentEducationStage
                        for (StudentEducationStage set: studentEducationStageSet) {
                            if(studentEducationStage.getEducationStageLevel().equals(set.getEducationStageLevel())){
                                isStudentEducationStageIsExist = true;
                                break;
                            }
                        }
                        //Увеличения уровней StudentEducationStage, которые больше или равны уровню studentEducationStage
                        if(isStudentEducationStageIsExist) {
                            for (StudentEducationStage set: studentEducationStageSet) {
                                if(studentEducationStage.getEducationStageLevel() <= set.getEducationStageLevel()) {
                                    set.setEducationStageLevel(set.getEducationStageLevel()+1);
                                    entityManager.merge(set);
                                }
                            }
                        }
                        studentEducationStageTemp.setEducationStageName(studentEducationStage.getEducationStageName());
                        studentEducationStageTemp.setEducationStageLevel(studentEducationStage.getEducationStageLevel());
                        entityManager.merge(studentEducationStageTemp);
                } else
                    //Если уровень studentEducationStage не задан
                    if(studentEducationStage.getEducationStageLevel()==null) {
                        Integer maxLevel = 0;
                        Integer studentEducationStageTempLevel = studentEducationStageTemp.getEducationStageLevel();
                        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
                     //Поиск максимума и одновременное уменьшение EducationStageLevel и объектов,
                        // чей уровень больше studentEducationStageTempLevel
                        for (StudentEducationStage set: studentEducationStageSet) {
                            if(set.getEducationStageLevel()>maxLevel){
                                maxLevel = set.getEducationStageLevel();
                            }
                            if (set.getEducationStageLevel()>studentEducationStageTempLevel) {
                                set.setEducationStageLevel(set.getEducationStageLevel()+1);
                                entityManager.merge(set);
                            }
                        }
                        studentEducationStageTemp.setEducationStageLevel(maxLevel);
                        studentEducationStageTemp.setEducationStageName(studentEducationStage.getEducationStageName());
                        entityManager.merge(studentEducationStageTempLevel);
                    }
            } else return;
        }
    }

    @Override
    public List<StudentEducationStage> getStudentEducationStageByCourse(Course course) {
        Course courseTemp = entityManager.find(Course.class, course.getId());
        if(course!=null) {
            List<StudentEducationStage> studentEducationStageList = new ArrayList<StudentEducationStage>();
            if(studentEducationStageList.add((StudentEducationStage) courseTemp.getStudentEducationStage())) {
                return studentEducationStageList;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void add(StudentEducationStage studentEducationStage, Course course) {
        Course courseTemp = entityManager.find(Course.class, course.getId());
        Set<StudentEducationStage> studentEducationStageSet = courseTemp.getStudentEducationStage();
        Integer maxLevel = 0;
        if(studentEducationStage.getEducationStageLevel()!=null) {
            boolean isStudentEducationStageIsExist = false;
            // проверка наличия в course StudentEducationStage уровня studentEducationStage
            for (StudentEducationStage set : studentEducationStageSet) {
                if (studentEducationStage.getEducationStageLevel().equals(set.getEducationStageLevel())) {
                    isStudentEducationStageIsExist = true;
                }
                if (set.getEducationStageLevel() > maxLevel) {
                    maxLevel = set.getEducationStageLevel();
                }
            }
            //Увеличения уровней StudentEducationStage, которые больше или равны уровню studentEducationStage
            if (isStudentEducationStageIsExist) {
                for (StudentEducationStage set : studentEducationStageSet) {
                    if (studentEducationStage.getEducationStageLevel() <= set.getEducationStageLevel()) {
                        set.setEducationStageLevel(set.getEducationStageLevel() + 1);
                        entityManager.merge(set);
                    }
                }
            }
        } else {
            for (StudentEducationStage set : studentEducationStageSet) {
                if (set.getEducationStageLevel() > maxLevel) {
                    maxLevel = set.getEducationStageLevel();
                }
            }

        }
        //Если уровень studentEducationStage не задан или он больше maxLevel+1, то уровень равен maxLevel+1
        if(studentEducationStage.getEducationStageLevel()==null || studentEducationStage.getEducationStageLevel() > (maxLevel+1)) {
            studentEducationStage.setEducationStageLevel(maxLevel+1);
        }
        //Добавление нового studentEducationStage
        studentEducationStage.setCourse(courseTemp);
        entityManager.merge(studentEducationStage);
    }

    @Override
    @Transactional
    public void deleteCustom(StudentEducationStage studentEducationStage) {
        Course course = studentEducationStage.getCourse();
        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
        if(studentEducationStageSet.size()>(studentEducationStage.getEducationStageLevel()+1)) {
            for(StudentEducationStage set : studentEducationStageSet) {
                if(set.getEducationStageLevel() > studentEducationStage.getEducationStageLevel()) {
                    set.setEducationStageLevel(set.getEducationStageLevel()-1);
                    entityManager.merge(set);
                }
            }
        }
        entityManager.remove(studentEducationStage);
    }
}
