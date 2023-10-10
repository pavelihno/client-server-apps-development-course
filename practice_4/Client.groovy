import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport

public class Client {
    public static void main(String[] args) {
        
        def connection = RSocketConnector.connectWith(TcpClientTransport.create("localhost", 7000)).block()

        if (connection != null) {
            println "Connected to the RSocket server"
        } else {
           println "Failed to connect to the RSocket server"
        }
    }
}