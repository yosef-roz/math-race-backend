package com.example.math_race;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

@EnableAsync
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class MathRaceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MathRaceApplication.class, args);

        String port = "Unknown";
        if (context instanceof WebServerApplicationContext webServerContext) {
            port = String.valueOf(webServerContext.getWebServer().getPort());
        }

        String ipAddress = "Unknown IP";
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Could not determine IP address.");
        }

        System.out.println("\n----------------------------------------------------------");
        System.out.println("  Application 'Math Race' is running successfully!");
        System.out.println("  Server IP: " + ipAddress);
        System.out.println("  Server Port: " + port);
        System.out.println("  Local URL: http://" + ipAddress + ":" + port);
        System.out.println("----------------------------------------------------------\n");
    }
}
