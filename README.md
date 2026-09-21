<img src="/lib/assets/header/image.png" style="width:5.36111in; height:auto;">

# Simulador de Escalonador de Processos da disciplina de Sistemas Operacionais

## 1. Como executar

Para compilar e executar o simulador de escalonamento, certifique-se de que possui o Java Development Kit (JDK) instalado no seu ambiente. 

1. **Configuração dos Dados:** Verifique se os arquivos de entrada (`processos_entrada_correlacionados.csv` e `historico_escalonamento_2000_correlacionado.csv`) estão localizados dentro da pasta `inputs/` na raiz do projeto.
2. **Compilação:** Abra o terminal na raiz do projeto (onde está localizada a pasta `src/`) e compile todos os arquivos Java. Em ambientes IDE (VS Code, IntelliJ, Eclipse), basta garantir que as pastas `app.domain`, `app.engine`, `app.io`, `app.schedulers` e `app.doc` estão no *build path*.
3. **Execução:** Execute a classe principal `app.Main`. O simulador irá ler o histórico dinamicamente, executar os três algoritmos de forma independente em cópias isoladas da mesma lista de processos, e imprimir os Relatórios de Execução, os Diagramas de Gantt e as Métricas Globais diretamente na saída padrão (console).

---
## 2. Algoritmos Implementados
### 2.1 Round-Robin
*   **Mecânica:** Fila única com *quantum* fixo de 4 unidades de tempo.
*   **Tratamento de E/S:** Interrupções retiram o processo da cpu e movem-no para a lista de bloqueados.

### 2.2 Múltiplas Filas
*   **Fila 1 (Tempo Real/Interativo):** Quantum 2.
*   **Fila 2 (I/O Bound/Misto):** Quantum 4.
*   **Fila 3 (CPU Bound/Batch):** Quantum 8.
* **Preempção:** O algoritmo implementa preempção absoluta entre as filas. Se um processo destinado a uma fila de maior prioridade (ex: Fila 1 - Tempo Real/Interativo) ficar pronto enquanto um processo de prioridade inferior (ex: Fila 3 - Batch) estiver ocupando a CPU, o processo atual é imediatamente interrompido, perde a CPU e retorna para a fila geral de prontos, cedendo o processamento ao recém-chegado.
* **Estratégia contra Inanição (Aging):** Para evitar que processos pesados fiquem aguardando indefinidamente no fundo do sistema, foi implementado um rastreador de inanição com um limite crítico de 30 unidades de tempo (ticks). Apenas os processos tipicamente alocados na Fila 3 (`cpu_bound` e `batch`) têm o seu tempo de espera cronometrado. Se atingirem 30 ticks de espera, recebem uma "promoção" e passam a ser tratados como processos da Fila 2, garantindo que eventualmente recebam tempo de CPU.

### 2.3 SDA (Scoring Dynamic Algorithm Based on Historical)

O método proposto utiliza inteligência baseada em dados (histórico) para otimizar as trocas de contexto e priorizar processos críticos de forma dinâmica.

* **Critério de escolha:** A escolha do próximo processo é feita através de uma fórmula de pontuação recalculada a cada ciclo: `Pontuação = (PesoBase * Prioridade Invertida) + Tempo de Espera`. O processo com a maior pontuação ganha a CPU. Os Pesos Base são extraídos de forma dinâmica através da leitura de um CSV de decisões legadas.
* **Uso de prioridade:** Sim, utiliza prioridade base. O valor original do processo (onde 1 é maior prioridade e 10 é menor) é invertido matematicamente para atuar como um fator multiplicador na fórmula de pontuação.
* **Uso de quantum:** Variável. O algoritmo utiliza um *quantum* base de 4 unidades. No entanto, se o processo selecionado atingir o limite crítico de inanição (> 50 pontos), o *quantum* é estendido temporariamente para 6 unidades.
* **Tratamento de processos interativos:** Há preferência explícita. Como a leitura prévia do histórico provou que processos `interativos` e de `tempo_real` eram os mais selecionados no passado, o algoritmo absorve essas taxas de vitória como pesos pesados (28 e 36), alavancando-os naturalmente para o topo da fila.
* **Estratégia contra inanição:** O algoritmo utiliza uma técnica contínua de envelhecimento (*aging*). A cada unidade de tempo que um processo passa na fila de Prontos sem executar, ele ganha +1 ponto na sua métrica de `Tempo de Espera`. Matematicamente, qualquer processo ignorado acabará por ultrapassar processos recém-chegados.
* **Justificativa:** O método faz sentido porque elimina a rigidez e as sobrecargas de Múltiplas Filas, resultando num sistema com apenas 91 trocas de contexto (quase metade dos concorrentes), garantindo o menor tempo de retorno e o menor tempo de espera global da simulação.
* **Limitações:** A principal falha conhecida é a penalização do Tempo de Resposta inicial para processos com baixo peso base (ex: *batch*). Além disso, o recálculo contínuo da equação matemática para todos os processos prontos a cada ciclo gera *overhead* de processamento no motor da simulação.

## 3. Resultados e Métricas (Tabelas e Gantt)

