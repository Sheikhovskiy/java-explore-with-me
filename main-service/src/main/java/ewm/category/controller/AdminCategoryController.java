package ewm.category.controller;

import ewm.category.AdminCategoryParam;
import ewm.category.Category;
import ewm.category.CategoryMapper;
import ewm.category.dto.CategoryDto;
import ewm.category.dto.NewCategoryDto;
import ewm.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {

        log.info("ADMIN: Получен запрос на создание категории {}", newCategoryDto);
        Category category = categoryService.create(CategoryMapper.toCategoryFromNewCategoryDto(newCategoryDto));
        CategoryDto categoryDto = CategoryMapper.toCategoryDtoFromCategory(category);
        log.info("ADMIN: Создана новая категория {}", categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CategoryDto delete(@PathVariable Long catId) {

        log.info("ADMIN: Получен запрос на удаление категории по id {}", catId);

        AdminCategoryParam adminCategoryParam = new AdminCategoryParam();
        adminCategoryParam.setCatId(catId);

        Category category = categoryService.delete(adminCategoryParam);
        CategoryDto categoryDto = CategoryMapper.toCategoryDtoFromCategory(category);
        log.info("ADMIN: Категория {} успешно удалена !", categoryDto);
        return categoryDto;
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @RequestBody @Valid NewCategoryDto newCategoryDto) {

        log.info("ADMIN: Получен запрос на изменение категории по id {} на категорию {}", catId, newCategoryDto);

        AdminCategoryParam adminCategoryParam = new AdminCategoryParam();
        adminCategoryParam.setCatId(catId);
        adminCategoryParam.setCategory(CategoryMapper.toCategoryFromNewCategoryDto(newCategoryDto));

        Category category = categoryService.update(adminCategoryParam);
        CategoryDto categoryDto = CategoryMapper.toCategoryDtoFromCategory(category);
        log.info("ADMIN: Категория {} успешно изменена !", categoryDto);
        return categoryDto;
    }




}
