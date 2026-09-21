package app.doc;

import app.domain.Process;
import java.util.List;


public class CalculateMetrics {

    private List<Process> executedProcesses;
    private int contextSwitches;

    //o construtor recebe a lista final dos processos já executados
    //e o número de trocas de contexto da engine
    public CalculateMetrics(List<Process> executedProcesses, int contextSwitches) {
        this.executedProcesses = executedProcesses;
        this.contextSwitches = contextSwitches;
    }


    ///tempo_retorno = tempo_conclusao - tempo_chegada
    public int calculateTurnaroundTime(Process p) {
        return p.getTempoConclusao() - p.getTempoChegada();
    }

    //tempo_espera = tempo_retorno - tempo_cpu_total - tempo_total_em_io
    public int calculateWaitTime(Process p) {
        // A engine precisa ter rastreado quanto tempo o processo passou em I/O (tempoTotalEmIo)
        int tempoRetorno = calculateTurnaroundTime(p);
        return tempoRetorno - p.getTempoTotalCpu() - p.getTempoTotalEmIo();
    }

    
    //tempo_resposta = primeiro tempo em cpu - tempo_chegada
    public int calculateResponseTime(Process p) {
        //momento exato em que a CPU atendeu o processo pela primeira vez (retornado pela engine)
        return p.getPrimeiroTempoCpu() - p.getTempoChegada();
    }


    public double calculateAverageTurnaroundTime() {
        if (executedProcesses.isEmpty()) return 0.0;
        double sum = 0;
        for (Process p : executedProcesses) {
            sum += calculateTurnaroundTime(p);
        }
        return sum / executedProcesses.size();
    }

    public double calculateAverageWaitTime() {
        if (executedProcesses.isEmpty()) return 0.0;
        double sum = 0;
        for (Process p : executedProcesses) {
            sum += calculateWaitTime(p);
        }
        return sum / executedProcesses.size();
    }

    public double calculateAverageResponseTime() {
        if (executedProcesses.isEmpty()) return 0.0;
        double sum = 0;
        for (Process p : executedProcesses) {
            sum += calculateResponseTime(p);
        }
        return sum / executedProcesses.size();
    }

    public int getContextSwitches() {
        return contextSwitches;
    }
}
