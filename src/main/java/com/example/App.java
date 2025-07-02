package com.example;

import org.apache.camel.main.Main;
import com.example.route.DatatypeChannelRoute;

public class App {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new DatatypeChannelRoute());
        main.run(args);
    }
}
