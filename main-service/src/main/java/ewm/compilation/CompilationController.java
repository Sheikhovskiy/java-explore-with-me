package ewm.compilation;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public class CompilationController {

    private List<Integer> events;
//    private Set<Integer> events;

    private boolean pinned;

//    @NotBlank
    @Size(min = 1, max = 50)
    private String title;



}
