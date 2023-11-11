package com.example.library.service;

import com.example.library.dto.BannerDto;
import com.example.library.model.Banner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {

    Banner save(BannerDto bannerDto, MultipartFile bannerImage);

    List<BannerDto> getAllBanners();

    BannerDto findById(long id);

    Banner update(BannerDto bannerDto,MultipartFile bannerImage);

    void disable(long id);

    void enable(long id);

    void deleteBanner(long id);
}