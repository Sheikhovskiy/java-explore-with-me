package ewm.compilation.controller;

import ewm.compilation.AdminCompilationParam;
import ewm.compilation.Compilation;
import ewm.compilation.CompilationMapper;
import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.CompilationUpdatedDto;
import ewm.compilation.dto.NewCompilationDto;
import ewm.compilation.dto.UpdateCompilationRequest;
import ewm.compilation.service.CompilationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {

        log.info("ADMIN: Получен запрос на создание подборки события {}", newCompilationDto);

        Compilation compilation = compilationService.create(CompilationMapper.fromNewCompilationDtoToCompilation(newCompilationDto));
        CompilationDto compilationDto = CompilationMapper.fromCompilationToCompilationDto(compilation);
        log.info("Подборка событий {} создана !", compilationDto);
        return compilationDto;
    }


    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CompilationDto deleteCompilation(@PathVariable Long compId) {

        log.info("ADMIN: Получен запрос на удаление подборки события");
        Compilation compilation = compilationService.delete(compId);
        CompilationDto compilationDto = CompilationMapper.fromCompilationToCompilationDto(compilation);
        log.info("Подборка событий успешна удалена !");
        return compilationDto;
    }

    @PatchMapping(path = "/{compId}")
    public CompilationUpdatedDto updateCompilation(@PathVariable Long compId,
                                                   @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) throws BadRequestException {

        log.info("ADMIN: Получен запрос на обновление подборки событий {} по id подборки {}", updateCompilationRequest, compId);

        if (updateCompilationRequest.getTitle() != null && (updateCompilationRequest.getTitle().length() > 50 || updateCompilationRequest.getTitle().isEmpty())) {
            throw new BadRequestException("Название нового названия подборки должно быть больше 1 символа и меньше 50 символов");
        }

        AdminCompilationParam adminCompilationParam = new AdminCompilationParam();
        adminCompilationParam.setCompId(compId);
        adminCompilationParam.setCompilation(CompilationMapper.fromUpdateCompilationRequestToCompilation(updateCompilationRequest));

        Compilation compilation = compilationService.update(adminCompilationParam);
        CompilationUpdatedDto compilationUpdatedDto = CompilationMapper.fromCompilationToCompilationUpdatedDto(compilation);
        log.info("Подборка событий успешно обновлена !");
        return compilationUpdatedDto;
    }



}
