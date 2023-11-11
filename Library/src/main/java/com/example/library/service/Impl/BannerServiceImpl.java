package com.example.library.service.Impl;

import com.example.library.dto.BannerDto;
import com.example.library.model.Banner;
import com.example.library.repository.BannerRepository;
import com.example.library.service.BannerService;
import com.example.library.utils.ImageUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private BannerRepository bannerRepository;

    private ImageUpload imageUpload;

    public BannerServiceImpl(BannerRepository bannerRepository,ImageUpload imageUpload) {
        this.imageUpload=imageUpload;
        this.bannerRepository = bannerRepository;
    }

    @Override
    public Banner save(BannerDto bannerDto, MultipartFile bannerImage) {
        Banner banner=new Banner();
        banner.setName(bannerDto.getName());
        banner.setDescription_1(bannerDto.getDescription_1());
        banner.setDescription_2(bannerDto.getDescription_2());
        banner.setDescription_3(bannerDto.getDescription_3());
        banner.setProduct(bannerDto.getProduct());
        banner.setEnabled(true);
        try {
            if (bannerImage == null) {
                banner.setBannerFile(null);
            } else {
                String fileName = imageUpload.uploadFile(bannerImage);
                banner.setBannerFile(fileName);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        bannerRepository.save(banner);


        return banner;
    }

    @Override
    public List<BannerDto> getAllBanners() {
        List<Banner> bannerList=bannerRepository.findAll();
        List<BannerDto>bannerDtoList = transferData(bannerList);
        return bannerDtoList;
    }

    @Override
    public BannerDto findById(long id) {
        Banner banner=bannerRepository.findById(id);
        BannerDto bannerDto=new BannerDto();
        bannerDto.setId(banner.getId());
        bannerDto.setName(banner.getName());
        bannerDto.setDescription_1(banner.getDescription_1());
        bannerDto.setDescription_2(banner.getDescription_2());
        bannerDto.setDescription_3(banner.getDescription_3());
        bannerDto.setProduct(banner.getProduct());
        bannerDto.setEnabled(banner.isEnabled());
        bannerDto.setBannerFile(banner.getBannerFile());



        return bannerDto;
    }

    @Override
    public Banner update(BannerDto bannerDto, MultipartFile bannerImage) {
        long banner_id=bannerDto.getId();
        Banner banner=bannerRepository.findById(banner_id);
        banner.setName(bannerDto.getName());
        banner.setDescription_1(bannerDto.getDescription_1());
        banner.setDescription_2(bannerDto.getDescription_2());
        banner.setDescription_3(bannerDto.getDescription_3());
        banner.setProduct(bannerDto.getProduct());
        try{
            if (!bannerImage.isEmpty()) {
                String fileName = imageUpload.uploadFile(bannerImage);
                banner.setBannerFile(fileName);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        bannerRepository.save(banner);
        return banner;
    }

    @Override
    public void disable(long id) {
        Banner banner=bannerRepository.findById(id);
        banner.setEnabled(false);
        bannerRepository.save(banner);
    }

    @Override
    public void enable(long id) {
        Banner banner=bannerRepository.findById(id);
        banner.setEnabled(true);
        bannerRepository.save(banner);

    }

    @Override
    public void deleteBanner(long id) {


        bannerRepository.deleteById(id);

    }

    public List<BannerDto> transferData(List<Banner> bannerList){
        List<BannerDto> BannerDtoList=new ArrayList<>();
        for(Banner banner : bannerList){
            BannerDto bannerDto=new BannerDto();
            bannerDto.setId(banner.getId());
            bannerDto.setName(banner.getName());
            bannerDto.setDescription_1(banner.getDescription_1());
            bannerDto.setDescription_2(banner.getDescription_2());
            bannerDto.setDescription_3(banner.getDescription_3());
            bannerDto.setBannerFile(banner.getBannerFile());
            bannerDto.setProduct(banner.getProduct());
            bannerDto.setEnabled(banner.isEnabled());

            BannerDtoList.add(bannerDto);

        }


        return BannerDtoList;
    }
}