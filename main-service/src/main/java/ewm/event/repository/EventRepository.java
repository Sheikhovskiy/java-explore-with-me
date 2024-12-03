package ewm.event.repository;

import ewm.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event> {

    Event save(Event event);


    Optional<Event> findById(Long id);


    @Query(value = "select ev " +
            "from Event as ev " +
            "where ev.id in ?1"
    )
    List<Event> getAllByIds(List<Long> ids);


    Optional<Event> getEventById(Long id);


}

