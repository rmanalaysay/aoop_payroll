 import model.Deduction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeductionTest {

    @Test
    public void testValidDeduction() {
        Deduction d = new Deduction(1, "Late", 150.0);
        assertEquals("Late", d.getType());
        assertEquals(150.0, d.getAmount());
    }

    @Test
    public void testInvalidAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Deduction(1, "Late", -50.0);
        });
        assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    public void testInvalidType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Deduction(1, "InvalidType", 100.0);
        });
        assertTrue(exception.getMessage().contains("Invalid deduction type"));
    }
}
