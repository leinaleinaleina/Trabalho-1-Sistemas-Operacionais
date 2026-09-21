package app.schedulers;

import app.domain.Process;
import java.util.List;

public interface Scheduler {

    //define qual será o próximo processo a assumir a cpu
    Process escolherProximoProcesso(List<Process> filaProntos);

    //notifica o scheduler que um processo novo chegou e voltou da e/s
    void adicionarProcesso(Process processo);

    public String getName();
}
