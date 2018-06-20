import de.iolite.apps.example.JBotApplication;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

public class SpringBootTests {

    @Test
    public void serverStart(){
        SpringApplication sa = new SpringApplication(JBotApplication.class);
        sa.run("");
    }

}
