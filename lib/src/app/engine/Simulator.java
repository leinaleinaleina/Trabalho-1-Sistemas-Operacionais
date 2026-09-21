package app.engine;

import app.domain.Process;
import app.schedulers.Scheduler;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private int globalClock;
    private List<Process> allProcesses;
    private List<Process> readyQueue;
    private List<Process> blockedQueue;
    private List<Process> executedProcesses; 
    private Scheduler scheduler;

    private Process currentProcess;
    private int contextSwitches;
    private List<String> executionTimeline; // Histórico do Gantt

    public Simulator(List<Process> initialProcesses, Scheduler scheduler) {
        this.allProcesses = initialProcesses;
        this.scheduler = scheduler;
        this.globalClock = 0;
        this.contextSwitches = 0;
        this.readyQueue = new ArrayList<>();
        this.blockedQueue = new ArrayList<>();
        this.executedProcesses = new ArrayList<>();
        this.executionTimeline = new ArrayList<>();
    }

    public void execute() {
        while (executedProcesses.size() < allProcesses.size()) {
            checkArrivals();
            updateBlockedProcesses();

            // Pede ao escalonador para definir quem usa a CPU neste tick
            Process nextProcess = scheduler.escolherProximoProcesso(readyQueue);

            // Regista troca de contexto (mudança de um processo A para B)
            if (currentProcess != null && nextProcess != null && !currentProcess.getPid().equals(nextProcess.getPid())) {
                contextSwitches++;
            }
            currentProcess = nextProcess;

            if (currentProcess != null) {
                // Marca o tempo de resposta se for a primeira vez na CPU
                if (currentProcess.getPrimeiroTempoCpu() == -1) {
                    currentProcess.setPrimeiroTempoCpu(globalClock);
                }

                currentProcess.incrementarTempoExecutandoCpu();
                executionTimeline.add(currentProcess.getPid()); // Alimenta o Gantt

                // Verifica se o processo terminou
                if (currentProcess.concluiuExecucao()) {
                    currentProcess.setTempoConclusao(globalClock + 1);
                    executedProcesses.add(currentProcess);
                    readyQueue.remove(currentProcess);
                    currentProcess = null; // Liberta a CPU
                } 
                // Simula E/S probabilística
                else if (checkIoInterruption(currentProcess)) {
                    currentProcess.setTempoRestanteBloqueado(currentProcess.getDuracaoES());
                    blockedQueue.add(currentProcess);
                    readyQueue.remove(currentProcess);
                    currentProcess = null; // Liberta a CPU
                }
            } else {
                executionTimeline.add(""); // CPU ociosa neste tick
            }

            globalClock++;
        }
    }

    private void checkArrivals() {
        for (Process p : allProcesses) {
            if (p.getTempoChegada() == globalClock) {
                readyQueue.add(p);
            }
        }
    }

    private void updateBlockedProcesses() {
        List<Process> returningFromIo = new ArrayList<>();
        for (Process p : blockedQueue) {
            p.decrementarTempoRestanteBloqueado();
            p.incrementarTempoTotalEmIo(); // Essencial para a CalculateMetrics
            
            if (p.getTempoRestanteBloqueado() <= 0) {
                returningFromIo.add(p);
            }
        }
        
        for (Process p : returningFromIo) {
            blockedQueue.remove(p);
            readyQueue.add(p);
        }
    }

    private boolean checkIoInterruption(Process p) {
        if (!p.isTemOperacaoES()) return false;
        return Math.random() < p.getProbabilidadeES();
    }

    // Getters utilizados pelo Main para gerar os relatórios
    public List<Process> getExecutedProcesses() { return executedProcesses; }
    public int getContextSwitches() { return contextSwitches; }
    public List<String> getExecutionTimeline() { return executionTimeline; }
}