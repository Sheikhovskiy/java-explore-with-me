package ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>,
        QuerydslPredicateExecutor<Category> {

    Category save(Category category);

    Optional<Category> getByName(String name);

    @Query(value = "select cg " +
            "from Category as cg " +
            "where cg.id = ?1")
    Optional<Category> getByCategoryId(Long id);




}
