package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDAO;
import model.entities.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJdbc implements DepartmentDAO {

    private Connection conn;

    public DepartmentDaoJdbc(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Department dep) {
        
    }

    @Override
    public void update(Department dep) {

    }

    @Override
    public void delete(Department dep) {

    }

    @Override
    public Department findById(Integer id) {
        return null;
    }

    @Override
    public List<Department> findAll() {
        List<Department> list = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("" +
                    "SELECT department.* " +
                    "FROM department " +
                    "ORDER BY name");
            rs = st.executeQuery();

            while (rs.next()) {
                list.add(instantieteDepartment(rs));
            }
            return list;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultset(rs);
        }

    }

    private Department instantieteDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("id"));
        dep.setName(rs.getString("name"));
        return dep;
    }
}
