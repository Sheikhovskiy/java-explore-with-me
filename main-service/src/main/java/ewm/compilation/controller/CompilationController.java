package ewm.compilation.controller;

import ewm.compilation.Compilation;
import ewm.compilation.CompilationMapper;
import ewm.compilation.PublicCompilationParam;
import ewm.compilation.dto.CompilationDto;
import ewm.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("PUBLIC: Получен запрос на получение всех подборок событий");

        PublicCompilationParam publicCompilationParam = new PublicCompilationParam();
        publicCompilationParam.setPinned(pinned);
        publicCompilationParam.setFrom(from);
        publicCompilationParam.setSize(size);

        List<Compilation> compilationList = compilationService.getAll(publicCompilationParam);
        List<CompilationDto> compilationDtoList = CompilationMapper.fromListCompilationToListCompilationDto(compilationList);
        log.info("PUBLIC: Получен список всех подборок событий {}", compilationDtoList);
        return compilationDtoList;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {

        log.info("PUBLIC: Получен запрос на получение подборка события по id {}", compId);

        Compilation compilation = compilationService.getById(compId);
        CompilationDto compilationDto = CompilationMapper.fromCompilationToCompilationDto(compilation);
        log.info("PUBLIC: Получена подборка событий {} по id {}", compilationDto, compId);
        return compilationDto;
    }




}
