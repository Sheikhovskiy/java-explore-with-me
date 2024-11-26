package ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ewm", "stat-client.ewm"})
public class MainApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class);
        StatClient statClient = context.getBean(StatClient.class);
//        statClient.sendHit(new ParamHitDto());
//        statClient.getStats(new ParamStatDto);

    }


}
