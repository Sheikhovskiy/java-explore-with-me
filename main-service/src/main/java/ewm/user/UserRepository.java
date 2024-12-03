package ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User> {

    User save(User user);

    @Query(value = "select case when COUNT(us) > 0 then true else false end  " +
            "from User as us " +
            "where us.email = ?1"
    )
    boolean existByEmail(String email);


    Optional<User> getUserById(Long userId);








}
