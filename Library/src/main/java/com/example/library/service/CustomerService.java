package com.example.library.service;

import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Customer;

import java.util.List;

public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    Customer findByUsername(String username);

      void blockById(long id);

    void unblockById(long id);

    Customer update(CustomerDto customerDto);
//
Customer changePass(Customer customer);
    CustomerDto getCustomer(String username);

    List<CustomerDto> findAll();
    CustomerDto findByEmailCustomerDto(String email);
    CustomerDto updateAccount(CustomerDto customerDto,String email);

    Customer findById(long id);
    void save(Customer customer, Address address);
}
