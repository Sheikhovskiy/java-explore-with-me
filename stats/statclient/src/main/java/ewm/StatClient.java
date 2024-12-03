package ewm;

import ewm.dto.HitCreateDto;
import ewm.dto.StatsCreateDto;
import ewm.dto.StatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class StatClient {

    public static final String API_PREFIX = "http://stat-server:9090";

    private final RestClient restClient;


    @Autowired
    public StatClient(@Value("${ewm-server.url}") String serverUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(API_PREFIX)
                .build();
    }

    public void sendHit(HitCreateDto hitCreateDto) {

        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitCreateDto)
                .retrieve()
                .body(HitCreateDto.class);
    }

    public List<StatsDto> getStats(StatsCreateDto statsCreateDto) {
        String start = statsCreateDto.getStart();
        String end = statsCreateDto.getEnd();
        List<String> uris = statsCreateDto.getUris();
        boolean unique = statsCreateDto.isUnique();

        String url = UriComponentsBuilder.fromUriString(API_PREFIX)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique)
                .queryParam("uris", uris.toArray(new String[0]))
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsDto>>() {});

    }


}
