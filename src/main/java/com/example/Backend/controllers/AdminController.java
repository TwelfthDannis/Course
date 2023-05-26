package com.example.Backend.controllers;

import com.example.Backend.models.Image;
import com.example.Backend.models.User;
import com.example.Backend.models.Item;
import com.example.Backend.repositories.CategoryRepository;
import com.example.Backend.repositories.OrderRepository;
import com.example.Backend.repositories.UserRepository;
import com.example.Backend.security.UserDetails;
import com.example.Backend.services.OrderService;
import com.example.Backend.services.UserService;
import com.example.Backend.services.ItemsService;
import com.example.Backend.util.ItemValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Value("/C:/Users/danya/uploads")
    private String uploadPath;

    private final ItemValidator itemValidator;

    private final ItemsService itemsService;

    private final CategoryRepository categoryRepository;
    private final OrderService orderService;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public AdminController(ItemValidator itemValidator, ItemsService itemsService, CategoryRepository categoryRepository, OrderService orderService, OrderRepository orderRepository, UserRepository userRepository, UserService userService) {
        this.itemValidator = itemValidator;
        this.itemsService = itemsService;
        this.categoryRepository = categoryRepository;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping()
    public String admin(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role = userDetails.getUser().getRole();

        if (role.equals("ROLE_USER")){
            return "redirect:/index";
        }
        model.addAttribute("item", itemsService.getAllItem());
        return "admin/admin";
    }

    @GetMapping("item/add")
    public String addItem(Model model){
        model.addAttribute("item", new Item());
        model.addAttribute("category", categoryRepository.findAll());
        return "item/addItem";
    }

    @PostMapping("/item/add")
    public String addItem(@ModelAttribute("item") @Valid Item item, BindingResult bindingResult, @RequestParam("file_one")MultipartFile file_one, @RequestParam("file_two")MultipartFile file_two) throws IOException {


        itemValidator.validate(item, bindingResult);

        if (bindingResult.hasErrors()){
            return "item/addItem";
        }

        if (file_one != null){
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setItem(item);
            image.setFileName(resultFileName);
            item.addImageItem(image);

        }


        if (file_two != null){
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
            file_two.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setItem(item);
            image.setFileName(resultFileName);
            item.addImageItem(image);

        }


        itemsService.saveItem(item);
        return "redirect:/admin";
    }




    @GetMapping("item/edit/{id}")
    public String editItem(@PathVariable("id") int id, Model model){
        model.addAttribute("editItem", itemsService.getItemId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "item/editItem";
    }

    @PostMapping("/item/edit/{id}")
    public String editItem(@ModelAttribute("editItem") Item item, @PathVariable("id") int id){
        itemsService.updateItem(id, item);
        return "redirect:/admin";
    }

    @GetMapping("/item/delete/{id}")
    public String deleteItem(@PathVariable("id") int id){
        itemsService.deleteItem(id);
        return "redirect:/admin";
    }


    @GetMapping("orderList")
    public String ordersUsers(Model model){
        model.addAttribute("ordersList", orderRepository.findAll());
        model.addAttribute("useralItem", itemsService.getAllItem());
        return "/admin/orderList";
    }


    @GetMapping("userList")
    public String userList(Model model, Principal principal) {
        List<User> users = userRepository.findAll();
        users.removeIf(user -> user.getLogin().equals(principal.getName()));
        model.addAttribute("userList", users);
        return "/admin/userList";
    }

    @PostMapping("userList")
    public String editStatus(@RequestParam("editRole") String roles,@RequestParam Integer userId){
        Optional<User> change=userRepository.findById(userId);
        User user=change.get();
        user.setRole(roles);
        userRepository.save(user);
        return "redirect:/admin/userList";
    }
}
