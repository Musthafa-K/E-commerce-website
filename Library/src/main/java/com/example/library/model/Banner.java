package com.example.library.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long id;

    private String name;

    private String bannerFile;

    private String description_1;

    private String description_2;

    private String description_3;

    @OneToOne
    @JoinColumn(name = "product_id",referencedColumnName = "product_id")
    private Product product;

    private boolean enabled;
}