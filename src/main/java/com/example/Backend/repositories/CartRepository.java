package com.example.Backend.repositories;

import com.example.Backend.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(int id);

    @Modifying
    @Query(value = "delete from item_cart where user_id=?1 and item_id=?2", nativeQuery = true)
    void deleteCartById(int user_id, int item_id);
}
