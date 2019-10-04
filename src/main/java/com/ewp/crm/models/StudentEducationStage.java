package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column (name = "education_stage_level")
    private Integer educationStageLevel;
//Для создания однонаправленной связи удалить @OneToMany(mappedBy = "studentEducationStage") геттры и сеттеры
    @OneToMany(mappedBy = "studentEducationStage")
    @JsonIgnore
    private Set<Student> student;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id")
    private Course course;

    public StudentEducationStage(){}

    public StudentEducationStage(String educationStageName, Integer educationStageLevel,
                                 Set<Student> student, Course course) {
        this.educationStageName = educationStageName;
        this.educationStageLevel = educationStageLevel;
        this.student = student;
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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
                ", course=" + course +
                '}';
    }
}
