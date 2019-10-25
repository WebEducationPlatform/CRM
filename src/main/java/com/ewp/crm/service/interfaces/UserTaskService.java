package com.ewp.crm.service.interfaces;

        import com.ewp.crm.models.UserTask;

        import java.util.List;

public interface UserTaskService {
    List<UserTask> getAll();

    UserTask add(UserTask userTask);

    UserTask getById(Long userTaskId);

    void update(UserTask userTaskFromDb);

    boolean delete(Long id);
}
