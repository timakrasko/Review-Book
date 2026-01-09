package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Тестовий контролер для діагностики проблем
 */
@RestController
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private CatalogRepositoryPort catalogRepositoryPort;

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("repositoryFound", catalogRepositoryPort != null);
        if (catalogRepositoryPort != null) {
            result.put("repositoryClass", catalogRepositoryPort.getClass().getName());
        }

        try {
            Map<String, CatalogRepositoryPort> beans = applicationContext.getBeansOfType(CatalogRepositoryPort.class);
            result.put("repositoryBeans", beans.keySet());
        } catch (Exception e) {
            result.put("repositoryBeansError", e.getMessage());
        }
        
        return result;
    }
}

