import org.junit.jupiter.api.Test;
import service.PayrollCalculator;
import model.Payroll;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PayrollCalculatorTest {

    PayrollCalculator calculator = new PayrollCalculator();

    @Test
    public void testCalculatePayrollValidEmployee() {
        int testEmployeeId = 1;
        LocalDate start = LocalDate.of(2024, 6, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);

        assertDoesNotThrow(() -> {
            Payroll payroll = calculator.calculatePayroll(testEmployeeId, start, end);
            assertNotNull(payroll);
            assertTrue(payroll.getNetPay() >= 0);
        });
    }

    @Test
    public void testInvalidEmployeeId() {
        assertThrows(PayrollCalculator.PayrollCalculationException.class, () -> {
            calculator.calculatePayroll(-1, LocalDate.now(), LocalDate.now().plusDays(1));
        });
    }
}
