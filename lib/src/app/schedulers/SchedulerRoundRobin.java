package app.schedulers;

public class SchedulerRoundRobin {
        public class SchedulerRoundRobin implements Scheduler {

        //O quantum tava especificado no trabalho 
        private static final int QUANTUM = 4;
        
        private Process currentProcess = null;
        private int tickCount = 0;

        @Override
        public Process getNextProcess(List<Process> readyQueue) {
            //Se a fila tá vazia e não tem processo nenhum
            if (readyQueue == null || readyQueue.isEmpty()) {
                currentProcess = null; 
                tickCount = 0;
                return null; //A CPU fica ociosa
            }

            // se não tá mais na fila de prontos que a engine retornou
            if (currentProcess == null || !readyQueue.contains(currentProcess)) {
                currentProcess = readyQueue.get(0);
                tickCount = 0; //reinicia o contador do quantum pra o novo processo
            } 
            //se o processo atual ainda tá na fila, mas já consumiu todo o quantum
            else if (tickCount >= QUANTUM) {
                //volta pro final da fila (RR)
                readyQueue.remove(currentProcess);
                readyQueue.add(currentProcess); //e depois coloca no fim da lista
                
                //pega o novo primeiro da fila
                currentProcess = readyQueue.get(0);
                tickCount = 0; //e ai reinicia o quantum pra entrar outro processo
            }

            //incrementa o tempo que o processo escolhido vai passar na CPU 
            tickCount++;

            return currentProcess;
        }

        @Override
        public String getName() {
            return "Round-Robin";
        }

        public int getQuantum() {
            return QUANTUM;
        }
    }
}
