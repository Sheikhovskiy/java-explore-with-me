package ewm.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.compilation.AdminCompilationParam;
import ewm.compilation.Compilation;
import ewm.compilation.CompilationRepository;
import ewm.compilation.PublicCompilationParam;
import ewm.compilation.QCompilation;
import ewm.event.model.QEvent;
import ewm.event.repository.EventRepository;
import ewm.event.model.Event;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private static final String NOT_EXISTING_COMPILATION = "Подборка не найдена или недоступна";


    @Override
    public Compilation create(Compilation compilation) {

        List<Event> eventList;

        if (compilation.getEventList() != null) {
            eventList = eventRepository.getAllByIds(compilation.getEventList());
        } else {
            eventList = new ArrayList<>();
        }

        Compilation compilationSaved = compilationRepository.save(compilation);
        compilationSaved.setEvents(eventList);

        return compilationSaved;
    }

    @Override
    public Compilation delete(Long id) {

        Optional<Compilation> compilationOpt = compilationRepository.getCompilationById(id);

        if (compilationOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_COMPILATION);
        }

        compilationRepository.deleteById(id);
        return compilationOpt.get();
    }

    @Override
    public Compilation update(AdminCompilationParam adminCompilationParam) {

        Long compilationIdToModify = adminCompilationParam.getCompId();
        Compilation compilationToModify = adminCompilationParam.getCompilation();

        Optional<Compilation> compilationOpt = compilationRepository.getCompilationById(compilationIdToModify);

        if (compilationOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_COMPILATION);
        }

        Compilation compilation = compilationOpt.get();

        if (compilationToModify.getEventList() != null) {
            compilation.setEventList(compilationToModify.getEventList());
        }

        if (compilationToModify.getPinned() != null) {
           compilation.setPinned(compilationToModify.getPinned());
        }

        if (compilationToModify.getTitle() != null) {
            compilation.setTitle(compilationToModify.getTitle());
        }

        return compilationRepository.save(compilation);
    }

    @Override
    public List<Compilation> getAll(PublicCompilationParam publicCompilationParam) {

        boolean pinned = publicCompilationParam.isPinned();
        int from = publicCompilationParam.getFrom();
        int size = publicCompilationParam.getSize();

        QCompilation qCompilation = QCompilation.compilation;

        BooleanExpression pinnedQ = qCompilation.pinned.eq(pinned);
        BooleanExpression fromQ = qCompilation.id.gt(from);
        Pageable pageable = PageRequest.of(0, size);

        Iterable<Compilation> compilationIterable = compilationRepository.findAll(pinnedQ.and(fromQ), pageable);
        List<Compilation> compilationList = new ArrayList<>();

        compilationIterable.forEach(compilationList::add);
        return compilationList;
    }

    @Override
    public Compilation getById(Long compId) {

        Optional<Compilation> compilationOpt = compilationRepository.getCompilationById(compId);

        if (compilationOpt.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_COMPILATION);
        }

        Compilation compilation = compilationOpt.get();

        if (compilation.getEventList() != null) {
            List<Long> eventsIds = compilation.getEventList();
            QEvent qEvent = QEvent.event;

            BooleanExpression eventIdsQ = qEvent.id.in(eventsIds);
            Iterable<Event> eventIterable = eventRepository.findAll(eventIdsQ);
            List<Event> eventList = new ArrayList<>();
            eventIterable.forEach(eventList::add);
            compilation.setEvents(eventList);
        }

        return compilation;
    }




}
