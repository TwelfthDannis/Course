package com.example.Backend.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "provider", nullable = false)
    private String provider;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "item")
    List<Image> imageList = new ArrayList<>();

    @ManyToOne(optional = false)
    private Category category;

    @ManyToMany()
    @JoinTable(name = "item_cart", joinColumns = @JoinColumn(name = "item_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> userList;


    @OneToMany(mappedBy = "item")
    private List<Order> orderList;

    private LocalDateTime dataTimeOfCreated;

    @PrePersist
    private void init(){
        dataTimeOfCreated = LocalDateTime.now();
    }

    public void addImageItem(Image image){
        image.setItem(this);
        imageList.add(image);
    }
}
