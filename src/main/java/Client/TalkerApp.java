package Client;

import Client.Hardware.Computer;
import Client.Yang.CUCConnect;
import Client.Yang.NetworkTopology.LLDPImpl;
import com.alibaba.fastjson.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;

import static Client.Hardware.Computer.*;

public class TalkerApp {
    public static final String cuc_ip = "10.2.25.85";
    public static final String topology_id = "tsn-network";
    public static void main(String[] args) throws IOException, SAXException {
        Computer computer = new Computer();
        LLDPImpl lldpService = new LLDPImpl();
        CUCConnect cucConnect = new CUCConnect();
//        cucConnect.registerDevice(lldpService);
//        cucConnect.removeDevice(lldpService);

        cucConnect.registerAndSendStream("message1");
//        cucConnect.registerAndSendStream("message2");


//        GetInfo getInfo = GetInfo.builder().url("http://" + cuc_ip +
//                ":8181/restconf/operations/tsn-talker-type:test").build();
//        getInfo.getInfo();

    }

    public static JSONObject buildTestNode(){
        JSONObject node = new JSONObject();

        node.put("node-id", host_name);
        node.put("host", device_ip);
        node.put("port", 17830);
        node.put("username", "admin");
        node.put("password", "admin");
        node.put("tcp-only", false);
        node.put("reconnect-on-changed-schema", false);
        node.put("connection-timeout-millis", 20000);
        node.put("max-connection-attempts", 0);
        node.put("between-attempts-timeout-millis", 2000);
        node.put("sleep-factor", 1.5);
        node.put("keepalive-delay", 120);

        return node;
    }
}

/*
<node xmlns="urn:TBD:params:xml:ns:yang:network-topology">
  <node-id>ubuntu</node-id>
  <host xmlns="urn:opendaylight:netconf-node-topology">127.0.0.1</host>
  <port xmlns="urn:opendaylight:netconf-node-topology">17830</port>
  <username xmlns="urn:opendaylight:netconf-node-topology">admin</username>
  <password xmlns="urn:opendaylight:netconf-node-topology">admin</password>
  <tcp-only xmlns="urn:opendaylight:netconf-node-topology">false</tcp-only>
  <!-- non-mandatory fields with default values, you can safely remove these if you do not wish to override any of these values-->
  <reconnect-on-changed-schema xmlns="urn:opendaylight:netconf-node-topology">false</reconnect-on-changed-schema>
  <connection-timeout-millis xmlns="urn:opendaylight:netconf-node-topology">20000</connection-timeout-millis>
  <max-connection-attempts xmlns="urn:opendaylight:netconf-node-topology">0</max-connection-attempts>
  <between-attempts-timeout-millis xmlns="urn:opendaylight:netconf-node-topology">2000</between-attempts-timeout-millis>
  <sleep-factor xmlns="urn:opendaylight:netconf-node-topology">1.5</sleep-factor>
  <!-- keepalive-delay set to 0 turns off keepalives-->
  <keepalive-delay xmlns="urn:opendaylight:netconf-node-topology">120</keepalive-delay>
</node>

{
"urn:TBD:params:xml:ns:yang:network-topology:node":{
"node-id":"ubuntu",
"host":"127.0.0.1",
"username":"admin",
"password":"admin",
"port":17830
}
}
* */