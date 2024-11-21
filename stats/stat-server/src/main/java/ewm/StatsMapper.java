package ewm;

import ewm.dto.HitCreateDto;
import ewm.dto.ViewStatsDto;
import ewm.model.EndpointHit;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StatsMapper {


    public EndpointHit toStatFromHitCreateDto(HitCreateDto hitCreateDto) {

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hitCreateDto.getApp());
        endpointHit.setUri(hitCreateDto.getUri());
        endpointHit.setIp(hitCreateDto.getIp());
        endpointHit.setTimestamp(LocalDateTime.parse(hitCreateDto.getTimestamp()));

        return endpointHit;
    }

    public ViewStatsDto toViewStatsDtoFromStat(EndpointHit endpointHit) {

        ViewStatsDto viewStatsDto = new ViewStatsDto();

        viewStatsDto.setApp(endpointHit.getApp());
        viewStatsDto.setUri(endpointHit.getUri());
//        viewStatsDto.setHits(endpointHit.getHits());
        return viewStatsDto;
    }

    public List<ViewStatsDto> toListStatsDtoFromListStat(List<EndpointHit> endpointHitList) {

        return endpointHitList.stream()
                .map(StatsMapper::toViewStatsDtoFromStat)
                .collect(Collectors.toList());
    }



}
