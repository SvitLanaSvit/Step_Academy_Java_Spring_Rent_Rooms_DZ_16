package com.example.dz_16_spring_book.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApartmentRental {
    private Long id;
    private String address;
    private Double rentAmount;
    private int bedrooms;
    private String description;
    private String district;
    private boolean isRented;
    List<String> linkImages;

    public void addToListImages(String linkImage){
        if(linkImages == null){
            linkImages = new ArrayList<>();
            linkImages.add(linkImage);
        }else{
            linkImages.add(linkImage);
        }
    }

    public List<String> getAllListImages(){
        if(linkImages != null){
            return linkImages;
        }
        return new ArrayList<>();
    }
}
