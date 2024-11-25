package ewm.service;

import ewm.model.EndpointHit;
import ewm.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServerServiceImpl implements StatServerService {

    private final StatRepository statRepository;

    @Override
    public void createHit(EndpointHit endpointHit) {

        System.out.println(endpointHit);
        EndpointHit receivedStat = statRepository.save(endpointHit);
        System.out.println(receivedStat);
    }

    @Override
    public List<EndpointHit> getStats(String start, String end, List<String> uris, boolean unique) {

        if (unique && !uris.isEmpty()) {
            List<Object[]> list = statRepository.getStatsByParametersUnique(toTimeFormatFromString(start), toTimeFormatFromString(end), uris);
            return makeEndPointListUniqueWithHits(list);

        } else if (unique && uris.isEmpty()) {
            List<EndpointHit> list =  statRepository.getStatsByParametersEmptyUrisUnique(toTimeFormatFromString(start), toTimeFormatFromString(end));
            return list;
        } else if (!unique && uris.isEmpty()) {
            List<EndpointHit> list = statRepository.getStatsByParametersEmptyUris(toTimeFormatFromString(start), toTimeFormatFromString(end));

            return makeEndPointListWithHits(list);
        } else {
            List<EndpointHit> list = statRepository.getStatsByParameters(toTimeFormatFromString(start), toTimeFormatFromString(end), uris);

            return makeEndPointListWithHits(list);
        }
    }


    private List<EndpointHit> makeEndPointListWithHits(List<EndpointHit> list) {
        Map<String, EndpointHit> uriMap = new HashMap<>();
        for (EndpointHit endpointHit : list) {
            String uri = endpointHit.getUri();
            if (!uriMap.containsKey(uri)) {
                endpointHit.setHits(1L);
                uriMap.put(uri, endpointHit);
            } else {
                Long actualHits = uriMap.get(uri).getHits();
                uriMap.get(uri).setHits(actualHits + 1);
            }
        }

        return new ArrayList<>(uriMap.values()).stream()
                .sorted(Comparator.comparing(EndpointHit::getHits).reversed())
                .collect(Collectors.toList());

    }

    private List<EndpointHit> makeEndPointListUniqueWithHits(List<Object[]> list) {

        return list.stream()
                .map(obj -> {
                    EndpointHit endpointHit = new EndpointHit();
                    endpointHit.setApp(obj[0].toString());
                    endpointHit.setUri(obj[1].toString());
                    endpointHit.setTimestamp((LocalDateTime) obj[2]);
                    endpointHit.setHits((Long) obj[3]);
                    return endpointHit;
                })
                .toList();
    }

    private LocalDateTime toTimeFormatFromString(String time) {

        time = URLDecoder.decode(time, StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return dateTime;
    }



}
