package org.wx;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.wx.config.DynamicThreadPoolAutoConfig;
import org.wx.config.DynamicThreadPoolAutoProperties;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@Configurable
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class);
    }
}