```text
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**
**RELATÓRIO DE EXECUÇÃO: ROUND-ROBIN**
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**

**1. ORDEM DE EXECUÇÃO DOS PROCESSOS:**
P031 -> P041 -> P015 -> P016 -> P022 -> P008 -> P004 -> P014 -> P001 -> P010 -> P035 -> P049 -> P007 -> P023 -> P028 -> P011 -> P044 -> P020 -> P030 -> P005 -> P003 -> P042 -> P034 -> P037 -> P033 -> P036 -> P013 -> P002 -> P026 -> P024 -> P012 -> P021 -> P027 -> P029 -> P050 -> P043 -> P047 -> P017 -> P019 -> P032 -> P038 -> P046 -> P045 -> P009 -> P018 -> P040 -> P006 -> P039 -> P048 -> P025

**DIAGRAMA DE GANTT:**
| LIVRE (2) | P001 (4) | P002 (1) | P001 (4) | P003 (4) | P004 (4) | P005 (1) | P002 (1) | P001 (4) | P006 (1) | P007 (4) | P008 (4) | P009 (4) | P010 (4) | P011 (1) | P004 (4) | P012 (4) | P013 (1) | P003 (1) | P014 (4) | P005 (1) | P015 (4) | P016 (4) | P002 (1) | P001 (4) | P017 (4) | P018 (2) | P019 (1) | P020 (4) | P007 (4) | P021 (4) | P022 (4) | P008 (4) | P023 (1) | P024 (4) | P025 (1) | P006 (4) | P026 (1) | P009 (4) | P027 (1) | P028 (4) | P010 (4) | P029 (1) | P030 (2) | P031 (3) | P004 (4) | P032 (4) | P033 (2) | P011 (1) | P034 (4) | P035 (4) | P012 (4) | P036 (2) | P037 (4) | P038 (4) | P039 (1) | P014 (4) | P040 (4) | P013 (1) | P003 (4) | P041 (3) | P042 (2) | P005 (4) | P043 (1) | P015 (2) | P044 (4) | P045 (1) | P016 (4) | P046 (3) | P047 (4) | P002 (1) | P048 (1) | P049 (4) | P001 (4) | P050 (1) | P017 (4) | P019 (2) | P020 (4) | P018 (1) | P007 (4) | P021 (4) | P022 (2) | P008 (1) | P024 (4) | P023 (1) | P006 (2) | P025 (1) | P009 (4) | P028 (2) | P026 (2) | P010 (4) | P027 (1) | P029 (2) | P004 (2) | P030 (1) | P032 (4) | P034 (4) | P011 (1) | P033 (2) | P035 (4) | P012 (4) | P037 (4) | P036 (1) | P038 (4) | P014 (1) | P039 (1) | P040 (2) | P003 (3) | P013 (4) | P005 (1) | P043 (2) | P042 (1) | P044 (4) | P045 (1) | P047 (4) | P046 (2) | P002 (3) | P049 (4) | P001 (3) | P048 (2) | P017 (4) | P050 (1) | P019 (1) | P020 (4) | P007 (4) | P018 (2) | P021 (4) | P024 (4) | P023 (1) | P009 (4) | P025 (2) | P028 (4) | P006 (1) | P010 (1) | P026 (3) | P029 (1) | P032 (4) | P027 (1) | P034 (4) | P030 (1) | P011 (1) | P035 (4) | P033 (1) | P012 (4) | P037 (4) | P038 (4) | P036 (4) | P040 (2) | P039 (1) | P013 (3) | P003 (2) | P005 (3) | P043 (1) | P044 (4) | P045 (1) | P047 (4) | P042 (1) | P002 (1) | P049 (3) | P046 (4) | P017 (4) | P048 (1) | P019 (2) | P020 (4) | P050 (1) | P007 (2) | P021 (4) | P018 (1) | P024 (4) | P009 (4) | P023 (2) | P028 (3) | P025 (2) | P029 (4) | P032 (4) | P006 (2) | P034 (4) | P026 (1) | P027 (1) | P011 (1) | P012 (4) | P030 (1) | P033 (1) | P037 (4) | P038 (4) | P040 (1) | P036 (1) | P039 (1) | P013 (4) | P003 (1) | P005 (1) | P043 (1) | P044 (1) | P045 (1) | P047 (4) | P002 (1) | P046 (4) | P042 (2) | P017 (4) | P019 (1) | P020 (1) | P048 (2) | P021 (4) | P050 (1) | P024 (4) | P018 (1) | P009 (4) | P029 (4) | P032 (4) | P025 (2) | P034 (4) | P006 (1) | P012 (4) | P026 (3) | P027 (1) | P037 (4) | P033 (1) | P038 (4) | P030 (1) | P040 (2) | P036 (1) | P013 (1) | P039 (1) | P005 (1) | P043 (2) | P003 (4) | P045 (1) | P047 (4) | P002 (1) | P046 (4) | P017 (4) | P019 (1) | P042 (3) | P021 (4) | P024 (4) | P048 (2) | P050 (2) | P009 (4) | P018 (4) | P029 (4) | P032 (4) | P034 (1) | P025 (1) | P012 (4) | P006 (1) | P037 (4) | P038 (4) | P026 (1) | P027 (1) | P033 (1) | P040 (1) | P036 (3) | P039 (2) | P043 (1) | P013 (1) | P045 (1) | P047 (4) | P002 (2) | P046 (4) | P017 (4) | P019 (1) | P021 (4) | P024 (4) | P009 (4) | P050 (1) | P048 (1) | P018 (1) | P029 (4) | P032 (4) | P012 (4) | P025 (1) | P038 (4) | P006 (2) | P040 (4) | P026 (2) | P043 (3) | P027 (1) | P039 (1) | P045 (3) | P047 (4) | P046 (4) | P017 (4) | P019 (1) | P021 (4) | P024 (4) | P009 (4) | P029 (3) | P050 (1) | P018 (2) | P032 (4) | P048 (1) | P012 (4) | P038 (4) | P025 (2) | P040 (1) | P006 (4) | P043 (1) | P039 (1) | P045 (1) | P047 (4) | P027 (1) | P046 (4) | P017 (4) | P019 (1) | P021 (4) | P024 (2) | P009 (4) | P029 (4) | P032 (4) | P050 (4) | P018 (4) | P012 (4) | P038 (4) | P048 (1) | P040 (2) | P006 (4) | P043 (1) | P025 (1) | P039 (1) | P045 (1) | P047 (4) | P046 (4) | P017 (4) | P027 (1) | P019 (2) | P021 (4) | P009 (4) | P029 (4) | P032 (4) | P050 (1) | P018 (1) | P012 (2) | P038 (4) | P040 (1) | P006 (3) | P043 (2) | P048 (1) | P039 (1) | P045 (1) | P047 (4) | P025 (2) | P046 (4) | P017 (4) | P019 (2) | P021 (3) | P009 (4) | P027 (2) | P029 (3) | P032 (4) | P050 (1) | P018 (1) | P038 (4) | P040 (1) | P043 (1) | P039 (3) | P045 (2) | P047 (4) | P006 (1) | P048 (1) | P046 (4) | P017 (4) | P025 (1) | P019 (1) | P009 (4) | P029 (1) | P032 (4) | P038 (4) | P050 (2) | P018 (1) | P040 (2) | P043 (1) | P039 (1) | P045 (1) | P047 (1) | P046 (3) | P017 (4) | P006 (2) | P048 (1) | P019 (1) | P009 (4) | P025 (1) | P032 (4) | P038 (4) | P040 (3) | P018 (3) | P039 (2) | P045 (1) | P017 (4) | P046 (4) | P019 (1) | P009 (4) | P006 (4) | P048 (1) | P032 (2) | P038 (4) | P025 (1) | P040 (1) | P018 (2) | P039 (2) | P045 (3) | P046 (2) | P009 (4) | P040 (1) | P006 (1) | P048 (1) | P025 (1) | P018 (3) | P039 (1) | P045 (1) | P009 (2) | P040 (1) | LIVRE (1) | P006 (1) | P048 (1) | P025 (4) | P018 (1) | P039 (3) | P040 (1) | P025 (1) | P006 (2) | P048 (1) | P039 (2) | LIVRE (4) | P025 (1) | P039 (1) | LIVRE (2) | P048 (1) | LIVRE (2) | P039 (2) | LIVRE (1) | P025 (1) | LIVRE (4) | P048 (1) | LIVRE (4) | P025 (1) | LIVRE (5) | P048 (1) | LIVRE (3) | P025 (1) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (1) |


**MÉTRICAS INDIVIDUAIS POR PROCESSO:**
PID   | T. Início    | T. Conclusão    | T. Retorno      | T. Espera       | T. Resposta    
----------------------------------------------------------------------------------------
P031  | 125          | 128             | 81              | 78              | 78             
P041  | 175          | 178             | 118             | 115             | 115            
P015  | 58           | 187             | 163             | 157             | 34             
P016  | 62           | 196             | 172             | 164             | 38             
P022  | 90           | 235             | 201             | 195             | 56             
P008  | 30           | 236             | 220             | 211             | 14             
P004  | 15           | 261             | 253             | 239             | 7              
P014  | 53           | 291             | 269             | 260             | 31             
P001  | 2            | 326             | 324             | 290             | 0              
P010  | 38           | 365             | 347             | 334             | 20             
P035  | 143          | 384             | 333             | 321             | 92             
P049  | 205          | 427             | 356             | 345             | 134            
P007  | 26           | 445             | 430             | 401             | 11             
P023  | 98           | 460             | 424             | 395             | 62             
P028  | 114          | 463             | 422             | 405             | 73             
P011  | 42           | 482             | 464             | 431             | 24             
P044  | 187          | 507             | 443             | 427             | 123            
P020  | 78           | 525             | 495             | 478             | 48             
P030  | 123          | 574             | 529             | 475             | 78             
P005  | 19           | 580             | 571             | 535             | 10             
P003  | 11           | 586             | 579             | 525             | 4              
P042  | 178          | 604             | 543             | 486             | 117            
P034  | 139          | 633             | 583             | 562             | 89             
P037  | 153          | 643             | 588             | 564             | 98             
P033  | 136          | 650             | 601             | 548             | 87             
P036  | 151          | 654             | 601             | 559             | 98             
P013  | 51           | 658             | 638             | 591             | 31             
P002  | 6            | 665             | 661             | 617             | 2              
P026  | 108          | 714             | 676             | 591             | 70             
P024  | 99           | 796             | 760             | 726             | 63             
P012  | 47           | 870             | 850             | 808             | 27             
P021  | 86           | 902             | 869             | 826             | 53             
P027  | 113          | 908             | 868             | 749             | 73             
P029  | 122          | 949             | 905             | 840             | 78             
P050  | 213          | 959             | 886             | 798             | 140            
P043  | 184          | 963             | 901             | 851             | 122            
P047  | 199          | 966             | 897             | 852             | 130            
P017  | 71           | 1003            | 977             | 921             | 45             
P019  | 77           | 1008            | 980             | 897             | 49             
P032  | 132          | 1019            | 970             | 916             | 83             
P038  | 157          | 1023            | 968             | 888             | 102            
P046  | 196          | 1034            | 967             | 890             | 129            
P045  | 191          | 1047            | 982             | 892             | 126            
P009  | 34           | 1049            | 1032            | 966             | 17             
P018  | 75           | 1058            | 1031            | 910             | 48             
P040  | 166          | 1062            | 1003            | 941             | 107            
P006  | 25           | 1065            | 1053            | 861             | 13             
P039  | 161          | 1081            | 1025            | 889             | 105            
P048  | 204          | 1099            | 1028            | 820             | 133            
P025  | 103          | 1143            | 1106            | 854             | 66             


**ESTATÍSTICAS GLOBAIS:**
Número de trocas de contexto: 149
Tempo médio de retorno:       642,86
Tempo médio de espera:        587,88
Tempo médio de resposta:      67,06
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**

**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**
**RELATÓRIO DE EXECUÇÃO: MÚLTIPLAS FILAS (MULTILEVEL QUEUE)**
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**

**1. ORDEM DE EXECUÇÃO DOS PROCESSOS:**
P015 -> P004 -> P008 -> P005 -> P031 -> P022 -> P016 -> P041 -> P014 -> P010 -> P035 -> P028 -> P044 -> P043 -> P011 -> P049 -> P030 -> P020 -> P023 -> P033 -> P013 -> P034 -> P037 -> P036 -> P001 -> P042 -> P002 -> P050 -> P003 -> P024 -> P026 -> P012 -> P021 -> P027 -> P047 -> P006 -> P046 -> P017 -> P029 -> P032 -> P039 -> P007 -> P048 -> P045 -> P009 -> P018 -> P019 -> P040 -> P038 -> P025

**DIAGRAMA DE GANTT:**
| LIVRE (2) | P001 (4) | P002 (1) | P001 (1) | P004 (2) | P005 (2) | P004 (2) | P005 (2) | P004 (2) | P008 (2) | P010 (2) | P004 (2) | P005 (2) | P008 (2) | P014 (2) | P010 (2) | P015 (2) | P016 (2) | P004 (2) | P008 (2) | P005 (2) | P014 (2) | P010 (2) | P022 (2) | P015 (2) | P016 (2) | P004 (2) | P008 (2) | P028 (2) | P005 (2) | P014 (2) | P010 (2) | P031 (2) | P022 (2) | P015 (2) | P035 (2) | P016 (2) | P004 (2) | P008 (1) | P028 (2) | P041 (2) | P043 (2) | P014 (2) | P005 (2) | P044 (1) | P010 (2) | P031 (1) | P022 (2) | P035 (2) | P016 (2) | P028 (2) | P041 (1) | P043 (2) | P014 (1) | P044 (2) | P010 (2) | P035 (2) | P028 (2) | P043 (2) | P044 (2) | P010 (1) | P035 (2) | P028 (2) | P043 (1) | P044 (1) | P035 (2) | P028 (2) | P044 (1) | P043 (1) | P035 (2) | P028 (1) | P044 (2) | P043 (1) | P044 (2) | P043 (1) | P044 (2) | P043 (2) | P003 (2) | P043 (1) | P001 (2) | P043 (1) | P002 (2) | P043 (2) | P006 (1) | P007 (1) | P043 (1) | P009 (4) | P011 (4) | P012 (4) | P013 (3) | P017 (4) | P018 (1) | P019 (1) | P020 (4) | P021 (4) | P023 (1) | P024 (4) | P025 (1) | P026 (1) | P027 (2) | P029 (1) | P030 (2) | P032 (4) | P033 (1) | P034 (4) | P036 (2) | P037 (4) | P038 (2) | P039 (3) | P040 (1) | P042 (1) | P045 (1) | P046 (4) | P047 (4) | P048 (1) | P049 (4) | P050 (1) | P001 (3) | P003 (1) | P002 (2) | P007 (2) | P009 (4) | P006 (4) | P012 (4) | P011 (1) | P017 (4) | P013 (2) | P019 (1) | P020 (4) | P018 (1) | P021 (4) | P024 (4) | P023 (1) | P029 (3) | P025 (1) | P032 (4) | P026 (3) | P027 (1) | P034 (4) | P030 (1) | P033 (1) | P037 (4) | P036 (3) | P038 (4) | P040 (1) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P042 (2) | P049 (4) | P001 (1) | P048 (2) | P050 (2) | P007 (1) | P002 (1) | P003 (2) | P009 (4) | P012 (4) | P017 (4) | P006 (4) | P019 (1) | P020 (4) | P013 (4) | P021 (4) | P018 (1) | P024 (4) | P023 (1) | P029 (3) | P032 (4) | P025 (3) | P034 (4) | P026 (1) | P037 (4) | P027 (1) | P033 (3) | P030 (1) | P038 (4) | P036 (2) | P040 (2) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P049 (3) | P001 (2) | P007 (1) | P042 (1) | P002 (1) | P050 (2) | P048 (1) | P009 (4) | P003 (2) | P012 (4) | P017 (4) | P006 (3) | P019 (1) | P020 (4) | P013 (1) | P021 (4) | P024 (4) | P018 (2) | P023 (1) | P029 (1) | P032 (4) | P034 (4) | P025 (1) | P037 (4) | P026 (1) | P038 (4) | P027 (1) | P033 (1) | P040 (2) | P030 (2) | P036 (2) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P001 (2) | P007 (1) | P002 (1) | P009 (4) | P050 (1) | P042 (1) | P048 (1) | P012 (4) | P003 (2) | P017 (4) | P019 (1) | P020 (1) | P021 (4) | P006 (4) | P013 (1) | P024 (4) | P018 (3) | P029 (1) | P032 (4) | P023 (1) | P034 (4) | P037 (4) | P025 (2) | P038 (1) | P040 (1) | P026 (1) | P033 (2) | P027 (1) | P036 (1) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P001 (1) | P007 (1) | P002 (1) | P009 (4) | P012 (4) | P050 (3) | P042 (2) | P048 (2) | P017 (4) | P003 (4) | P019 (1) | P021 (4) | P024 (4) | P013 (4) | P006 (3) | P029 (2) | P032 (4) | P018 (1) | P034 (1) | P037 (4) | P038 (2) | P040 (3) | P025 (3) | P036 (1) | P039 (4) | P045 (1) | P046 (4) | P026 (1) | P027 (1) | P047 (4) | P001 (4) | P007 (1) | P002 (1) | P009 (4) | P012 (4) | P050 (1) | P017 (4) | P042 (1) | P048 (1) | P019 (1) | P021 (4) | P003 (4) | P024 (4) | P029 (4) | P032 (4) | P006 (4) | P018 (4) | P038 (4) | P040 (1) | P036 (1) | P025 (2) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P001 (3) | P007 (1) | P026 (1) | P027 (1) | P002 (1) | P009 (4) | P012 (4) | P017 (4) | P050 (4) | P019 (1) | P021 (4) | P003 (1) | P042 (1) | P048 (3) | P024 (4) | P029 (2) | P032 (4) | P018 (1) | P038 (3) | P040 (1) | P006 (1) | P039 (4) | P045 (1) | P046 (4) | P025 (1) | P047 (4) | P007 (1) | P002 (1) | P009 (4) | P012 (4) | P026 (1) | P027 (1) | P017 (4) | P050 (2) | P019 (1) | P021 (4) | P003 (1) | P024 (2) | P048 (1) | P029 (2) | P032 (4) | P038 (4) | P040 (1) | P018 (1) | P039 (2) | P045 (1) | P046 (4) | P006 (3) | P047 (4) | P007 (2) | P025 (2) | P009 (4) | P012 (4) | P017 (4) | P026 (3) | P027 (1) | P019 (1) | P021 (4) | P029 (4) | P032 (4) | P048 (1) | P038 (2) | P040 (1) | P018 (2) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P007 (1) | P006 (2) | P009 (4) | P012 (2) | P025 (1) | P017 (4) | P019 (1) | P021 (3) | P027 (1) | P029 (4) | P032 (4) | P038 (1) | P040 (4) | P048 (1) | P018 (1) | P039 (1) | P045 (1) | P046 (4) | P047 (4) | P007 (1) | P009 (4) | P006 (3) | P017 (4) | P025 (1) | P019 (1) | P029 (2) | P032 (4) | P038 (1) | P040 (1) | P018 (2) | P039 (4) | P045 (2) | P046 (4) | P048 (1) | P047 (1) | P007 (1) | P009 (4) | P017 (4) | P019 (1) | P006 (4) | P029 (4) | P032 (4) | P038 (1) | P025 (1) | P040 (1) | P018 (1) | P039 (1) | P045 (1) | P046 (2) | P007 (1) | P009 (4) | P048 (3) | P017 (4) | P019 (2) | P029 (2) | P032 (2) | P038 (1) | P040 (2) | P007 (1) | P018 (3) | P039 (3) | P045 (4) | P025 (1) | P009 (4) | P048 (1) | P019 (1) | P038 (2) | P040 (1) | P007 (1) | P018 (4) | P045 (1) | P009 (4) | P038 (1) | P025 (1) | P019 (1) | P040 (1) | P048 (2) | P045 (1) | P009 (2) | P018 (2) | P038 (1) | P040 (3) | P019 (1) | P025 (1) | P038 (2) | P040 (2) | P038 (1) | P040 (1) | P038 (4) | P025 (1) | P038 (11) | P025 (1) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (2) | LIVRE (9) | P025 (1) | LIVRE (9) | P025 (2) |


**MÉTRICAS INDIVIDUAIS POR PROCESSO:**
PID   | T. Início    | T. Conclusão    | T. Retorno      | T. Espera       | T. Resposta    
----------------------------------------------------------------------------------------
P015  | 32           | 70              | 46              | 40              | 8              
P004  | 8            | 76              | 68              | 54              | 0              
P008  | 18           | 77              | 61              | 52              | 2              
P005  | 10           | 87              | 78              | 54              | 1              
P031  | 64           | 91              | 44              | 41              | 17             
P022  | 46           | 93              | 59              | 53              | 12             
P016  | 34           | 97              | 73              | 65              | 10             
P041  | 79           | 100             | 40              | 37              | 19             
P014  | 28           | 103             | 81              | 72              | 6              
P010  | 20           | 116             | 98              | 85              | 2              
P035  | 70           | 130             | 79              | 67              | 19             
P028  | 56           | 131             | 90              | 73              | 15             
P044  | 87           | 139             | 75              | 58              | 23             
P043  | 81           | 154             | 92              | 45              | 19             
P011  | 158          | 253             | 235             | 223             | 140            
P049  | 227          | 409             | 338             | 327             | 156            
P030  | 193          | 476             | 431             | 389             | 148            
P020  | 175          | 511             | 481             | 464             | 145            
P023  | 183          | 533             | 497             | 460             | 147            
P033  | 199          | 548             | 499             | 455             | 150            
P013  | 166          | 599             | 579             | 532             | 146            
P034  | 200          | 610             | 560             | 539             | 150            
P037  | 206          | 614             | 559             | 535             | 151            
P036  | 204          | 694             | 641             | 593             | 151            
P001  | 2            | 709             | 707             | 676             | 0              
P042  | 216          | 736             | 675             | 594             | 155            
P002  | 6            | 771             | 767             | 719             | 2              
P050  | 231          | 787             | 714             | 650             | 158            
P003  | 141          | 793             | 786             | 718             | 134            
P024  | 184          | 795             | 759             | 725             | 148            
P026  | 189          | 841             | 803             | 694             | 151            
P012  | 162          | 880             | 860             | 818             | 142            
P021  | 179          | 889             | 856             | 813             | 146            
P027  | 190          | 890             | 850             | 731             | 150            
P047  | 222          | 951             | 882             | 837             | 153            
P006  | 151          | 965             | 953             | 797             | 139            
P046  | 218          | 981             | 914             | 864             | 151            
P017  | 169          | 993             | 967             | 911             | 143            
P029  | 192          | 997             | 953             | 868             | 148            
P032  | 195          | 999             | 950             | 896             | 146            
P039  | 212          | 1009            | 953             | 853             | 156            
P007  | 152          | 1024            | 1009            | 977             | 137            
P048  | 226          | 1039            | 968             | 804             | 155            
P045  | 217          | 1040            | 975             | 880             | 152            
P009  | 154          | 1042            | 1025            | 959             | 137            
P018  | 173          | 1044            | 1017            | 889             | 146            
P019  | 174          | 1049            | 1021            | 923             | 146            
P040  | 215          | 1056            | 997             | 933             | 156            
P038  | 210          | 1072            | 1017            | 942             | 155            
P025  | 188          | 1135            | 1098            | 846             | 151            


**ESTATÍSTICAS GLOBAIS:**
Número de trocas de contexto: 171
Tempo médio de retorno:       585,60
Tempo médio de espera:        532,60
Tempo médio de resposta:      103,88
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**

**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**
**RELATÓRIO DE EXECUÇÃO: SDA (SCORING DYNAMIC ALGORITHM BASED ON HISTORICAL)**
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**

**1. ORDEM DE EXECUÇÃO DOS PROCESSOS:**
P015 -> P008 -> P022 -> P031 -> P028 -> P041 -> P014 -> P004 -> P016 -> P043 -> P044 -> P035 -> P010 -> P049 -> P020 -> P011 -> P005 -> P007 -> P001 -> P023 -> P034 -> P033 -> P002 -> P013 -> P042 -> P050 -> P027 -> P030 -> P039 -> P037 -> P036 -> P026 -> P045 -> P025 -> P018 -> P024 -> P019 -> P003 -> P040 -> P012 -> P048 -> P047 -> P032 -> P021 -> P017 -> P009 -> P029 -> P046 -> P038 -> P006

**DIAGRAMA DE GANTT:**
| LIVRE (2) | P001 (6) | P004 (12) | P008 (6) | P015 (6) | P008 (3) | P022 (6) | P028 (8) | P031 (3) | P028 (5) | P014 (6) | P041 (3) | P014 (3) | P004 (2) | P016 (6) | P043 (2) | P005 (1) | P010 (6) | P043 (1) | P044 (6) | P043 (4) | P016 (2) | P043 (1) | P035 (6) | P043 (2) | P002 (1) | P044 (6) | P043 (4) | P005 (1) | P020 (6) | P043 (2) | P027 (1) | P010 (6) | P043 (1) | P044 (1) | P025 (1) | P023 (1) | P007 (6) | P001 (6) | P035 (6) | P018 (1) | P039 (1) | P026 (2) | P013 (3) | P019 (1) | P034 (6) | P005 (2) | P011 (1) | P033 (1) | P045 (1) | P030 (1) | P010 (1) | P036 (1) | P040 (3) | P050 (1) | P048 (1) | P042 (3) | P049 (6) | P009 (6) | P002 (3) | P003 (6) | P005 (1) | P012 (6) | P017 (6) | P020 (6) | P006 (4) | P027 (1) | P024 (6) | P029 (1) | P032 (6) | P025 (4) | P021 (6) | P023 (1) | P005 (1) | P039 (1) | P047 (6) | P037 (6) | P038 (6) | P007 (6) | P046 (6) | P034 (6) | P018 (4) | P026 (1) | P001 (6) | P045 (2) | P033 (3) | P019 (1) | P050 (1) | P005 (4) | P002 (3) | P040 (4) | P036 (1) | P013 (2) | P048 (1) | P049 (5) | P030 (1) | P020 (5) | P011 (4) | P042 (2) | P027 (1) | P025 (1) | P039 (3) | P005 (1) | P023 (2) | P009 (6) | P005 (1) | P034 (6) | P007 (6) | P012 (6) | P017 (6) | P045 (1) | P003 (1) | P002 (1) | P026 (1) | P018 (2) | P050 (1) | P024 (6) | P033 (2) | P029 (6) | P032 (6) | P027 (1) | P001 (5) | P019 (1) | P040 (1) | P039 (2) | P025 (1) | P036 (1) | P048 (1) | P006 (2) | P047 (6) | P023 (1) | P021 (6) | P013 (6) | P030 (1) | P034 (3) | P037 (6) | P042 (1) | P038 (6) | P002 (1) | P045 (1) | P046 (6) | P050 (1) | P027 (1) | P026 (1) | P018 (2) | P039 (4) | P033 (2) | P025 (1) | P040 (2) | P048 (1) | P036 (1) | P019 (3) | P009 (6) | P002 (2) | P027 (1) | P045 (1) | P012 (6) | P017 (6) | P039 (1) | P025 (3) | P030 (1) | P050 (1) | P013 (1) | P024 (6) | P027 (1) | P042 (1) | P003 (3) | P029 (2) | P026 (1) | P002 (1) | P018 (1) | P032 (6) | P040 (1) | P048 (1) | P045 (2) | P039 (4) | P036 (1) | P025 (3) | P027 (1) | P019 (2) | P047 (6) | P050 (6) | P006 (3) | P039 (1) | P027 (1) | P021 (6) | P025 (1) | P030 (1) | P045 (1) | P026 (1) | P013 (3) | P018 (4) | P040 (1) | P037 (6) | P042 (2) | P048 (1) | P027 (1) | P039 (1) | P038 (6) | P036 (1) | P046 (6) | P050 (5) | P025 (1) | P009 (6) | P019 (1) | P045 (1) | P027 (1) | P039 (5) | P012 (6) | P017 (6) | P026 (1) | P040 (1) | P024 (6) | P018 (4) | P030 (1) | P048 (1) | P025 (1) | P029 (2) | P032 (6) | P039 (1) | P036 (1) | P045 (1) | P003 (6) | P047 (6) | P019 (1) | P025 (2) | P039 (4) | P040 (1) | P026 (1) | P048 (1) | P045 (1) | P018 (2) | P021 (6) | P025 (2) | P006 (2) | P036 (4) | P037 (6) | P045 (1) | P025 (2) | P040 (2) | P038 (6) | P019 (2) | P026 (1) | P048 (1) | P009 (6) | P025 (1) | P046 (6) | P018 (2) | P045 (1) | P036 (1) | P012 (6) | P017 (6) | P025 (1) | P040 (3) | P024 (6) | P026 (1) | P048 (1) | P029 (6) | P045 (3) | P032 (6) | P019 (1) | P025 (1) | P018 (1) | P047 (6) | P003 (1) | P040 (2) | P025 (1) | P045 (1) | P026 (2) | P048 (2) | P021 (6) | P025 (3) | P045 (1) | P018 (4) | P019 (3) | P040 (1) | P025 (1) | P006 (4) | P045 (1) | P048 (1) | P009 (6) | P025 (1) | P038 (6) | P040 (1) | P018 (3) | P012 (6) | P017 (6) | P048 (2) | P046 (6) | P019 (1) | P024 (4) | P040 (2) | P029 (1) | P032 (6) | P048 (1) | P047 (6) | P040 (1) | P019 (1) | P003 (2) | P048 (1) | P040 (1) | P021 (6) | P040 (3) | P048 (1) | P009 (6) | P006 (4) | P048 (1) | P012 (6) | P017 (6) | P048 (1) | P038 (6) | P029 (2) | P032 (6) | P048 (1) | P046 (6) | P047 (6) | P009 (6) | P021 (6) | P017 (6) | P029 (2) | P006 (1) | P032 (6) | P038 (6) | P047 (3) | P009 (6) | P046 (6) | P017 (6) | P029 (1) | P032 (6) | P021 (1) | P009 (6) | P006 (1) | P038 (6) | P017 (2) | P029 (2) | P046 (6) | P009 (6) | P029 (2) | P038 (4) | P029 (4) | P006 (3) | P029 (4) | P046 (2) | P038 (4) | LIVRE (1) | P006 (4) | LIVRE (11) | P006 (1) | LIVRE (11) | P006 (2) | LIVRE (11) | P006 (5) |


**MÉTRICAS INDIVIDUAIS POR PROCESSO:**
PID   | T. Início    | T. Conclusão    | T. Retorno      | T. Espera       | T. Resposta    
----------------------------------------------------------------------------------------
P015  | 26           | 32              | 8               | 2               | 2              
P008  | 20           | 35              | 19              | 10              | 4              
P022  | 35           | 41              | 7               | 1               | 1              
P031  | 49           | 52              | 5               | 2               | 2              
P028  | 41           | 57              | 16              | -1              | 0              
P041  | 63           | 66              | 6               | 3               | 3              
P014  | 57           | 69              | 47              | 38              | 35             
P004  | 8            | 71              | 63              | 49              | 0              
P016  | 71           | 99              | 75              | 67              | 47             
P043  | 77           | 136             | 74              | 36              | 15             
P044  | 87           | 137             | 73              | 53              | 23             
P035  | 100          | 157             | 106             | 94              | 49             
P010  | 80           | 178             | 160             | 147             | 62             
P049  | 187          | 332             | 261             | 250             | 116            
P020  | 120          | 338             | 308             | 291             | 90             
P011  | 173          | 342             | 324             | 312             | 155            
P005  | 79           | 359             | 350             | 310             | 70             
P007  | 139          | 371             | 356             | 327             | 124            
P001  | 2            | 416             | 414             | 380             | 0              
P023  | 138          | 432             | 396             | 367             | 102            
P034  | 165          | 448             | 398             | 377             | 115            
P033  | 174          | 480             | 431             | 396             | 125            
P002  | 108          | 532             | 528             | 492             | 104            
P013  | 161          | 584             | 564             | 517             | 141            
P042  | 184          | 597             | 536             | 479             | 123            
P050  | 182          | 618             | 545             | 481             | 109            
P027  | 128          | 628             | 588             | 457             | 88             
P030  | 176          | 658             | 613             | 547             | 131            
P039  | 158          | 690             | 634             | 540             | 102            
P037  | 264          | 716             | 661             | 637             | 209            
P036  | 178          | 748             | 695             | 635             | 125            
P026  | 159          | 803             | 765             | 632             | 121            
P045  | 175          | 829             | 764             | 669             | 110            
P025  | 137          | 837             | 800             | 578             | 100            
P018  | 157          | 847             | 820             | 713             | 130            
P024  | 232          | 872             | 836             | 802             | 196            
P019  | 164          | 890             | 862             | 789             | 136            
P003  | 202          | 892             | 885             | 838             | 195            
P040  | 179          | 903             | 844             | 782             | 120            
P012  | 209          | 921             | 901             | 859             | 189            
P048  | 183          | 943             | 872             | 653             | 112            
P047  | 258          | 991             | 922             | 877             | 189            
P032  | 239          | 1016            | 967             | 913             | 190            
P021  | 249          | 1017            | 984             | 941             | 216            
P017  | 215          | 1032            | 1006            | 950             | 189            
P009  | 193          | 1046            | 1029            | 963             | 176            
P029  | 238          | 1063            | 1019            | 939             | 194            
P046  | 282          | 1065            | 998             | 948             | 215            
P038  | 270          | 1069            | 1014            | 941             | 215            
P006  | 227          | 1115            | 1103            | 923             | 215            


**ESTATÍSTICAS GLOBAIS:**
Número de trocas de contexto: 91
Tempo médio de retorno:       533,04
Tempo médio de espera:        480,12
Tempo médio de resposta:      109,60
**+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++**
```

