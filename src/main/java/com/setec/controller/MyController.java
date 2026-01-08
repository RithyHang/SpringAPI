package com.setec.controller;

import com.setec.entities.PostProductDAO;
import com.setec.entities.Product;
import com.setec.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class MyController {
    @Autowired
    private ProductRepo productRepo;

    //http://localhost:8080/swagger-ui/index.html

    @GetMapping
    public Object getAll(){
        var products = productRepo.findAll();
        if (products.isEmpty()){
            return ResponseEntity.status(404).body(Map.of("message", "Product is empty"));
        }
        return productRepo.findAll();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(@ModelAttribute PostProductDAO product)
    throws Exception{
        String uploadDir = new File("myApp/static").getAbsolutePath();
        File dir = new File(uploadDir);
        if(!dir.exists()){
            dir.mkdirs();
        }

        var file = product.getFile();
        String uniqueName = UUID.randomUUID()+"_"+file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, uniqueName).toString();

        file.transferTo(new File(filePath));

        var pro = new Product();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setQty(product.getQty());
        pro.setImageUrl("/static/"+uniqueName);

        productRepo.save(pro);

        return ResponseEntity.status(201).body(pro);
    }
}
