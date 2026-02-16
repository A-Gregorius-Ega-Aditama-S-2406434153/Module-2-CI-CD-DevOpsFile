package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class EshopApplicationTest {

    @Test
    void testApplicationClassCanBeInstantiated() {
        EshopApplication application = new EshopApplication();
        assertNotNull(application);
    }

    @Test
    void testMainDelegatesToSpringApplicationRun() {
        String[] args = {"--server.port=0"};
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            EshopApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(EshopApplication.class, args));
        }
    }
}