## 4. Comparação Analítica
*   **Qual algoritmo teve o menor tempo médio de espera?** 
    * *O SDA obteve o melhor desempenho, com um tempo médio de espera de apenas 480,12 unidades de tempo, superando as Múltiplas Filas (532,60) e o Round-Robin (587,88). Isto ocorreu porque o SDA utilizou os pesos históricos para despachar processos críticos rapidamente e aplicou um quantum estendido (6) aos processos que sofriam de inanição, limpando a fila de prontos de forma mais eficiente.*
*   **Qual teve o menor tempo médio de resposta?** 
    * *O Round-Robin venceu nesta métrica, com 67,06 de tempo de resposta médio (contra 103,88 das Múltiplas Filas e 109,60 do SDA). Como o Round-Robin ignora prioridades e usa uma fila circular rigorosa com um quantum pequeno (4), garante que todo e qualquer processo novo receba uma fatia da CPU quase imediatamente após chegar ao sistema.*
*   **Qual favoreceu processos interativos?** 
    * *Tanto as Múltiplas Filas quanto o SDA. As Múltiplas Filas forçaram os processos interativos para a Fila 1 (prioridade máxima). O SDA, de forma mais orgânica, favoreceu-os porque extraiu do ficheiro legado de histórico de escalonamento que os processos interativos e de tempo real tinham as maiores taxas de vitória (pesos de 28 e 36, respetivamente), multiplicando a sua pontuação e colocando-os no topo da fila naturalmente.*
