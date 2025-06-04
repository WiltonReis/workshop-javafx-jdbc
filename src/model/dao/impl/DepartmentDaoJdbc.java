package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDAO;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJdbc implements DepartmentDAO {

    private Connection conn;

    public DepartmentDaoJdbc(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Department dep) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("" +
                    "INSERT INTO department " +
                    "(Name) " +
                    "VALUES " +
                    "(?)", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, dep.getName());

            int x = st.executeUpdate();

            if (x > 0){
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    dep.setId(id);
                }
                DB.closeResultset(rs);
            }
            else {
                throw new DbException("Erro inesperado!");
            }
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Department dep) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("" +
                    "UPDATE department " +
                    "SET Name = ? " +
                    "WHERE Id = ?");
            st.setString(1, dep.getName());
            st.setInt(2, dep.getId());

            st.executeUpdate();
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void delete(Department dep) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("" +
                    "DELETE FROM department " +
                    "WHERE Id = ?");

            st.setInt(1, dep.getId());
            st.executeUpdate();

        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("" +
                    "SELECTED department.* " +
                    "FROM department " +
                    "WHERE Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()){
                Department dep = instantieteDepartment(rs);
                return dep;
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultset(rs);
        }
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
