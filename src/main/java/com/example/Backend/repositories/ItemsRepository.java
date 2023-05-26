package com.example.Backend.repositories;

import com.example.Backend.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Integer> {

    Optional<Item> findByTitle(String title);

    List<Item> findByTitleContainingIgnoreCase(String name);

    @Query(value = "select * from item where ((lower(title) like %?1%) or (lower(title) like '?1%') or (lower(title) like '%?1') and (price >= ?2 and price <= ?3))", nativeQuery = true)
    List<Item> findByTitleAndPriceGreaterThenEqualAndPriceLessThen(String title, float min, float max);

    @Query(value = "select * from item where ((lower(title) like %?1%) or (lower(title) like '?1%') or (lower(title) like '%?1') and (price >= ?2 and price <= ?3) order by price)", nativeQuery = true)
    List<Item> findByTitleOrderByPrice(String title, float min, float max);

    @Query(value = "select * from item where ((lower(title) like %?1%) or (lower(title) like '?1%') or (lower(title) like '%?1') and (price >= ?2 and price <= ?3) order by price desc)", nativeQuery = true)
    List<Item> findByTitleOrderByPriceDesc(String title, float min, float max);

    @Query(value = "select * from item where category_id=?4 and ((lower(title) like %?1%) or (lower(title) like '?1%') or (lower(title) like '%?1')) and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Item> findByTitleAndCategoryOrderByPrice(String title, float min, float max, int category);

    @Query(value = "select * from item where category_id=?4 and ((lower(title) like %?1%) or (lower(title) like '?1%')) or (lower(title) like '%?1') and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Item> findByTitleAndCategoryOrderByPriceDesc(String title, float min, float max, int category);
}
