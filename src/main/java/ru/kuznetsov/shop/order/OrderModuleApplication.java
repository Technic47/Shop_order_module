package ru.kuznetsov.shop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.kuznetsov.shop.data.config.SpringConfig;
import ru.kuznetsov.shop.parameter.config.ParameterConfig;

@SpringBootApplication
@Import({SpringConfig.class, ParameterConfig.class})
public class OrderModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderModuleApplication.class, args);
    }

}
