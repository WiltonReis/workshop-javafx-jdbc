package model.dao;

import model.entities.Seller;

import java.util.List;

public interface SellerDAO {

    void insert (Seller sel);
    void update (Seller sel);
    void delete (Seller sel);
    Seller findById(Integer id);
    List<Seller> findAll();
}
