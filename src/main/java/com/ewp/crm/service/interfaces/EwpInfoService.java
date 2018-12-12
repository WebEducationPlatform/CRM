package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.models.dto.StudentProgressInfo;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

public interface EwpInfoService {
    List<StudentProgressInfo> getStudentProgressInfo(List<Student> listStudents);
}
