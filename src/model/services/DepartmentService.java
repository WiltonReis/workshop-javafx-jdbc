package model.services;

import model.dao.DaoFactory;
import model.dao.DepartmentDAO;
import model.dao.impl.DepartmentDaoJdbc;
import model.entities.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    DepartmentDAO departmentDAO = DaoFactory.createDepartmentDao();

    public List<Department> findAll(){
        return departmentDAO.findAll();
    }
}
