package ewm.compilation.service;

import ewm.compilation.AdminCompilationParam;
import ewm.compilation.Compilation;
import ewm.compilation.PublicCompilationParam;

import java.util.List;

public interface CompilationService {

    Compilation create(Compilation compilation);

    Compilation delete(Long id);

    Compilation update(AdminCompilationParam adminCompilationParam);

    List<Compilation> getAll(PublicCompilationParam publicCompilationParam);

    Compilation getById(Long id);







}
