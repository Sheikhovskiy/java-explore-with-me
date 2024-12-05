package ewm.subscription.repository;

import ewm.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
        QuerydslPredicateExecutor<Subscription> {


    Subscription findByOwnerId(Long ownerId);



}
