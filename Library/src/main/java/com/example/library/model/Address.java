package com.example.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Data
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private long id;
    private String state;
    private String addressLine1;
    private String addressLine2;
    private  String district;
    private String pin_code;
    private String addressType;
//    @Pattern(regexp = "^[0-9]+$", message = "String must contain only numeric characters")
    private String ContactNumber;
    private boolean Active= true;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "shippingAddress")
    private List<Order> orders;


}
