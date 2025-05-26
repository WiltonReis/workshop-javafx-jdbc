package model.dao;

import model.entities.Department;

import java.util.List;

public interface DepartmentDAO {

    void insert (Department dep);
    void update (Department dep);
    void delete (Department dep);
    Department findById(Integer id);
    List<Department> findAll();
}
