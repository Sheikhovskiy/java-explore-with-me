package ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Long>,
        QuerydslPredicateExecutor<User> {

    User save(User user);


    boolean existByEmail(String email);

//    @Query(value = "select case when COUNT(us) > 0  " +
//            "from User as us " +
//            "where us.email ?1"
//    )
//    boolean emailExist(String email);
    
}
