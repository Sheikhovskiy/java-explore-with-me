package ewm;

import ewm.dto.HitCreateDto;
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

    public static final String API_PREFIX = "/server/stats";

    private final RestClient restClient;


    @Autowired
    public StatClient(@Value("${ewm-server.url}") String serverUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public Object sendHit(HitCreateDto hitCreateDto) {

        return restClient.post()
                .uri(API_PREFIX + "/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitCreateDto)
                .retrieve()
                .body(HitCreateDto.class);
    }

    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {

        // Формирование строки запроса через UriComponentsBuilder
        String url = UriComponentsBuilder.fromUriString(API_PREFIX)
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique)
                .queryParam("uris", uris.toArray(new String[0]))
                .toUriString();

        // Использование RestClient для GET-запроса
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsDto>>() {});
    }


}
