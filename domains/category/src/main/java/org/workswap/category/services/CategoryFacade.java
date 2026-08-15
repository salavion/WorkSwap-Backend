package org.workswap.category.services;

import java.util.Collection;
import java.util.List;

import org.workswap.category.datasource.Category;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.category.dto.CategoryDTO;

public abstract class CategoryFacade<T extends Category> {

    private final CategoryCommandService<T> commandService;
    private final CategoryQueryService<T> queryService;
    private final CategoryMappingService mappingService;

    public CategoryFacade(CategoryRepository<T> repository,
                          CategoryFactory<T> factory,
                          CategoryQueryService<T> queryService,
                          CategoryMappingService mappingService) {
        this.commandService = new CategoryCommandService<T>(repository, factory) {
            // если нужны override методы, можно тут
        };
        this.queryService = queryService;
        this.mappingService = mappingService;
    }

    // --- Общие команды ---

    public void createCategory(CategoryDTO dto) {
        commandService.createCategory(dto);
    }

    public void deleteCategory(Long id) {
        commandService.deleteCategory(id);
    }

    // --- Общие запросы ---

    public List<T> getLeafCategories() {
        return queryService.getLeafCategories();
    }

    public List<T> getChildCategories(Long parentId) {
        return queryService.getChildCategories(parentId);
    }

    public List<T> getRootCategories() {
        return queryService.getRootCategories();
    }

    public List<T> getAllDescendants(T parent) {
        return queryService.getAllDescendants(parent);
    }

    public List<T> getAllDescendantsById(Long parentId) {
        return queryService.getAllDescendantsById(parentId);
    }

    public List<T> getCategoryPath(Long categoryId) {
        return queryService.getCategoryPath(categoryId);
    }

    public boolean isLeafCategory(Long categoryId) {
        return queryService.isLeafCategory(categoryId);
    }

    public List<CategoryDTO> getAllCategories() {
        return queryService.getAllCategories();
    }

    // --- Маппинг DTO ---

    public CategoryDTO toDTO(T category) {
        return mappingService.toDTO(category);
    }

    public List<CategoryDTO> toDTOList(Collection<T> categories) {
        return mappingService.toDTOList(categories);
    }
}