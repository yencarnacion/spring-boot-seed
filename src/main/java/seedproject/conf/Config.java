package seedproject.conf;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.sql.SQLException;

@Configuration
public class Config {

    // This bean opens the H2 console on port 8082.
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @DependsOn("dataSource")
    public Server dataSourceWebConnector() {
        try {
            return Server.createWebServer();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}