package com.example.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    @Column(columnDefinition = "Text")
    private String description;
    private int currentQuantity;
    private double costPrice;
    private double salePrice;
    @OneToMany(mappedBy="product",cascade = CascadeType.ALL)
    private List<Image>images;
    @ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="category_id",referencedColumnName = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product")
    private Banner banner;

    private boolean is_activated;
    private boolean is_deleted;
}
