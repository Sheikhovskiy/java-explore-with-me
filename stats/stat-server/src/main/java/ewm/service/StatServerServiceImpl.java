package ewm.service;

import ewm.model.EndpointHit;
import ewm.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServerServiceImpl implements StatServerService {

    private final StatRepository statRepository;

    @Override
    public void createHit(EndpointHit endpointHit) {

        System.out.println(endpointHit);
        EndpointHit receivedStat = statRepository.save(endpointHit);
        System.out.println(receivedStat);
        return;
    }

    @Override
    public List<EndpointHit> getStats(String start, String end, List<String> uris, boolean unique) {

        if (unique) {
            return statRepository.getStatsByParameters(toTimeFormatFromString(start), toTimeFormatFromString(end), uris);
        }
        return statRepository.getStatsByParametersUnique(toTimeFormatFromString(start), toTimeFormatFromString(end), uris);
    }


    private LocalDateTime toTimeFormatFromString(String time) {

        time = URLDecoder.decode(time, StandardCharsets.UTF_8);

        time = time.replace('T', ' ');
        System.out.println(time);

        // Normalize multiple spaces to a single space
        time = time.replaceAll("\\s+", " ");

        // Trim leading and trailing spaces
        time = time.trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return dateTime;
    }



}
