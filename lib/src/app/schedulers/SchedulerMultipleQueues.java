package app.schedulers;

public class SchedulerMultipleQueues implements Scheduler {

    //quantidade de quantum do trabalho
    private static final int QUANTUM_FILA_1 = 2;
    private static final int QUANTUM_FILA_2 = 4;
    private static final int QUANTUM_FILA_3 = 8;
    
    //limite de tempo de espera (os ticks)
    private static final int LIMITE_INANICAO = 30;

    private Process currentProcess = null;
    private int currentQuantumLimit = 0;
    private int tickCount = 0;

    //rastrea de tempo de espera do processo com o tempo
    private Map<Process, Integer> waitTimesMap = new HashMap<>();
    
    //lista de processos que passaram do tempo e pra a fila 2
    private List<Process> promotedProcesses = new ArrayList<>();

    @Override
    public Process getNextProcess(List<Process> readyQueue) {
        if (readyQueue == null || readyQueue.isEmpty()) {
            currentProcess = null;
            tickCount = 0;
            return null;
        }

        //atualiza o tempo de espera dos processos 
        updateStarvationTracker(readyQueue);

        //classifica os processos nas 3 filas virtuais
        List<Process> fila1 = new ArrayList<>();
        List<Process> fila2 = new ArrayList<>();
        List<Process> fila3 = new ArrayList<>();

        for (Process p : readyQueue) {
            String tipo = getTipoProcesso(p);
            
            if (tipo.equals("tempo_real") || tipo.equals("interativo")) {
                fila1.add(p);
            } else if (tipo.equals("io_bound") || tipo.equals("misto") || promotedProcesses.contains(p)) {
                fila2.add(p);
            } else {
                fila3.add(p);
            }
        }

        //defini qual processo deve ganhar a CPU com base de prioridade 
        Process nextProcess = null;
        int nextQuantum = 0;

        if (!fila1.isEmpty()) {
            nextProcess = fila1.get(0);
            nextQuantum = QUANTUM_FILA_1;
        } else if (!fila2.isEmpty()) {
            nextProcess = fila2.get(0);
            nextQuantum = QUANTUM_FILA_2;
        } else if (!fila3.isEmpty()) {
            nextProcess = fila3.get(0);
            nextQuantum = QUANTUM_FILA_3;
        }

        //preempção e rotação
        if (currentProcess != null && nextProcess != null && !currentProcess.equals(nextProcess)) {
            // Se houve mudança de contexto por um processo de fila superior
            if (readyQueue.contains(currentProcess)) {
                //envia o processo interrompido pra o final da fila geral
                readyQueue.remove(currentProcess);
                readyQueue.add(currentProcess);
            }
            currentProcess = nextProcess;
            currentQuantumLimit = nextQuantum;
            tickCount = 0;
        } else if (currentProcess == null && nextProcess != null) {
            currentProcess = nextProcess;
            currentQuantumLimit = nextQuantum;
            tickCount = 0;
        }

        //verifica se o quantum do processo atual expirou
        if (tickCount >= currentQuantumLimit) {
            if (readyQueue.contains(currentProcess)) {
                readyQueue.remove(currentProcess);
                readyQueue.add(currentProcess);
            }
            currentProcess = null;
            tickCount = 0;
            
            //recalcula o processo após rotacionar a fila
            return getNextProcess(readyQueue);
        }

        //atualiza o estado e retorna
        if (currentProcess != null) {
            tickCount++;
            //se o processo ta pra executar, ele não tá em espera, logo sai do rastreio de inanição
            waitTimesMap.remove(currentProcess);
        }

        return currentProcess;
    }

    //essa parte é a estratégia contra inanição que o trabalho pediu
    // função dos processos da Fila 3 que aguardam mais qu o tempo que são promovidos à Fila 2.
    private void updateStarvationTracker(List<Process> readyQueue) {
        for (Process p : readyQueue) {
            String tipo = getTipoProcesso(p);
            
            if (tipo.equals("cpu_bound") || tipo.equals("batch")) {
                if (!p.equals(currentProcess) && !promotedProcesses.contains(p)) {
                    int tempoEspera = waitTimesMap.getOrDefault(p, 0) + 1;
                    waitTimesMap.put(p, tempoEspera);

                    if (tempoEspera >= LIMITE_INANICAO) {
                        promotedProcesses.add(p);      // Promove o processo
                        waitTimesMap.remove(p);        // Limpa o contador
                    }
                }
            }
        }
    }

   //auxiliar pra extrair o tipo do processo
    private String getTipoProcesso(Process p) {
        if (p.getTipoProcesso() != null) {
            return p.getTipoProcesso().toString().toLowerCase(); 
        }
        return "";
    }

    @Override
    public String getName() {
        return "Múltiplas Filas (Multilevel Queue)";
    }
}