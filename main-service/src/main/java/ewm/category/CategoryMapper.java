package ewm.category;

import ewm.category.dto.CategoryDto;
import ewm.category.dto.NewCategoryDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {

    public Category toCategoryFromNewCategoryDto(NewCategoryDto newCategoryDto) {

        Category category = new Category();

        category.setName(newCategoryDto.getName());
        return category;
    }

    public CategoryDto toCategoryDtoFromCategory(Category category) {

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public List<CategoryDto> toListCategoryDtoFromListCategory(List<Category> categoryList) {

        return categoryList.stream()
                .map(CategoryMapper::toCategoryDtoFromCategory)
                .collect(Collectors.toList());


    }


}
