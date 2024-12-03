package ewm;

import ewm.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long>,
        QuerydslPredicateExecutor<EndpointHit> {



    @Query(value = "select ep " +
            "from EndpointHit as ep " +
            "where ep.uri in ?3 " +
            "and ((ep.timestamp > ?1) and (ep.timestamp < ?2)) ")
    List<EndpointHit> getStatsByParameters(LocalDateTime start, LocalDateTime end, List<String> uris);



    @Query(value = "select ep.app, ep.uri, ep.timestamp, count(distinct ep.ip) as hits " +
            "from EndpointHit as ep " +
            "where ep.uri in ?3 " +
            "and (ep.timestamp between ?1 and ?2) " +
            "group by ep.app, ep.uri, ep.timestamp"
    )
    List<Object[]> getStatsByParametersUnique(LocalDateTime start, LocalDateTime end, List<String> uris);



    @Query(value = "select ep " +
            "from EndpointHit as ep " +
            "where ((ep.timestamp > ?1) and (ep.timestamp < ?2)) ")
    List<EndpointHit> getStatsByParametersEmptyUris(LocalDateTime start, LocalDateTime end);

    @Query(value = "select ep.app, ep.uri, count(distinct ep.ip) " +
            "from EndpointHit as ep " +
            "where ((ep.timestamp > ?1) and (ep.timestamp < ?2)) " +
            "group by ep.app, ep.uri")
    List<EndpointHit> getStatsByParametersEmptyUrisUnique(LocalDateTime start, LocalDateTime end);

}
