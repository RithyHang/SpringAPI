package com.setec.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutProductDAO {
    private Integer id;
    private String name;
    private double price;
    private int qty;
    private MultipartFile file;
}
