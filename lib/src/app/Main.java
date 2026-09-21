package app;

import app.doc.CalculateMetrics;
import app.doc.GeneratorOutput;
import app.domain.Process;
import app.engine.Simulator;
import app.io.HistoricalReader;
import app.io.ProcessReader;
import app.schedulers.SchedulerMultipleQueues;
import app.schedulers.SchedulerRoundRobin;
import app.schedulers.SchedulerSDA;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        
        String caminhoProcessos = "C:\\Users\\yuuke\\OneDrive\\Documentos\\Unioeste\\2026\\SO\\simulador-escalonador-processo\\inputs\\processos_entrada_correlacionados.csv";
        String caminhoHistorico = "C:\\Users\\yuuke\\OneDrive\\Documentos\\Unioeste\\2026\\SO\\simulador-escalonador-processo\\inputs\\historico_escalonamento_2000_correlacionado.csv";

        System.out.println("\n===============================================================");
        System.out.println("        SIMULACAO DE ESCALONAMENTO DE PROCESSOS");
        System.out.println("===============================================================\n");

        // SDA extrai a inteligência dos dados em tempo real
        System.out.println("-> A ler historico dos arquivos...");
        Map<String, Integer> pesosHistorico = HistoricalReader.extractWeights(caminhoHistorico);
        System.out.println("Pesos extraidos com sucesso: " + pesosHistorico + "\n");

        // 1: ROUND-ROBIN
        List<Process> processosRR = ProcessReader.lerArquivoCSV(caminhoProcessos);
        if (!processosRR.isEmpty()) {
            SchedulerRoundRobin rr = new SchedulerRoundRobin();
            Simulator motorRR = new Simulator(processosRR, rr);
            motorRR.execute();
            
            CalculateMetrics metricasRR = new CalculateMetrics(motorRR.getExecutedProcesses(), motorRR.getContextSwitches());
            GeneratorOutput.printReport(rr.getName(), motorRR.getExecutedProcesses(), metricasRR, motorRR.getExecutionTimeline());
        }

        // 2: MÚLTIPLAS FILAS
        List<Process> processosMF = ProcessReader.lerArquivoCSV(caminhoProcessos);
        if (!processosMF.isEmpty()) {
            SchedulerMultipleQueues mf = new SchedulerMultipleQueues();
            Simulator motorMF = new Simulator(processosMF, mf);
            motorMF.execute();
            
            CalculateMetrics metricasMF = new CalculateMetrics(motorMF.getExecutedProcesses(), motorMF.getContextSwitches());
            GeneratorOutput.printReport(mf.getName(), motorMF.getExecutedProcesses(), metricasMF, motorMF.getExecutionTimeline());
        }

        // 3: SDA (Scoring Dynamic Algorithm Based on Historical)
        List<Process> processosSDA = ProcessReader.lerArquivoCSV(caminhoProcessos);
        if (!processosSDA.isEmpty()) {
            SchedulerSDA sda = new SchedulerSDA(pesosHistorico);
            Simulator motorSDA = new Simulator(processosSDA, sda);
            motorSDA.execute();
            
            CalculateMetrics metricasSDA = new CalculateMetrics(motorSDA.getExecutedProcesses(), motorSDA.getContextSwitches());
            GeneratorOutput.printReport(sda.getName(), motorSDA.getExecutedProcesses(), metricasSDA, motorSDA.getExecutionTimeline());
        }
    }
}