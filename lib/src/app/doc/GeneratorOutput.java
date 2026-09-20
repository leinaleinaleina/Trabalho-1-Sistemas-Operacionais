package app.doc;

public class GeneratorOutput {
    //relatório completo de um algoritmo, cumprindo aquele requisito da saída obrigatórias do simulador
    
    public static void printReport(String schedulerName, List<Process> executedProcesses, CalculateMetrics metrics, List<String> executionTimeline) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        System.out.println("RELATÓRIO DE EXECUÇÃO: " + schedulerName.toUpperCase());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        System.out.println("1. ORDEM DE EXECUÇÃO DOS PROCESSOS:");
        for (int i = 0; i < executedProcesses.size(); i++) {
            System.out.print(executedProcesses.get(i).getPid() + (i < executedProcesses.size() - 1 ? " -> " : "\n\n"));
        }

        System.out.println("DIAGRAMA DE GANTT:");
        printGanttChart(executionTimeline);
        System.out.println("\n");

        System.out.println("MÉTRICAS INDIVIDUAIS POR PROCESSO:");
        System.out.printf("%-5s | %-12s | %-15s | %-15s | %-15s | %-15s\n", 
                "PID", "T. Início", "T. Conclusão", "T. Retorno", "T. Espera", "T. Resposta");
        System.out.println("-".repeat(88));

        for (Process p : executedProcesses) {
            System.out.printf("%-5s | %-12d | %-15d | %-15d | %-15d | %-15d\n",
                    p.getPid(),
                    p.getPrimeiroTempoCpu(),
                    p.getTempoConclusao(),
                    metrics.calculateTurnaroundTime(p),
                    metrics.calculateWaitTime(p),
                    metrics.calculateResponseTime(p)
            );
        }
        System.out.println("\n");

        System.out.println("ESTATÍSTICAS GLOBAIS:");
        System.out.println("Número de trocas de contexto: " + metrics.getContextSwitches());
        System.out.printf("Tempo médio de retorno:       %.2f\n", metrics.calculateAverageTurnaroundTime());
        System.out.printf("Tempo médio de espera:        %.2f\n", metrics.calculateAverageWaitTime());
        System.out.printf("Tempo médio de resposta:      %.2f\n", metrics.calculateAverageResponseTime());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
    }

    /**
     * @param timeline Uma lista cronológica de PIDs simulados a cada unidade de tempo (tick).
     *                 Se a CPU ficou vazia num tick, a lista deve conter uma string vazia "".
     */
    private static void printGanttChart(List<String> timeline) {
        if (timeline == null || timeline.isEmpty()) {
            System.out.println("Nenhum dado de execução para exibir no Gantt.");
            return;
        }

        //imprime o processo, bem assim:
        // | P1 (4) | P2 (2) | P1 (3)
        System.out.print("|");
        String currentPid = timeline.get(0);
        int duration = 0;

        for (String pid : timeline) {
            if (pid.equals(currentPid)) {
                duration++;
            } else {
                String label = currentPid.isEmpty() ? "LIVRE" : currentPid;
                System.out.printf(" %s (%d) |", label, duration);
                currentPid = pid;
                duration = 1;
            }
        }
        //imprime o último bloco
        String label = currentPid.isEmpty() ? "LIVRE" : currentPid;
        System.out.printf(" %s (%d) |\n", label, duration);
    }
}
