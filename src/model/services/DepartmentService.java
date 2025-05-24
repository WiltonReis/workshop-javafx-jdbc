package model.services;

import model.entities.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    public List<Department> findAll(){
        List<Department> list = new ArrayList<>();
        list.add(new Department(1L, "Electronics"));
        list.add(new Department(2L, "Books"));
        list.add(new Department(3L, "Computer"));
        return list;
    }
}
