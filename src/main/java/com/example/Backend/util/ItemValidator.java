package com.example.Backend.util;

import com.example.Backend.models.Item;
import com.example.Backend.services.ItemsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {


    private final ItemsService itemsService;

    public ItemValidator(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    Item item = (Item) target;
        if (itemsService.getItemFindByTitle(item) != null){
            errors.rejectValue("title", "","Name is used");
        }
    }
}
