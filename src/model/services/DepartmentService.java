package model.services;

import model.dao.DaoFactory;
import model.dao.DepartmentDAO;
import model.entities.Department;

import java.util.List;

public class DepartmentService {

    DepartmentDAO departmentDAO = DaoFactory.createDepartmentDao();

    public void insertOrUpdate(Department dep){
        if (dep.getId() == null) {
            departmentDAO.insert(dep);
        }
        else {
            departmentDAO.update(dep);
        }
    }

    public void delete(Department dep){
        departmentDAO.delete(dep);
    }

    public List<Department> findAll(){
        return departmentDAO.findAll();
    }
}
