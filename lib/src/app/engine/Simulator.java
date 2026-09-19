package app.engine;

import app.domain.Process;
import app.domain.ProcessStatus;
import app.schedulers.Scheduler;

import java.util.ArrayList;
import java.util.List;

//controla o relógio global e garante que os estados dos processos são atualizados a cada unidade de tempo, transitando entre os estados definidos
public class Simulator {
    
    private int relogioGlobal;
    private List<Process> todosProcessos;
    private List<Process> processosProntos;
    private List<Process> processosBloqueados;
    private Scheduler escalonadorAtual;
    private Process processoNaCpu;

    public Simulator(List<Process> processosIniciais, Scheduler escalonador) {
        this.todosProcessos = processosIniciais;
        this.escalonadorAtual = escalonador;
        this.relogioGlobal = 0;
        this.processosProntos = new ArrayList<>();
        this.processosBloqueados = new ArrayList<>();
    }

    public void executar() {
        while(!todosFinalizados()) {
            verificarChegadas();
            atualizarBloqueados();

            //se a cpu está livre, solicita ao escalonador uma decisão
            if(processoNaCpu == null && !processosProntos.isEmpty()) {
                processoNaCpu = escalonadorAtual.escolherProximoProcesso(processosProntos);
                if(processoNaCpu != null) {
                    processoNaCpu.setEstadoAtual(ProcessStatus.EXECUTANDO);
                    processosProntos.remove(processoNaCpu);

                //registra o primeiro instante na cpu para o tempo de resposta
                if(processoNaCpu.getTempoPrimeiraExecucao() == -1) {
                    processoNaCpu.setTempoPrimeiraExecucao(relogioGlobal);
                }
            }
        }

        executarClicoCpu();
        atualizarEsperaProcessosProntos();
        relogioGlobal++;
    }
}

    private void verificarChegadas() {
        for (Process processo : todosProcessos) {
            if (processo.getTempoChegada() == relogioGlobal && processo.getEstadoAtual() == ProcessStatus.PRONTO) {
                processosProntos.add(processo);
                escalonadorAtual.adicionarProcesso(processo);
            }
        }
    }

    private void atualizarBloqueados() {
        List<Process> concluidosES = new ArrayList<>();
        for (Process processo : processosBloqueados) {
            processo.decrementarTempoRestanteBloqueado();
            if (processo.getTempoRestanteBloqueado() <= 0) {
                processo.setEstadoAtual(ProcessStatus.PRONTO);
                concluidosES.add(processo);
            }
        }

        for (Process processo : concluidosES) {
            processosBloqueados.remove(processo);
            processosProntos.add(processo);
            escalonadorAtual.adicionarProcesso(processo);
        }
    }

    private void executarClicoCpu() {
        if (processoNaCpu != null) {
            processoNaCpu.incrementarTempoExecutandoCpu();

            //verifica se o processo atingiu o tempo necessário de execução na cpu para ser concluído
            if (processoNaCpu.concluiuExecucao()) {
                processoNaCpu.setEstadoAtual(ProcessStatus.FINALIZADO);
                processoNaCpu.setTempoConclusao(relogioGlobal + 1);
                processoNaCpu = null;
            } else if (verificarInterrupcaoPorES(processoNaCpu)) {
                processoNaCpu.setEstadoAtual(ProcessStatus.BLOQUEADO);
                processoNaCpu.setTempoRestanteBloqueado(processoNaCpu.getDuracaoES());
                processosBloqueados.add(processoNaCpu);
                processoNaCpu = null;
            }
        
            //logica de preempção por quantum será controlada internamente pelos escalonadores round-robin e multiplas filas
        }
    }

    private boolean verificarInterrupcaoPorES(Process processo) {
        if (!processo.isTemOperacaoES()) return false;

        //simulaçao probabilistica simples baseada na probabilidade de E/S definida no ficheiro
        return Math.random() < processo.getProbabilidadeES();
        }
    
    private void atualizarEsperaProcessosProntos() {
        for (Process processo : processosProntos) {
            processo.incrementarTempoEspera();
        }
    }

    private boolean todosFinalizados() {
        for (Process processo : todosProcessos) {
            if (processo.getEstadoAtual() != ProcessStatus.FINALIZADO) {
                return false;
            }
        }
        return true;
    }
}