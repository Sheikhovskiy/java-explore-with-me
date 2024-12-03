package ewm.event.repository;


import ewm.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface LocationRepository extends JpaRepository<Location, Long>,
        QuerydslPredicateExecutor<Location> {
}
