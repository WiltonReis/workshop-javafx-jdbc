package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DaoFactory;
import model.dao.DepartmentDAO;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDaoJdbc implements SellerDAO {

    private final Connection conn;

    public SellerDaoJdbc(Connection conn){
        this.conn = conn;
    }

    private final DepartmentDAO departmentService = DaoFactory.createDepartmentDao();

    @Override
    public void insert(Seller sel) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("" +
                    "INSERT INTO seller " +
                    "(Name) " +
                    "(Email)" +
                    "(BirthDate)" +
                    "(BaseSalary)" +
                    "(DepartmentId) " +
                    "VALUES " +
                    "(?), (?), (?), (?), (?)", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, sel.getName());
            st.setString(2, sel.getEmail());
            st.setDate(3, Date.valueOf(sel.getBirthDate()));
            st.setDouble(4, sel.getBaseSalary());
            st.setInt(5, sel.getDepartment().getId());

            int x = st.executeUpdate();

            if (x > 0){
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    sel.setId(id);
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
    public void update(Seller sel) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("" +
                    "UPDATE seller " +
                    "SET Name = ? " +
                    ", Email = ? " +
                    ", BirthDate = ? " +
                    ", BaseSalary = ? " +
                    ", DepartmentId = ? " +
                    "WHERE Id = ?");
            st.setString(1, sel.getName());
            st.setString(2, sel.getEmail());
            st.setDate(3, Date.valueOf(sel.getBirthDate()));
            st.setDouble(4, sel.getBaseSalary());
            st.setInt(5, sel.getDepartment().getId());
            st.setInt(6, sel.getId());

            st.executeUpdate();
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void delete(Seller sel) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("" +
                    "DELETE FROM seller " +
                    "WHERE Id = ?");

            st.setInt(1, sel.getId());
            st.executeUpdate();

        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("" +
                    "SELECT seller.* " +
                    "FROM seller " +
                    "WHERE Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()){
                return instantieteSeller(rs);
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
    public List<Seller> findAll() {
        List<Seller> list = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("" +
                    "SELECT seller.* " +
                    "FROM seller " +
                    "ORDER BY name");
            rs = st.executeQuery();

            while (rs.next()) {
                list.add(instantieteSeller(rs));
            }
            return list;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultset(rs);
        }

    }

    private Seller instantieteSeller(ResultSet rs) throws SQLException {
        Seller sel = new Seller();
        sel.setId(rs.getInt("id"));
        sel.setName(rs.getString("name"));
        sel.setEmail(rs.getString("email"));
        sel.setBirthDate(rs.getDate("birthDate").toLocalDate());
        sel.setBaseSalary(rs.getDouble("baseSalary"));
        sel.setDepartment(instantieteDepartment(rs));
        return sel;
    }

    private Department instantieteDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep = departmentService.findById(rs.getInt("departmentId"));
        return dep;
    }
}
