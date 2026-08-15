package org.workswap.listing.controllers.category;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.workswap.category.datasource.Category;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.category.services.CategoryFacade;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractCategoryController<T extends Category> {

    protected final CategoryFacade<T> categoryService;

    @GetMapping("/all")
    @PermitAll
    public List<CategoryDTO> categoryList() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{parentId}/children")
    @PermitAll
    public List<CategoryDTO> getChildCategories(@PathVariable Long parentId, Locale locale) {
        return categoryService.toDTOList(
                    categoryService.getChildCategories(parentId)
                );
    }

    @GetMapping("/{categoryId}/is-leaf")
    @PermitAll
    public boolean isLeafCategory(@PathVariable Long categoryId) {
        return categoryService.isLeafCategory(categoryId);
    }

    @GetMapping("/{categoryId}/path")
    @PermitAll
    public List<CategoryDTO> getCategoryPath(@PathVariable Long categoryId, Locale locale) {
        return categoryService.toDTOList(
                    categoryService.getCategoryPath(categoryId)
                );
    }

    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    @PostMapping
    public void createCategory(@RequestBody CategoryDTO dto) {
        categoryService.createCategory(dto);
    }

    @PreAuthorize("hasAuthority('DELETE_CATEGORY')")
    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
