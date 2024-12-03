package ewm.category.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.category.AdminCategoryParam;
import ewm.category.Category;
import ewm.category.CategoryRepository;
import ewm.category.PublicCategoryParam;
import ewm.category.QCategory;
import ewm.event.model.Event;
import ewm.event.model.QEvent;
import ewm.event.repository.EventRepository;
import ewm.exception.ConditionsNotRespected;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private static final String CATEGORY_ALREADY_EXIST = "Ошибка при работе с категориями: Категория %s " +
            "уже существует !";

    private static final String NOT_EXISTING_CATEGORY = "Ошибка при работе с категориями: Категория по id %s " +
            ", которую вы пытаетесь изменить не существует !";

    private static final String CATEGORY_IS_INDICATED_IN_EVENTS = "Ошибка при работе с категориями: Категория по id %s " +
            ", которую вы пытаетесь удалить используется в событиях !";


    public Category create(Category category) {

        Optional<Category> categoryOpt = categoryRepository.getByName(category.getName());

        if (categoryOpt.isPresent()) {
            throw new ConditionsNotRespected(String.format(CATEGORY_ALREADY_EXIST, category.getName()));
        }
        return categoryRepository.save(category);
    }

    public Category delete(AdminCategoryParam adminCategoryParam) {

        Long categoryId = adminCategoryParam.getCatId();

        Optional<Category> categoryOpt = categoryRepository.getByCategoryId(categoryId);

        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(String.format(NOT_EXISTING_CATEGORY, categoryId));
        }

        QEvent qEvent = QEvent.event;
        BooleanExpression categoryIdQ = qEvent.category.id.eq(categoryId);

        Iterable<Event> eventIterable = eventRepository.findAll(categoryIdQ);
        List<Event> eventList = new ArrayList<>();
        eventIterable.forEach(eventList::add);

        if (!eventList.isEmpty()) {
            throw new ConditionsNotRespected(String.format(CATEGORY_IS_INDICATED_IN_EVENTS, categoryId));
        }

        categoryRepository.deleteById(categoryId);
        return categoryOpt.get();
    }

    public Category update(AdminCategoryParam adminCategoryParam) {

        Long requestedCategoryId = adminCategoryParam.getCatId();
        String newCategoryName = adminCategoryParam.getCategory().getName();

        Optional<Category> categoryOptGotById = categoryRepository.getByCategoryId(requestedCategoryId);

        if (categoryOptGotById.isEmpty()) {
            throw new NotFoundException(String.format(NOT_EXISTING_CATEGORY, requestedCategoryId));
        }

        Optional<Category> categoryOptGotByName = categoryRepository.getByName(newCategoryName);

        if (categoryOptGotByName.isPresent() && !categoryOptGotByName.get().getId().equals(requestedCategoryId)) {
            throw new ConditionsNotRespected(String.format(CATEGORY_ALREADY_EXIST, newCategoryName));
        }

        return categoryRepository.save(adminCategoryParam.getCategory());
    }

    @Override
    public List<Category> getAll(PublicCategoryParam publicCategoryParam) {
        QCategory qCategory = QCategory.category;

        BooleanExpression filter = qCategory.id.gt(publicCategoryParam.getFrom());

        Pageable pageable = PageRequest.of(0, publicCategoryParam.getSize());

        Iterable<Category> categoriesIterable = categoryRepository.findAll(filter, pageable).getContent();
        List<Category> categoryList = new ArrayList<>();

        categoriesIterable.forEach(categoryList::add);
        return categoryList;
    }

    @Override
    public Category getCategoryById(Long categoryId) {

        Optional<Category> categoryOpt = categoryRepository.getByCategoryId(categoryId);

        if (categoryOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_CATEGORY);
        }
        return categoryOpt.get();
    }


}
