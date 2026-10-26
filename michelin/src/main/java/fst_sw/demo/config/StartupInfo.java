package fst_sw.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupInfo {

    // Au démarrage : affiche l'url de la base de données et le PATH
    public StartupInfo(
        @Value("${spring.datasource.url}") String datasourceUrl,
        @Value("${PATH}") String path
    ) {
        System.out.println(datasourceUrl);
        System.out.println(path);
    }
}
