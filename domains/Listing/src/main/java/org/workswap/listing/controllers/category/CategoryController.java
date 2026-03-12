package org.workswap.listing.controllers.category;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.category.services.CategoryMappingService;
import org.workswap.listing.datasource.repository.category.ProductCategoryRepository;
import org.workswap.listing.datasource.repository.category.ServiceCategoryRepository;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    
    private final CategoryMappingService categoryMappingService;

    //перенести сервис в сервис
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    
    @GetMapping("/all")
    @PermitAll
    public Map<String, List<CategoryDTO>> categoryList() {

        List<CategoryDTO> sCategories = categoryMappingService.toDTOList(serviceCategoryRepository.findAll());
        List<CategoryDTO> pCategories = categoryMappingService.toDTOList(productCategoryRepository.findAll());

        return Map.of("PRODUCT", pCategories, "SERVICE", sCategories);
    }
}