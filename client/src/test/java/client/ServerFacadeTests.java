package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerUserTest() throws Exception {
        String auth = serverFacade.registerUser("username", "password", "email");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
    }

}
