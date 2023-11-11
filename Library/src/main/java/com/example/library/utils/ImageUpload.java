package com.example.library.utils;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class ImageUpload {

String rootPath = System.getProperty("user.home");

    String UPLOAD_FOLDER = rootPath + File.separator + "Desktop" + File.separator + "Week 11" + File.separator + "Pursify-project" + File.separator + "Admin" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "imgs" + File.separator + "imageproduct";

    String UPLOAD_FOLDER_CUSTOMER = rootPath + File.separator + "Desktop" + File.separator + "Week 11" + File.separator + "Pursify-project" + File.separator + "Customer" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "imgs" + File.separator + "imageproduct";



    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path uploadPath = Paths.get(UPLOAD_FOLDER);
        Path uploadPathCustomer = Paths.get(UPLOAD_FOLDER_CUSTOMER);


        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            Files.createDirectories(uploadPathCustomer);
        }

        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            Path filePath = uploadPath.resolve(fileName);
            Path filePathCustomer = uploadPathCustomer.resolve(fileName);
            Files.write(filePath,buffer, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(filePathCustomer,buffer, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);

            return fileName.toString();
        } catch (IOException e) {
            throw new IOException("Could not store file " + fileName, e);
        }
    }
    public boolean checkExist(MultipartFile File){
        boolean isExist = false;
        try {
            File file = new File(UPLOAD_FOLDER +"\\" + File.getOriginalFilename());
            isExist = file.exists();
        }catch (Exception e){
            e.printStackTrace();
        }
        return isExist;
    }


}
