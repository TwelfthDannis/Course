package com.example.Backend.controllers;

import com.example.Backend.models.Cart;
import com.example.Backend.models.Order;
import com.example.Backend.models.Item;
import com.example.Backend.repositories.CartRepository;
import com.example.Backend.repositories.OrderRepository;
import com.example.Backend.repositories.ItemsRepository;
import com.example.Backend.security.UserDetails;
import com.example.Backend.services.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    private final ItemsRepository itemsRepository;

    private final OrderRepository orderRepository;
    private final ItemsService itemsService;
    private final CartRepository cartRepository;

    @Autowired
    public UserController(ItemsRepository itemsRepository, OrderRepository orderRepository, ItemsService itemsService, CartRepository cartRepository) {
        this.itemsRepository = itemsRepository;
        this.orderRepository = orderRepository;
        this.itemsService = itemsService;
        this.cartRepository = cartRepository;
    }


    @GetMapping("/index")
    public String index(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role = userDetails.getUser().getRole();

        if (role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }
            model.addAttribute("items", itemsService.getAllItem());
            return "user/index";
    }

    @GetMapping("/cart")
    public String cart(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        int id_user = userDetails.getUser().getId();

        List<Cart> cartList = cartRepository.findByUserId((id_user));
        List<Item> itemList = new ArrayList<>();
        for (Cart cart: cartList
             ) {
            itemList.add(itemsService.getItemId(cart.getItemId()));
        }

        float price = 0;
        for (Item item: itemList
             ) {
            price += item.getPrice();
        }

        model.addAttribute("price", price);
        model.addAttribute("item_cart", itemList);
        return "user/cart";
    }

    @GetMapping("/info/{id}")
    public String infoItem(@PathVariable("id") int id, Model model){
        model.addAttribute("item", itemsService.getItemId(id));
        return "item/infoItem";
    }


    @GetMapping("/cart/add/{id}")
    public String addItemInCart(@PathVariable("id") int id){
        Item item = itemsService.getItemId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        int id_user = userDetails.getUser().getId();
        Cart cart = new Cart(id_user, item.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteItemInCart(@PathVariable("id") int item_id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        int user_id = userDetails.getUser().getId();
        System.out.println(item_id + "  "+ user_id);
        cartRepository.deleteCartById(user_id,item_id);
        return "redirect:/cart";
    }



    @GetMapping("/order/create")
    public String createOrder(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        int id_user = userDetails.getUser().getId();

        List<Cart> cartList = cartRepository.findByUserId((id_user));
        List<Item> itemList = new ArrayList<>();
        for (Cart cart: cartList
        ) {
            itemList.add(itemsService.getItemId(cart.getItemId()));
        }

        String uuid = UUID.randomUUID().toString();

        for (Item item: itemList
             ) {
            Order newOrder = new Order(uuid, 1, item.getPrice(), item, userDetails.getUser());
            orderRepository.save(newOrder);
            cartRepository.deleteCartById(item.getId(), id_user);
        }

        return "redirect:/orders";

    }

    @GetMapping("/orders")
    public String ordersUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByUser((userDetails.getUser()));
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }

    @PostMapping("index/search")
    public String itemSearch(@RequestParam("search") String search, @RequestParam("min") String min, @RequestParam("max") String max, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "category", required = false, defaultValue = "") String category, Model model){
        System.out.println(search);
        System.out.println(min);
        System.out.println(max);
        System.out.println(price);
        System.out.println(category);
        if (!min.isEmpty() & !max.isEmpty()) {
            if (!price.isEmpty()) {
                if (price.equals("sorted_by_ascending_price")) {
                        model.addAttribute("search_item", itemsRepository.findByTitleOrderByPrice(search.toLowerCase(), Float.parseFloat(min), Float.parseFloat(max)));
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
        return "user/index";
    }
}
