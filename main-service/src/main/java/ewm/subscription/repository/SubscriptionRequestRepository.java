package ewm.subscription.repository;

import ewm.subscription.model.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long>,
        QuerydslPredicateExecutor<SubscriptionRequest> {



    @Query(value = "select case when COUNT(rq) > 0 then true else false end " +
            "from SubscriptionRequest as rq " +
            "where rq.requester.id = ?1 " +
            "and rq.userToFollow.id = ?2")
    boolean checkIfExistsByRequesterIdAndUserId(Long requesterId, Long userId);

    @Query(value = "select rq " +
            "from SubscriptionRequest as rq " +
            "where rq.requester.id = ?1 " +
            "and rq.userToFollow.id = ?2")
    SubscriptionRequest findByRequesterIdAndUserId(Long requesterId, Long userId);
}
