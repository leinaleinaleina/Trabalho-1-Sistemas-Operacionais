package app.domain;

public class Process {

//lista mínima de processos
    public String pid;
    private int tempoChegada;
    private int tempoTotalCpu;
    private int prioridade;  //menor valor, maior prioridade
    private ProcessType tipoProcesso;
    private boolean temOperacaoES;
    private double probabilidadeES;
    private int mediaES;
    private int duracaoES;

//para controle de estado dinâmico da simulação
    private ProcessStatus estadoAtual;
    private int tempoExecutandoCpu;
    private int tempoEspera;
    private int tempoRestanteBloqueado;
    
//métricas
    private int tempoPrimeiraExecucao;
    private int tempoTotalEmIo;
    private int primeiroTempoCpu;
    private int tempoConclusao;

    public Process(String pid, int tempoChegada, int tempoTotalCpu, int prioridade, ProcessType tipoProcesso, boolean temOperacaoES, double probabilidadeES, int mediaES, int duracaoES) {
        this.pid = pid;
        this.tempoChegada = tempoChegada;
        this.tempoTotalCpu = tempoTotalCpu;
        this.prioridade = prioridade;
        this.tipoProcesso = tipoProcesso;
        this.temOperacaoES = temOperacaoES;
        this.probabilidadeES = probabilidadeES;
        this.mediaES = mediaES;
        this.duracaoES = duracaoES;

        //inicializa estado dinâmico
        this.estadoAtual = ProcessStatus.PRONTO;
        this.tempoExecutandoCpu = 0;
        this.tempoEspera = 0;
        this.tempoRestanteBloqueado = 0;

        //inicializa métricas
        this.tempoPrimeiraExecucao = -1; //indica que ainda não foi para a cpu
        this.tempoTotalEmIo = 0; //inicializa o tempo total em I/O
        this.primeiroTempoCpu = -1; //indica que ainda não foi para a cpu
        this.tempoConclusao = -1; //indica que ainda não foi concluído
    }

    //getters
    public String getPid() {
        return pid;
    }

    public int getTempoChegada() {
        return tempoChegada;
    }

    public int getTempoTotalCpu() {
        return tempoTotalCpu;
    }
    
    public int getPrioridade() {
        return prioridade;
    }

    public ProcessType getTipoProcesso() {
        return tipoProcesso;
    }

    public boolean isTemOperacaoES() {
        return temOperacaoES;
    }

    public double getProbabilidadeES() {
        return probabilidadeES;
    }

    public int getMediaES() {
        return mediaES;
    }

    public int getDuracaoES() {
        return duracaoES;
    }

    public ProcessStatus getEstadoAtual() {
        return estadoAtual;
    }

    public int getTempoExecutandoCpu() {
        return tempoExecutandoCpu;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public int getTempoRestanteBloqueado() {
        return tempoRestanteBloqueado;
    }
    
    public int getTempoPrimeiraExecucao() {
        return tempoPrimeiraExecucao;
    }
    
    public int getTempoTotalEmIo() { return tempoTotalEmIo; }

    public int getPrimeiroTempoCpu() { return primeiroTempoCpu; }

    public int getTempoConclusao() { return tempoConclusao; }
    
    //setters
    public void setEstadoAtual(ProcessStatus estadoAtual) {
        this.estadoAtual = estadoAtual;
    }
    
    public void setTempoRestanteBloqueado(int tempoRestanteBloqueado) {
        this.tempoRestanteBloqueado = tempoRestanteBloqueado;
    }
    
    public void setTempoPrimeiraExecucao(int tempoPrimeiraExecucao) {
        this.tempoPrimeiraExecucao = tempoPrimeiraExecucao;
    }

    public void setPrimeiroTempoCpu(int tick) { this.primeiroTempoCpu = tick; }

    public void setTempoConclusao(int tick) { this.tempoConclusao = tick; }

//métodos de incremento e decremento de tempo
    public void incrementarTempoExecutandoCpu() {
        this.tempoExecutandoCpu++;
    }

    public void incrementarTempoEspera() {
        this.tempoEspera++;
    }

    public void decrementarTempoRestanteBloqueado() {
        if (this.tempoRestanteBloqueado > 0) {
            this.tempoRestanteBloqueado--;
        }
    }
        
    //verifica se o processo atingiu o tempo necessário de execução na cpu para ser concluído
    public boolean concluiuExecucao() {
    return this.tempoExecutandoCpu >= this.tempoTotalCpu;
    }

    // Incrementar quando estiver na lista de bloqueados
    public void incrementarTempoTotalEmIo() { this.tempoTotalEmIo++; }

}