*   **Qual favoreceu processos longos?** 
    * *O SDA, através do seu mecanismo de expansão de quantum. Enquanto as Múltiplas Filas penalizavam processos CPU-bound despromovendo-os para a Fila 3, o SDA acumulava o tempo de espera desses processos longos. Quando o limite crítico de inanição (> 50) era atingido, o SDA não só lhes dava a CPU como expandia o seu quantum para 6, permitindo que processos pesados avançassem mais rapidamente, resultando no menor tempo de retorno global (533,04).*
*   **Houve risco de inanição?** 
    * *No Round-Robin não, devido à natureza circular. Nas Múltiplas Filas houve risco para a Fila 3, mas foi evitado pela regra de promoção após aguardar 30 unidades de tempo. No SDA também houve risco inicial para processos tipo Batch ou CPU-Bound (que tinham pesos muito baixos de 4 e 6), mas a inanição foi mitigada pelo fator de envelhecimento matemático, que somava 1 ponto por cada tick de espera, garantindo que eventualmente ultrapassassem os processos recém-chegados.*
*   **O método proposto foi melhor em algum cenário?** 
    * *O SDA foi superior em eficiência de CPU (Throughput) e redução de sobrecarga (Overhead). Ele gerou apenas 91 trocas de contexto, o que é drasticamente menor do que o Round-Robin (149) e as Múltiplas Filas (171). Menos trocas de contexto significam que o sistema operacional real perderia muito menos tempo a salvar e restaurar estados de memória. Além disso, obteve os menores tempos médios de Retorno e Espera globais.*
*   **Quais foram as limitações do método proposto?** 
    * *O SDA foi a limitação do Tempo de Resposta inicial (109,60). Ao favorecer matematicamente os processos com alto peso histórico, processos de baixa prioridade ficam "esquecidos" no fundo da fila até que o seu fator de inanição acumule pontos suficientes. Isso significa que um processo de fundo pode demorar bastante tempo até receber a sua primeira execução na CPU.*

## Link do repositório
https://github.com/leinaleinaleina/Trabalho-1-Sistemas-Operacionais.git

**Integrantes:** Leina Yoshida e Thaynara Nascimento
