package ibm.gse.orderms;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.orderms.domain.model.order.ShippingOrder;
import ibm.gse.orderms.infra.jms.JMSQueueWriter;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
public class PriceTest {

    private static final Logger log = Logger.getLogger(PriceTest.class);

    @Inject
    private JMSQueueWriter<ShippingOrder> jmsQueueWriter;

    @Test
    public void testLastPrice() throws Exception {
        assertDoesNotThrow(() ->
            jmsQueueWriter.sendMessage(getOrderFromTestFile(), "DEV.QUEUE.1")
        );
    }

    public ShippingOrder getOrderFromTestFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("order.json");
        if (is == null)
            throw new IllegalAccessError("file not found for order json");
        try {
            return mapper.readValue(is, mapper.getTypeFactory().constructType(ShippingOrder.class));
        } catch (Exception e) {
            log.error("Error reading resource file", e);
            throw e;
        }
    }
}
