package ewm;

import ewm.dto.HitCreateDto;
import ewm.dto.StatsDto;
import ewm.model.EndpointHit;
import ewm.service.StatServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

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
        EndpointHit endpointHit = StatsMapper.toStatFromHitCreateDto(hitCreateDto);
        statServerService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) throws BadRequestException {

        log.info("Получен запрос на сервер статистики о получение статистики: " +
                "c {} по {} к {} уникальные: {}", start, end, uris, unique);

        if (toTimeFormatFromString(start).isAfter(toTimeFormatFromString(end))) {
            throw new BadRequestException("Время начала поиска не может после времени конца поиска");
        }

        List<EndpointHit> stats = statServerService.getStats(start, end, uris, unique);
        log.info("stats " + stats);
        return StatsMapper.toListStatsDtoFromListEndpoint(stats);
    }



    private LocalDateTime toTimeFormatFromString(String time) {

        time = URLDecoder.decode(time, StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        return LocalDateTime.parse(time, formatter);
    }



}
