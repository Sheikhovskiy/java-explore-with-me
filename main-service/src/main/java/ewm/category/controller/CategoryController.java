package ewm.category.controller;

import ewm.category.Category;
import ewm.category.CategoryMapper;
import ewm.category.PublicCategoryParam;
import ewm.category.dto.CategoryDto;
import ewm.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {

        log.info("PUBLIC: Получен запрос на получение всех категорий");

        PublicCategoryParam publicCategoryParam = new PublicCategoryParam();
        publicCategoryParam.setFrom(from);
        publicCategoryParam.setSize(size);

        List<Category> categoryList = categoryService.getAll(publicCategoryParam);
        List<CategoryDto> categoryDtoList = CategoryMapper.toListCategoryDtoFromListCategory(categoryList);
        log.info("PUBLIC: Получен список всех категорий от {} до {}", from, size);
        return categoryDtoList;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {

        log.info("PUBLIC: Получен запрос по получению категории по id {}", catId);

        Category category = categoryService.getCategoryById(catId);
        CategoryDto categoryDto = CategoryMapper.toCategoryDtoFromCategory(category);
        log.info("PUBLIC: Получена категория {} по id {}", categoryDto, catId);
        return categoryDto;
    }




}
