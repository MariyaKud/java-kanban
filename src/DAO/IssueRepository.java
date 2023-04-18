package DAO;

import service.TaskManager;

import java.io.File;

public interface IssueRepository {

    TaskManager load(TaskManager tracker);

    void save(TaskManager tracker) throws ManagerSaveException;
}
