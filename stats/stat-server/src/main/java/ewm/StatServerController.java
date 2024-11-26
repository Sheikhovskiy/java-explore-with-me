package ewm;

import ewm.dto.HitCreateDto;
import ewm.dto.ViewStatsDto;
import ewm.model.EndpointHit;
import ewm.service.StatServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatServerController {

    private final StatServerService statServerService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@RequestBody HitCreateDto hitCreateDto) {

        log.info("Получен запрос на сервер статистики об обращении к приложению {}", hitCreateDto);
        EndpointHit endpointHit = StatsMapper.toStatFromHitCreateDto(hitCreateDto); // Преобразование DTO в сущность
        statServerService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {

        log.info("Получен запрос на сервер статистики о получение статистики: " +
                "c {} по {} к {} уникальные: {}", start, end, uris, unique);

        List<EndpointHit> stats = statServerService.getStats(start, end, uris, unique);

        return StatsMapper.toListStatsDtoFromListStat(stats);
    }



}
