package ewm.category.service;

import ewm.category.AdminCategoryParam;
import ewm.category.Category;
import ewm.category.PublicCategoryParam;

import java.util.List;

public interface CategoryService {

    Category create(Category category);

    Category update(AdminCategoryParam adminCategoryParam);

    Category delete(AdminCategoryParam adminCategoryParam);

    List<Category> getAll(PublicCategoryParam publicCategoryParam);

    Category getCategoryById(Long catId);
}
