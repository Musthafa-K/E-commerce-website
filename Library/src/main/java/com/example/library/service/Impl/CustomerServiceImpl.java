package com.example.library.service.Impl;

import com.example.library.dto.CustomerDto;
import com.example.library.model.Address;
import com.example.library.model.Customer;
import com.example.library.repository.CustomerRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.service.CustomerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,RoleRepository roleRepository){
        this.customerRepository=customerRepository;
        this.roleRepository=roleRepository;
    }
    @Override
    public CustomerDto save(CustomerDto customerDto){
        Customer customer=new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPassword(customerDto.getPassword());
        customer.setUsername(customerDto.getUsername());
        customer.setPhoneNumber(customer.getPhoneNumber());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        Customer customerSave=customerRepository.save(customer);
        return mapperDto(customerSave);
    }
    @Override
    public Customer findById(long id) {
        System.out.println("Customer findBy id");
        return customerRepository.findById(id);
    }
    @Override
    public Customer findByUsername(String username){
        return customerRepository.findByUsername(username);
    }
    @Override
    public CustomerDto getCustomer(String username){
        CustomerDto customerDto=new CustomerDto();
        Customer customer=customerRepository.findByUsername(username);
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
       // customerDto.setAddresses(customer.getAddresses().toString());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
         return customerDto;
    }

//    @Override
//    public Customer changePass(CustomerDto customerDto){
//        Customer customer=customerRepository.findByUsername(customerDto.getUsername());
//        customer.setPassword(customer.getPassword());
//        return customerRepository.save(customer);
//    }


    @Override
    public Customer changePass(Customer customer){
        Customer username=customerRepository.findByUsername(customer.getUsername());
        username.setPassword(username.getPassword());
        return customerRepository.save(username);
    }
    @Override
    public Customer update(CustomerDto dto) {
        Customer customer = customerRepository.findByUsername(dto.getUsername());
       // customer.setAddresses(dto.getAddresses());
        customer.setPhoneNumber(dto.getPhoneNumber());
        return customerRepository.save(customer);
    }
    @Override
    public CustomerDto updateAccount(CustomerDto customerDto,String email) {
        Customer customer= findByUsername(email);
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customerRepository.save(customer);
        CustomerDto customerDtoUpdated = convertEntityToDto(customer);
        return customerDtoUpdated;
    }
    public CustomerDto convertEntityToDto(Customer customer){
        CustomerDto customerDto=new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setBlocked(customer.isBlocked());
        customerDto.setPassword(customer.getPassword());

        return customerDto;
    }

    @Override
    public CustomerDto findByEmailCustomerDto(String email) {
        Customer customer = customerRepository.findByUsername(email);
        CustomerDto customerDto=new CustomerDto();
        customerDto.setUsername(customer.getUsername());
        customerDto.setId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setAddresses(customer.getAddresses().toString());
        customerDto.setPassword(customer.getPassword());
        customerDto.setBlocked(customer.isBlocked());

        return customerDto;
    }


    @Override
    public List<CustomerDto> findAll() {
        List<CustomerDto>  customerDtoList = new ArrayList<>();
        List<Customer> customers = customerRepository.findAll();
        for(Customer customer:customers){
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(customer.getId());
            customerDto.setFirstName(customer.getFirstName());
            customerDto.setLastName(customer.getLastName());
            customerDto.setUsername(customer.getUsername());
            customerDto.setPhoneNumber(customer.getPhoneNumber());
           // customerDto.setBlocked(customer.is_blocked());
            customerDtoList.add(customerDto);
        }
        return customerDtoList;
    }

    @Override
    public void save(Customer customer, Address address) {

    }
   /* @Override
    public void blockUser(Long id) {
        Customer customer = customerRepository.getReferenceById(id);
        if (customer.is_blocked()){
            customer.set_blocked(false);
        }
        else customer.set_blocked(true);


    }*/

    private CustomerDto mapperDto(Customer customer){
        CustomerDto customerDto=new CustomerDto();
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customerDto.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        return customerDto;
    }
//    @Override
//    public void blockUser(Long id){
//        Customer customer=customerRepository.getReferenceById(id);
//        if(customer.is_blocked()){
//            customer.set_blocked(false);
//        }else{
//            customer.set_blocked(true);
//        }
//        customerRepository.save(customer);
//    }

    @Override
    public void blockById(long id) {


        Customer customer=customerRepository.findById(id);
        customer.setBlocked(true);
        customerRepository.save(customer);


    }

    @Override
    public void unblockById(long id) {

        Customer customer=customerRepository.findById(id);
        customer.setBlocked(false);
        customerRepository.save(customer);

    }

}
