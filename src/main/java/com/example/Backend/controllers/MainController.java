package com.example.Backend.controllers;

import com.example.Backend.repositories.ItemsRepository;
import com.example.Backend.services.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/item")
public class MainController {
    private final ItemsService itemsService;
    private final ItemsRepository itemsRepository;


    @Autowired
    public MainController(ItemsService itemsService, ItemsRepository itemsRepository) {
        this.itemsService = itemsService;
        this.itemsRepository = itemsRepository;
    }

    @GetMapping("")
    public String getAllItem(Model model){
        model.addAttribute("items", itemsService.getAllItem());
        return "item/item";
    }

    @GetMapping("/info/{id}")
    public String infoItem(@PathVariable("id") int id, Model model){
        model.addAttribute("item", itemsService.getItemId(id));
        return "item/infoItem";
    }

    @PostMapping("/search")
    public String itemSearch(@RequestParam("search") String search, @RequestParam("min") String min, @RequestParam("max") String max, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "category", required = false, defaultValue = "") String category, Model model){
        if (!min.isEmpty() & !max.isEmpty()) {
            if (!price.isEmpty()) {
                if (price.equals("sorted_by_ascending_price")) {

                    if (!category.isEmpty()) {
                        model.addAttribute("search_item", itemsRepository.findByTitleOrderByPrice(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max)));
                    }

                } else if (price.equals("sorted_by_descending_price")) {

                    if (!category.isEmpty()) {

                        if (category.equals("animal")) {
                            model.addAttribute("search_item", itemsRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max), 1));
                        } else if (category.equals("feed")) {
                            model.addAttribute("search_item", itemsRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max), 2));
                        } else if (category.equals("terrarium")) {
                            model.addAttribute("search_item", itemsRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max), 3));
                        } else if (category.equals("equipment")) {
                            model.addAttribute("search_item", itemsRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max), 4));
                        } else if (category.equals("decor")) {
                            model.addAttribute("search_item", itemsRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max), 5));
                        }

                    } else {
                        model.addAttribute("search_item", itemsRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max)));
                    }
                }
            } else {
                model.addAttribute("search_item", itemsRepository.findByTitleAndPriceGreaterThenEqualAndPriceLessThen(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max)));
            }

        } else {
            model.addAttribute("search_item", itemsRepository.findByTitleContainingIgnoreCase(search));
        }
        model.addAttribute("value_search", search);
        model.addAttribute("value_min", min);
        model.addAttribute("value_max", max);
        model.addAttribute("value_price", price);
        model.addAttribute("value_category", category);

        return "item/item";
    }
}
