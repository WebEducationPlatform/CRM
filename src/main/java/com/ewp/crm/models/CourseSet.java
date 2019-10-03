package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course_set")
public class CourseSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_set_id")
    private Long id;

    @Column(name = "course_set_name", nullable = false, unique = true)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @OneToOne
    @JoinColumn (name = "course_id")
    private Course course;

    @ElementCollection
    @CollectionTable(name = "course_set_students", joinColumns = @JoinColumn(name = "course_set_id"))
    @Column(name = "student")
    @JsonIgnore
    private Set<Student> students = new HashSet<>();

    public CourseSet() {
    }

    public CourseSet(String name, LocalDate startDate, Course course) {
        this.name = name;
        this.startDate = startDate;
        this.course = course;
    }

    public CourseSet(String name, LocalDate startDate, Course course, Set<Student> students) {
        this.name = name;
        this.startDate = startDate;
        this.course = course;
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public void setStudent(Student student) {
        students.add(student);
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + startDate + " " + course.getName() + " " + students;
    }
}
