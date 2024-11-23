package ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ewm", "stat-client.ewm"})
public class MainApplication {

    public static void main(String[] args) {
//        ConfigurableApplicationContext context = SpringApplication.run();
//        StatClient statClient = context.getBean(StatClient.class);
//        statClient.hit(new ParamHitDto());
    SpringApplication.run(MainApplication.class, args);

    }


}
