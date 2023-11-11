package com.example.library.dto;
import com.example.library.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto {

    private Long id;

    private String name;

    private String bannerFile;

    private String description_1;

    private String description_2;

    private String description_3;

    private Product product;

    private boolean enabled;


}
