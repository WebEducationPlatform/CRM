package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.StudentEducationStage;
import com.ewp.crm.repository.interfaces.StudentEducationStageRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class StudentEducationStageRepositoryImpl implements StudentEducationStageRepositoryCustom {
    private static Logger logger = LoggerFactory.getLogger(StudentEducationStageRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void update(StudentEducationStage studentEducationStage) {
        if(studentEducationStage!=null){
            StudentEducationStage studentEducationStageTemp = entityManager.find(StudentEducationStage.class,
                studentEducationStage.getId());
            Course course = studentEducationStageTemp.getCourse();
                //Если не меняется уровень studentEducationStage
                if (studentEducationStage.getEducationStageLevel()!=null &&
                        studentEducationStage.getEducationStageLevel().equals(studentEducationStageTemp.getEducationStageLevel())) {
                    studentEducationStageTemp.setEducationStageName(studentEducationStage.getEducationStageName());
                    entityManager.merge(studentEducationStageTemp);
                } else
                //Если меняется уровень studentEducationStage
                    if (studentEducationStage.getEducationStageLevel()!=null) {
                        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
                        studentEducationStageSet.remove(studentEducationStageTemp);

                        //Изменение уровней StudentEducationStage после "удаления" studentEducationStageTemp
                        for (StudentEducationStage set : studentEducationStageSet) {
                            if(set.getEducationStageLevel()>studentEducationStageTemp.getEducationStageLevel()) {
                                Integer stageLevel = set.getEducationStageLevel()-1;
                                set.setEducationStageLevel(stageLevel);
                            }
                        }
                        course.setStudentEducationStage(studentEducationStageSet);

                        //Изменение уровней StudentEducationStage, перед добалением studentEducationStage
                        studentEducationStageSet = course.getStudentEducationStage();
                        Integer maxLevel = maxLevel(course);
                        if(studentEducationStage.getEducationStageLevel()<=maxLevel) {
                            for (StudentEducationStage set : studentEducationStageSet) {
                                if (studentEducationStage.getEducationStageLevel() <= set.getEducationStageLevel()) {
                                    Integer stageLevel = set.getEducationStageLevel() + 1;
                                    set.setEducationStageLevel(stageLevel);
                                }
                            }
                        } else {
                            studentEducationStage.setEducationStageLevel(maxLevel+1);
                        }
                        studentEducationStageSet.add(studentEducationStage);
                        course.setStudentEducationStage(studentEducationStageSet);
     //                   entityManager.merge(course);
                        entityManager.merge(studentEducationStage);
                } else
                    //Если уровень studentEducationStage не задан
                    if(studentEducationStage.getEducationStageLevel()==null) {
                        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
                        studentEducationStageSet.remove(studentEducationStageTemp);
                        //Изменение уровней StudentEducationStage после "удаления" studentEducationStageTemp
                        for (StudentEducationStage set : studentEducationStageSet) {
                            if(set.getEducationStageLevel()>studentEducationStageTemp.getEducationStageLevel()) {
                                Integer stageLevel = set.getEducationStageLevel()-1;
                                set.setEducationStageLevel(stageLevel);
                            }
                        }
                        course.setStudentEducationStage(studentEducationStageSet);
                        //Установка уровня studentEducationStage и добавление studentEducationStage в базу
                        Integer studentEducationStageLevel = maxLevel(course)+1;
                        studentEducationStageSet = course.getStudentEducationStage();
                        studentEducationStage.setEducationStageLevel(studentEducationStageLevel);
                        studentEducationStageSet.add(studentEducationStage);
                        course.setStudentEducationStage(studentEducationStageSet);
     //                   entityManager.merge(course);
                        entityManager.merge(studentEducationStage);
                    }
        }
    }

    @Override
    public List<StudentEducationStage> getStudentEducationStageByCourse(Course course) {
        Course courseTemp = entityManager.find(Course.class, course.getId());
        if(courseTemp!=null) {
            List<StudentEducationStage> studentEducationStageList = new ArrayList<StudentEducationStage>();
            Set<StudentEducationStage> studentEducationStageSet = courseTemp.getStudentEducationStage();
            if(studentEducationStageSet!=null) {
                studentEducationStageList.addAll(studentEducationStageSet);
                return studentEducationStageList;
            }
        }
        return null;
    }

    @Override
     public void add(StudentEducationStage studentEducationStage, Course course) {
        Course courseTemp = entityManager.find(Course.class, course.getId());
        Set<StudentEducationStage> studentEducationStageSet = courseTemp.getStudentEducationStage();
        Integer maxLevel = maxLevel(courseTemp);
        if(studentEducationStage.getEducationStageLevel()!=null) {
            boolean isStudentEducationStageIsExist = false;
            // проверка наличия в course StudentEducationStage уровня studentEducationStage
            for (StudentEducationStage set : studentEducationStageSet) {
                if (studentEducationStage.getEducationStageLevel().equals(set.getEducationStageLevel())) {
                    isStudentEducationStageIsExist = true;
                }
            }
            //Увеличения уровней StudentEducationStage, которые больше или равны уровню studentEducationStage
            studentEducationStageSet = courseTemp.getStudentEducationStage();
            if (isStudentEducationStageIsExist) {
                for (StudentEducationStage set : studentEducationStageSet) {
                    if (studentEducationStage.getEducationStageLevel() <= set.getEducationStageLevel()) {
                        Integer stageLevel = set.getEducationStageLevel() + 1;
                        set.setEducationStageLevel(stageLevel);
                    }
                }
            }
        }
        //Если уровень studentEducationStage не задан или он больше maxLevel+1, то уровень равен maxLevel+1
        if(studentEducationStage.getEducationStageLevel()==null || studentEducationStage.getEducationStageLevel() > (maxLevel+1)) {
            Integer stageLevel = maxLevel+1;
            studentEducationStage.setEducationStageLevel(stageLevel);
        }
        courseTemp.setStudentEducationStage(studentEducationStageSet);
        studentEducationStage.setCourse(courseTemp);
        entityManager.persist(courseTemp);
        //Добавление нового studentEducationStage
        entityManager.merge(studentEducationStage);
    }

    @Override
     public void deleteCustom(StudentEducationStage studentEducationStage) {
        Course course = studentEducationStage.getCourse();
        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
        StudentEducationStage studentEducationStageTmp = null;
        //Смена уровней обучения в связи с удалением
        if(studentEducationStageSet.size()>(studentEducationStage.getEducationStageLevel()+1)) {
            for(StudentEducationStage set : studentEducationStageSet) {
                if((set.getEducationStageLevel() > studentEducationStage.getEducationStageLevel()) && (!studentEducationStage.equals(set))) {
                    Integer level = set.getEducationStageLevel()-1;
                    set.setEducationStageLevel(level);
                } else if (studentEducationStage.equals(set)) {
                    studentEducationStageTmp = set;
                }
            }
        }

        if(studentEducationStageTmp!=null) {
            studentEducationStageSet.remove(studentEducationStageTmp);
        }
        course.setStudentEducationStage(studentEducationStageSet);

        entityManager.merge(course);
        entityManager.remove(studentEducationStage);
    }

    //Метод для определения максимального уровня обучения
    private Integer maxLevel(Course course) {
        Integer maxLevel = -1;
        Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
        for (StudentEducationStage set : studentEducationStageSet) {
            if (set.getEducationStageLevel() > maxLevel) {
                maxLevel = set.getEducationStageLevel();
            }
        }
        return maxLevel;
    }
}
