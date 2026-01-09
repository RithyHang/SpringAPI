package com.setec.controller;

import com.setec.entities.PostProductDAO;
import com.setec.entities.Product;
import com.setec.entities.PutProductDAO;
import com.setec.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@ModelAttribute PutProductDAO product) throws Exception{
        var pro = productRepo.findById(product.getId());
        if(pro.isPresent()){
            var update = pro.get();

            update.setName(product.getName());
            update.setPrice(product.getPrice());
            update.setQty(product.getQty());

            if(product.getFile()!=null){
                String uploadDir = new File("myApp/static").getAbsolutePath();
                File dir = new File(uploadDir);
                if(!dir.exists()){
                    dir.mkdirs();
                }

                var file = product.getFile();
                String uniqueName = UUID.randomUUID()+"_"+file.getOriginalFilename();
                String filePath = Paths.get(uploadDir, uniqueName).toString();

                new File("myApp/"+update.getFullImageUrl()).delete();

                file.transferTo(new File(filePath));
                update.setImageUrl("/static/"+uniqueName);
            }

            productRepo.save(update);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Product has been updated successfully!", "product", update));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product id = " + product.getId() + " not found"));

    }


    @GetMapping({"/{id}","/id/{id}"})
    public ResponseEntity<?> getById(@PathVariable("id") Integer id){
        var product = productRepo.findById(id);
        if (product.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(product.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product id = " + id + " not found"));
    }

    @DeleteMapping({"/{id}","/id/{id}"})
    public ResponseEntity<?> deleteById(@PathVariable("id") Integer id){
        var product = productRepo.findById(id);
        if (product.isPresent()){
            new File("myApp/"+product.get().getImageUrl()).delete();
            productRepo.delete(product.get()) ;

            productRepo.delete(product.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Product id = " + id + " has been deleted successfully!"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product id = " + id + " not found"));
    }
}
