package com.example.library.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.cache.CacheType;

import java.util.Collection;
import java.util.List;

@Getter
@Setter

@EqualsAndHashCode
@Entity
@Table(name="customers",uniqueConstraints = @UniqueConstraint(columnNames ="username" ))
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_id")
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;
    @Column(name = "is_blocked")
    private boolean isBlocked;


     @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL)
      private ShoppingCart cart;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
     @JoinTable(name = "customer_role",joinColumns = @JoinColumn(name = "customer_id",referencedColumnName = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName ="role_id"))
    private Collection<Role> roles;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Address> addresses;


    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<Order>orders;

    @OneToOne(mappedBy = "customer")
    private Wallet wallet;

    public Customer(){
        this.cart=new ShoppingCart();
    }



    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", roles=" + roles +
                //", cart=" + cart.getId() +
                //", cart=" + addresses +
                '}';
    }

//    public void setAddresses(String addresses) {
//        this.addresses=addresses;
//    }
}
