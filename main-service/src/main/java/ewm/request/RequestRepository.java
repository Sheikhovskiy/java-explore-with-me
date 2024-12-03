package ewm.request;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long>,
        QuerydslPredicateExecutor<Request> {

    Request save(Request request);

    @Query(value = "select case when count(rq) > 0 then true else false end " +
            "from Request as rq " +
            "where rq.requester.id = ?1 " +
            "and rq.event.id = ?2"
    )
    boolean requestExists(Long userId, Long eventId);


    Optional<Request> getRequestById(Long requestId);

    Optional<List<Request>> getRequestsByRequesterId(Long userId);

    @Query(value = "select rq " +
            "from Request as rq " +
            "where rq.event.id = ?1"
    )
    List<Request> getRequestByEventId(Long eventId);


    @Transactional
    @Modifying
    @Query(value = "update Request as rq " +
            "set rq.status = ?2 " +
            "where rq.id in ?1 "
    )
    void updateRequestStatus(List<Long> requestsId, RequestStatus requestStatus);

}
