package com.rowland.engineering.ecommerce.repository;

import com.rowland.engineering.ecommerce.model.Favourite;
import com.rowland.engineering.ecommerce.model.Product;
import com.rowland.engineering.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findAllFavouriteByUserId(Long userId);

    List<Favourite> findAllByProductId(Long id);
    List<Favourite> findAllByUserId(Long userId);
    Favourite findByProductAndUser(Product product, User user);

}
