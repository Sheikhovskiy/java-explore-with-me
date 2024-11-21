package ewm;

import ewm.dto.HitCreateDto;
import ewm.dto.ViewStatsDto;
import ewm.model.EndpointHit;
import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StatsMapper {


    public EndpointHit toStatFromHitCreateDto(HitCreateDto hitCreateDto) {

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hitCreateDto.getApp());
        endpointHit.setUri(hitCreateDto.getUri());
        endpointHit.setIp(hitCreateDto.getIp());
        endpointHit.setTimestamp(toTimeFormatFromString(hitCreateDto.getTimestamp()));

        return endpointHit;
    }

    public ViewStatsDto toViewStatsDtoFromStat(EndpointHit endpointHit) {

        ViewStatsDto viewStatsDto = new ViewStatsDto();

        viewStatsDto.setApp(endpointHit.getApp());
        viewStatsDto.setUri(endpointHit.getUri());
        if (endpointHit.getHits() == null) {
            viewStatsDto.setHits(0);
        } else {
            viewStatsDto.setHits(endpointHit.getHits());
        }

        return viewStatsDto;
    }

    public List<ViewStatsDto> toListStatsDtoFromListStat(List<EndpointHit> endpointHitList) {

        return endpointHitList.stream()
                .map(StatsMapper::toViewStatsDtoFromStat)
                .collect(Collectors.toList());
    }

    private LocalDateTime toTimeFormatFromString(String time) {

        time = URLDecoder.decode(time, StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return dateTime;
    }

}
