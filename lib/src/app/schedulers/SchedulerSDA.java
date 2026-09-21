package app.schedulers;

import app.domain.Process;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulerSDA implements Scheduler {

    private Map<String, Integer> tipoPesos;
    private Process processoAtual = null;
    private int contadorTicks = 0;
    private int quantumAtual = 4; // Quantum padrão

    private Map<Process, Integer> tempoEsperaMap = new HashMap<>();
    private static final int LIMITE_INANICAO_CRITICA = 50;

    public SchedulerSDA(Map<String, Integer> tipoPesos) {
        this.tipoPesos = tipoPesos;
    }

    @Override
    public Process escolherProximoProcesso(List<Process> filaProntos) {
        if (filaProntos.isEmpty() || filaProntos == null) {
            processoAtual = null;
            contadorTicks = 0;
            return null;
        }

        //incrementa o tempo de espera de todos os processos na fila de prontos, exceto o processo atual na CPU
        for(Process p : filaProntos) {
        if(!p.equals(processoAtual)) {
            tempoEsperaMap.put(p, tempoEsperaMap.getOrDefault(p, 0) + 1);
        }
        }

        //verifica se o processo atual ainda tá na fila de prontos e se ainda não consumiu todo o quantum
        if(processoAtual != null && filaProntos.contains(processoAtual)) {
            if(contadorTicks < quantumAtual) {
                contadorTicks++;
                return processoAtual;
            }
            else {
                //quantum expirou, então move o processo atual para o final da fila
                filaProntos.remove(processoAtual);
                filaProntos.add(processoAtual);
            }
        }

        //escolha do novo processo com base no calculo scoring
        Process processoVencedor = null;
        double maiorPontuacao = -1;

        for (Process p : filaProntos) {

            String tipo = getTipoProcesso(p); 
            int pesoDoHistorico = tipoPesos.getOrDefault(tipo, 10); 
            int tempoEspera = tempoEsperaMap.getOrDefault(p, 0);
            
            int fatorPrioridade = 11 - p.getPrioridade(); 

            double pontuacao = (pesoDoHistorico * fatorPrioridade) + tempoEspera;

            if (pontuacao > maiorPontuacao) {
                maiorPontuacao = pontuacao;
                processoVencedor = p;
            }
        }

        processoAtual = processoVencedor;
        contadorTicks = 1; 

        // definir quantum variável baseado na inanição
        if (maiorPontuacao > LIMITE_INANICAO_CRITICA) {
            this.quantumAtual = 6; // bonus de execução para compensar a espera
        } else {
            this.quantumAtual = 4; // quantum normal
        }

        // o processo que entra na CPU zera o seu tempo de inanição
        tempoEsperaMap.remove(processoAtual);

        return processoAtual;
    }

    @Override
    public String getName() {
        return "SDA (Scoring Dynamic Algorithm Based on Historical)";
    }

    public int getQuantumAtual() {
        return this.quantumAtual;
    }

    private String getTipoProcesso(Process p) {
        if (p.getTipoProcesso() != null) {
            return p.getTipoProcesso().toString().toLowerCase(); 
        }
        return "";
    }

    @Override 
    public void adicionarProcesso(Process p) {
    }
}
