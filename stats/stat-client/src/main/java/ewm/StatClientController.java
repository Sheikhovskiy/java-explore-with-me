package ewm;


import ewm.dto.HitCreateDto;
import ewm.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatClientController {

    private final StatClient statClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> createHit(@RequestBody @Valid HitCreateDto hitCreateDto) {

        log.info("Получен запрос на сохранение запроса {}", hitCreateDto);

        statClient.sendHit(hitCreateDto);

        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam List<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {

        log.info("Получен запрос на получение всех обращений к приложению {}, с {} до {}, уникальные: {}", uris, start, end, unique);

        List<StatsDto> stats = statClient.getStats(start, end, uris, unique);

        return ResponseEntity.ok(stats);
    }

}
