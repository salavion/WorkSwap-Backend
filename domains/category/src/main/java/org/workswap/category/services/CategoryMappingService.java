package org.workswap.category.services;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.category.datasource.Category;
import org.workswap.category.dto.CategoryDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class CategoryMappingService {
    
    public CategoryDTO toDTO(Category category) {
        Long parentId = category.getParent() != null
                ? category.getParent().getId()
                : null;

        return new CategoryDTO(
            category.getId(),
            category.getName(),
            parentId,
            category.isLeaf()
        );
    }

    public List<CategoryDTO> toDTOList(Collection<? extends Category> categories) {
        return categories.stream().map(listing -> toDTO(listing)).toList();
    }
}
