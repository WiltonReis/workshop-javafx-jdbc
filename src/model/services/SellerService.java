package model.services;

import model.dao.DaoFactory;
import model.dao.DepartmentDAO;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

import java.util.List;

public class SellerService {

    SellerDAO sellerDAO = DaoFactory.createSellerDao();

    public void insertOrUpdate(Seller sel){
        if (sel.getId() == null) {
            sellerDAO.insert(sel);
        }
        else {
            sellerDAO.update(sel);
        }
    }

    public void delete(Seller sel){
        sellerDAO.delete(sel);
    }

    public List<Seller> findAll(){
        return sellerDAO.findAll();
    }
}
