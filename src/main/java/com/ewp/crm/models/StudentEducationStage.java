package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.Set;

/**
 * Этапы обучения студентов
 */

@Entity
@Table(name = "student_education_stage")
public class StudentEducationStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "student_education_id")
    private Long id;

    @Column (name = "education_stage_name")
    private String educationStageName;

    @Column (name = "education_stage_level", unique = true)
    private Integer educationStageLevel;
//Для создания однонаправленной связи удалить @OneToMany(mappedBy = "studentEducationStage") геттры и сеттеры
    @OneToMany(mappedBy = "studentEducationStage")
    @JsonIgnore
    private Set<Student> student;

    public StudentEducationStage(){}

    public StudentEducationStage(String educationStageName, @UniqueElements Integer educationStageLevel, Set<Student> student) {
        this.educationStageName = educationStageName;
        this.educationStageLevel = educationStageLevel;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEducationStageName() {
        return educationStageName;
    }

    public void setEducationStageName(String educationStageName) {
        this.educationStageName = educationStageName;
    }

    public Integer getEducationStageLevel() {
        return educationStageLevel;
    }

    public void setEducationStageLevel(Integer educationStageLevel) {
        this.educationStageLevel = educationStageLevel;
    }

    public Set<Student> getStudent() {
        return student;
    }

    public void setStudent(Set<Student> student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "StudentEducationStage{" +
                "id=" + id +
                ", educationStageName='" + educationStageName + '\'' +
                ", educationStageLevel=" + educationStageLevel +
                ", student=" + student +
                '}';
    }
}
