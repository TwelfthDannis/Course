package com.example.Backend.services;

import com.example.Backend.models.Item;
import com.example.Backend.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemsService {

    private final ItemsRepository itemsRepository;

    @Autowired
    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public List<Item> getAllItem(){
        return itemsRepository.findAll();
    }

    public Item getItemId(int id){
        Optional<Item> optionalItem = itemsRepository.findById(id);
        return optionalItem.orElse(null);
    }

    @Transactional
    public void saveItem(Item item){
        itemsRepository.save(item);
    }

    @Transactional
    public void updateItem(int id, Item item){
        item.setId(id);
        itemsRepository.save(item);
    }

    @Transactional
    public void deleteItem(int id){
        itemsRepository.deleteById(id);
    }


    public Item getItemFindByTitle(Item item){
        Optional<Item> item_db = itemsRepository.findByTitle(item.getTitle());
        return item_db.orElse(null);
    }

}
