package ucas.csu.tsn.Hardware;

import ucas.csu.tsn.Yang.Topology.NetworkCard;
import ucas.csu.tsn.Yang.StreamLauncher;
import ucas.csu.tsn.Yang.TopologyLauncher;
import lombok.Builder;
import lombok.NonNull;

import java.util.Scanner;

public class Command {
    private TopologyLauncher topologyLauncher;
    private StreamLauncher streamLauncher;
    private Computer computer;

    @Builder
    public Command(@NonNull TopologyLauncher topologyLauncher,
                   @NonNull StreamLauncher streamLauncher,
                   @NonNull Computer computer){
        this.topologyLauncher = topologyLauncher;
        this.streamLauncher = streamLauncher;
        this.computer = computer;
    }

    public void start() throws Exception {
        int pattern  = 0;
        Scanner scanner = new Scanner(System.in);

        System.out.println("*****************************************************************");
        System.out.println("<TSN Client> Please input the number which pattern to start: \n" +
                "1. Talker\n2. Listener\n3. Both");
        System.out.println("*****************************************************************");

        String input = scanner.next();
        if (input.equals("1") || input.equals("talker") || input.equals("Talker")){
            pattern = 1;
        }else if (input.equals("2") || input.equals("listener") ||
                input.equals("Listener")){
            pattern = 2;
        } else if (input.equals("3") || input.equals("both") || input.equals("Both")) {
            pattern = 3;
        }

        topologyLauncher.startTimerThread();
        NetworkCard networkCard = computer.getCurrentNetworkCard();
        if (pattern == 1){
            System.out.println("<TSN Client> Start Talker Client.");
            streamLauncher.startPollingThread();
            streamLauncher.registerTalkerStream("talker client message", networkCard,
                    "192.168.1.15");
        }else if (pattern == 2){
            System.out.println("<TSN Client> Start Listener Server.");
            streamLauncher.startListenerServer(networkCard);
        }else if(pattern == 3){
            System.out.println("<TSN Client> Start Talker Client and Listener Server.");
            streamLauncher.startPollingThread();
            streamLauncher.startListenerServer(networkCard);
            streamLauncher.registerTalkerStream("talker client message", networkCard,
                    "192.168.1.15");
        }else {
            System.out.println("<TSN Client> Start Test Pattern.");
        }

        while (scanner.hasNext()){
            String str = scanner.next();

            if (str.equals("quit") || str.equals("exit") || str.equals("stop")){
                streamLauncher.stopStreamLauncher();
                topologyLauncher.stopTimerThread();
                break;
            }else{
                switch (pattern){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}