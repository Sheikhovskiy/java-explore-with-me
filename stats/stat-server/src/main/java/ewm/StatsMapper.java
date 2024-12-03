package ewm;

import ewm.dto.HitCreateDto;
import ewm.dto.StatsDto;
import ewm.model.EndpointHit;
import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@UtilityClass
public class StatsMapper {


    public EndpointHit toStatFromHitCreateDto(HitCreateDto hitCreateDto) {

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hitCreateDto.getApp());
        endpointHit.setUri(String.valueOf(hitCreateDto.getUri()));
        endpointHit.setIp(hitCreateDto.getIp());
        endpointHit.setTimestamp(toTimeFormatFromString(hitCreateDto.getTimestamp()));

        return endpointHit;
    }

    public StatsDto toStatsDtoFromEndpoint(EndpointHit endpointHit) {

        StatsDto statsDto = new StatsDto();

        statsDto.setApp(endpointHit.getApp());
        statsDto.setUri(endpointHit.getUri());
        statsDto.setHits(endpointHit.getHits());

        return statsDto;
    }

    public List<StatsDto> toListStatsDtoFromListEndpoint(List<EndpointHit> endpointHitList) {

        return endpointHitList.stream()
                .map(StatsMapper::toStatsDtoFromEndpoint)
                .collect(Collectors.toList());
    }

    private LocalDateTime toTimeFormatFromString(String time) {

        time = URLDecoder.decode(time, StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return LocalDateTime.parse(time, formatter);
    }


}
