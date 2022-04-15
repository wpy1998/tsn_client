package Yang;

import Yang.Stream.ListenerServer;
import Yang.Stream.TalkerClient;
import Yang.Stream.Header;
import lombok.Builder;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StreamLauncher {
    private String talkerFront, listenerFront, hostName;
    private static List<TalkerClient> clients = new ArrayList<>();

    private static Thread pollingThread = null;

    @Builder
    public StreamLauncher(@NonNull String talkerFront, @NonNull String listenerFront,
                          @NonNull String hostName){
        this.talkerFront = talkerFront;
        this.listenerFront = listenerFront;
        this.hostName = hostName;
    }

    public void startPollingThread(){
        if (pollingThread != null){
            System.out.println("Polling Thread has started");
            return;
        }
        pollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int timeInterval = 15 * 1000;
                try {
                    while (true){
                        for (TalkerClient talkerClient: clients){
                            System.out.println(talkerClient.getKey());
                        }
                        Thread.sleep(timeInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        pollingThread.start();
    }

    /**
     * create by: wpy
     * description: Common
     * create time: 3/15/22 8:28 PM
     *
      * @Param: null
     * @return
     */
    private static int unique_id = 0;

    synchronized private int allocateUniqueId(){
        int current = unique_id;
        unique_id++;
        return current;
    }

    /**
     * create by: wpy
     * description: talker: header, body 以下参数,函数仅在操作talker config库时使用
     * create time: 3/10/22 6:08 PM
     *
      * @Param: null
     * @return
     */

    private String convertUniqueID(int uniqueId){
        int front, next;
        next = unique_id % 100;
        front = unique_id % 10000 - next;
        String s1, s2;
        s1 = String.valueOf(front);
        s2 = String.valueOf(next);
        if (s1.length() == 1){
            s1 = "0" + s1;
        }
        if (s2.length() == 1){
            s2 = "0" + s2;
        }
        return s1 + "-" + s2;
    }

    public void registerTalkerStream(String body){
        int uniqueId = allocateUniqueId();
        Header header = Header.builder().uniqueId(convertUniqueID(uniqueId))
                .rank((short) 0)
                .build();

        TalkerClient client = TalkerClient.builder().host("localhost").port(17835).header(header)
                .url(this.talkerFront + this.hostName + "/stream-list/").build();
        clients.add(client);
    }

    /**
     * create by: wpy
     * description: Listener
     * create time: 3/15/22 8:28 PM
     *
      * @Param: null
     * @return
     */
    //listener 以下参数,函数仅在操作listener config库时使用
    public void startListenerServer() throws InterruptedException {
        int uniqueId = allocateUniqueId();
        Header header = Header.builder().uniqueId(convertUniqueID(uniqueId))
                .rank((short) 0)
                .build();
        ListenerServer server = ListenerServer.builder().port(17835).header(header)
                .url(this.listenerFront + hostName + "/stream-list/").build();
        server.start();
    }

    //status 以下参数,函数仅在操作status config库时使用
}