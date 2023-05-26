package com.example.Backend.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;

    private int count;

    private float price;

    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    private Item item;

    @ManyToOne(optional = false)
    private User user;

    @PrePersist
    private void init(){
        dateTime = LocalDateTime.now();
    }

    public Order(String number, int count, float price, Item item, User user) {
        this.number = number;
        this.count = count;
        this.price = price;
        this.item = item;
        this.user = user;
    }
}
