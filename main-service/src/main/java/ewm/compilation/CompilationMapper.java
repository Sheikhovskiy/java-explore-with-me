package ewm.compilation;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.dto.CompilationUpdatedDto;
import ewm.compilation.dto.NewCompilationDto;
import ewm.compilation.dto.UpdateCompilationRequest;
import ewm.event.EventMapper;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public Compilation fromNewCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {

        Compilation compilation = new Compilation();

        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setEventList(newCompilationDto.getEvents());

        return compilation;
    }

    public CompilationDto fromCompilationToCompilationDto(Compilation compilation) {

        CompilationDto compilationDto = new CompilationDto();
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(EventMapper.toListEventShortDtoFromListEvent(compilation.getEvents()));
        } else {
            compilationDto.setEvents(new ArrayList<>());
        }
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        return compilationDto;
    }

    public CompilationUpdatedDto fromCompilationToCompilationUpdatedDto(Compilation compilation) {

        CompilationUpdatedDto compilationUpdatedDto = new CompilationUpdatedDto();

        if (compilation.getEventList() != null) {
            compilationUpdatedDto.setEvents(compilation.getEventList());
        } else {
            compilationUpdatedDto.setEvents(new ArrayList<>());
        }
        compilationUpdatedDto.setId(compilation.getId());
        compilationUpdatedDto.setPinned(compilation.getPinned());
        compilationUpdatedDto.setTitle(compilation.getTitle());
        return compilationUpdatedDto;
    }

    public Compilation fromUpdateCompilationRequestToCompilation(UpdateCompilationRequest updateCompilationRequest) {

        Compilation compilation = new Compilation();

        compilation.setEventList(updateCompilationRequest.getEvents());
        compilation.setPinned(updateCompilationRequest.getPinned());
        compilation.setTitle(updateCompilationRequest.getTitle());

        return compilation;
    }

    public List<CompilationDto> fromListCompilationToListCompilationDto(List<Compilation> compilationList) {

        return compilationList.stream()
                .map(CompilationMapper::fromCompilationToCompilationDto)
                .collect(Collectors.toList());
    }







}
