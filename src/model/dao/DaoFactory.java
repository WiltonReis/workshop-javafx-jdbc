package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJdbc;

public class DaoFactory {

    public static DepartmentDAO createDepartmentDao(){
        return new DepartmentDaoJdbc(DB.getConnection());
    }
}
