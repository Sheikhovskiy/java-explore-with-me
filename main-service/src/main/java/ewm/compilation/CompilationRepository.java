package ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long>,
        QuerydslPredicateExecutor<Compilation> {


    Compilation save(Compilation compilation);

    @Query(value = "select cp " +
            "from Compilation as cp " +
            "where cp.id = ?1"
    )
    Optional<Compilation> getCompilationById(Long id);


}
