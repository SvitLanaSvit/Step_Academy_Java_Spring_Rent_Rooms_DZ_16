package com.example.dz_16_spring_book.controllers;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.models.PublicAccessType;
import com.example.dz_16_spring_book.dto.ApartmentImageInfo;
import com.example.dz_16_spring_book.models.Apartment;
import com.example.dz_16_spring_book.models.ApartmentImage;
import com.example.dz_16_spring_book.repositories.ApartmentImageRepository;
import com.example.dz_16_spring_book.repositories.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class AdminApartmentImageController {
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());
    @Autowired
    private ApartmentImageRepository apartmentImageRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;

    private BlobServiceClient client;
    private BlobContainerClient container;

    public AdminApartmentImageController(){
        BlobServiceClientBuilder builder = new BlobServiceClientBuilder();
        client = builder.connectionString("UseDevelopmentStorage=true").buildClient();

        //ВАЖНО!!!!
        //azurite --silent --location c:\azurite --debug c:\azurite\debug.log --skipApiVersionCheck
        //Запускать Azurite ИМЕННО с этой команды, иначе выдаст ошибку
        //ВАЖНО!!!!
        BlobSignedIdentifier identifier = new BlobSignedIdentifier().setId("test policy")
                .setAccessPolicy(new BlobAccessPolicy().setStartsOn(OffsetDateTime.now())
                        .setExpiresOn(OffsetDateTime.now().plusDays(7))
                        .setPermissions("cd")); //permission for create and delete

        ArrayList<BlobSignedIdentifier> identifiers = new ArrayList<>();
        identifiers.add(identifier);
        container = client.createBlobContainerIfNotExists("apartmentimages");
        container.setAccessPolicy(PublicAccessType.CONTAINER, identifiers);
    }

    @GetMapping("/admin/apartmentImages")
    public String getAllApartmentImages(Model model){
        List<ApartmentImage> apartmentImages = getAllApartmentImages();
        List<Apartment> apartments = getAllApartments();
        List<ApartmentImageInfo> apartmentImageInfos = new ArrayList<>();

        for(var apartmentImage : apartmentImages){
           ApartmentImageInfo apartmentImageInfo = new ApartmentImageInfo();
           apartmentImageInfo.setId(apartmentImage.getId());
           apartmentImageInfo.setImageLink(apartmentImage.getLinkImage());
            if(apartmentImage.getApartment() != null){
                for (var apartment : apartments) {
                    if(apartment.getId().equals(apartmentImage.getApartment().getId())){
                        apartmentImageInfo.setApartmentInfo(apartment.getDistrict().getNameDistrict() + ", " +
                                apartment.getAddress() + ", " + apartment.getBedrooms() + ", " + apartment.getRentAmount());
                    }
                }
            }
            apartmentImageInfos.add(apartmentImageInfo);
        }

        model.addAttribute("images", apartmentImageInfos);
        return "adminApartmentImages";
    }

    @GetMapping("/apartments/newImage")
    public String getNewApartmentImage(Model model){
        List<Apartment> apartments = getAllApartments();
        model.addAttribute("apartments", apartments);
        return "newApartmentImage";
    }

    @PostMapping("/apartments/newImage")
    public String saveNewApartmentImage(
            @RequestParam Long apartment,
            @RequestParam MultipartFile linkImage,
            Model model){
        ApartmentImage apartmentImage = new ApartmentImage();
        Apartment apartmentFromDB = apartmentRepository.findById(apartment).orElse(null);
        apartmentImage.setApartment(apartmentFromDB);

        try (var stream = linkImage.getInputStream()) {
            BlobClient cl = container.getBlobClient(linkImage.getOriginalFilename());
            cl.upload(stream, stream.available(), true);
            apartmentImage.setLinkImage(cl.getBlobUrl()); //if I work with blob and BD
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/error";
        }

        apartmentImageRepository.save(apartmentImage);
        return "redirect:/admin/apartmentImages";
    }

    @GetMapping("/apartmentImage/{id}/update")
    public String getApartmentImageUpdate(@PathVariable(value = "id")Long id, Model model){
        ApartmentImage apartmentImage = apartmentImageRepository.findById(id).orElse(null);
        List<Apartment> apartments = getAllApartments();
        if(apartmentImage != null){
            model.addAttribute("image", apartmentImage);
        }
        model.addAttribute("apartments", apartments);
        return "updateApartmentImage";
    }

    @PostMapping("/apartmentImage/{id}/update")
    public String saveApartmentImageUpdate(@PathVariable(value = "id")Long id,
                                           @RequestParam Long apartment,
                                           @RequestParam MultipartFile linkImage,
                                           Model model){
        ApartmentImage apartmentImage = apartmentImageRepository.findById(id).orElse(null);
        Apartment apartmentFromDB = apartmentRepository.findById(apartment).orElse(null);
        if(apartmentImage != null){
            if(apartmentFromDB != null){
                if(!apartmentFromDB.equals(apartmentImage.getApartment())){
                    apartmentImage.setApartment(apartmentFromDB);
                }
            }

            if(linkImage != null){
                BlobClient cl = container.getBlobClient((apartmentImage.getLinkImage()));
                cl.deleteIfExists();
                BlobClient clUpdate = container.getBlobClient(linkImage.getOriginalFilename());
                try(var stream = linkImage.getInputStream()){
                    clUpdate.upload(stream, stream.available(), true);
                    apartmentImage.setLinkImage(cl.getBlobUrl()); //if I work with blob and BD
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            apartmentImageRepository.save(apartmentImage);
        }
        return "redirect:/admin/apartmentImages";
    }

    @GetMapping("/apartmentImage/{id}/remove")
    public String removeApartmentImage(@PathVariable(value = "id")Long id, Model model){
        ApartmentImage apartmentImage = apartmentImageRepository.findById(id).orElse(null);
        if(apartmentImage != null){
            BlobClient cl = container.getBlobClient(apartmentImage.getLinkImage());
            cl.deleteIfExists();
            apartmentImageRepository.delete(apartmentImage);
        }
        return "redirect:/admin/apartmentImages";
    }

    private List<Apartment> getAllApartments(){
        List<Apartment> apartments = new ArrayList<>();
        apartmentRepository.findAll().forEach(item -> apartments.add(item));
        return apartments;
    }

    private List<ApartmentImage> getAllApartmentImages(){
        List<ApartmentImage> apartmentImages = new ArrayList<>();
        apartmentImageRepository.findAll().forEach(item -> apartmentImages.add(item));
        return apartmentImages;
    }
}