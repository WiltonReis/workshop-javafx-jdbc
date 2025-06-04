package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJdbc;
import model.dao.impl.SellerDaoJdbc;

public class DaoFactory {

    public static DepartmentDAO createDepartmentDao(){
        return new DepartmentDaoJdbc(DB.getConnection());
    }
    public static SellerDAO createSellerDao(){return new SellerDaoJdbc(DB.getConnection());}
}
