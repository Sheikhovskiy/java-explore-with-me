package ewm.service;

import ewm.model.EndpointHit;

import java.util.List;

public interface StatServerService {

    void createHit(EndpointHit endpointHit);

    List<EndpointHit> getStats(String start, String end, List<String> uris, boolean unique);

}
