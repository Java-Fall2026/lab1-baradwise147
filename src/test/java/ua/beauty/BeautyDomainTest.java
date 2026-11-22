package ua.beauty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.beauty.model.AppointmentState;
import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ClientAppointment;
import ua.beauty.model.Master;
import ua.beauty.model.ServiceCategory;
import ua.beauty.util.BeautyUtils;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Object Creation and Computed Logic Tests")
class BeautyDomainTest {

    @Test
    @DisplayName("Should correctly create BeautyClient record with valid inputs")
    void testCreateValidBeautyClient() {
        BeautyClient client = new BeautyClient("  +380501234567  ", "  Anna Koval  ", LocalDate.of(2000, 1, 15));

        assertEquals("+380501234567", client.phone());
        assertEquals("Anna Koval", client.name());
        assertEquals(LocalDate.of(2000, 1, 15), client.birthDate());
    }

    @Test
    @DisplayName("Should correctly create Master record with valid inputs")
    void testCreateValidMaster() {
        Master master = new Master("  EMP-007  ", "  Olena Melnyk  ", ServiceCategory.MASSAGE);

        assertEquals("EMP-007", master.employeeId());
        assertEquals("Olena Melnyk", master.fullName());
        assertEquals(ServiceCategory.MASSAGE, master.category());
    }

    @Test
    @DisplayName("Should correctly compute endTime for an appointment")
    void testComputeEndTime() {
        BeautyClient client = new BeautyClient("+380501112233", "Client", LocalDate.of(1995, 5, 20));
        Master master = new Master("EMP-10", "Master", ServiceCategory.HAIRCUT);
        BeautyService service = new BeautyService("SRV-1", "Haircut", ServiceCategory.HAIRCUT, 75, 500.0);

        ClientAppointment appointment = new ClientAppointment(
                client, master, service, LocalDate.now().plusDays(1), LocalTime.of(14, 15), AppointmentState.CONFIRMED
        );

        LocalTime expectedEndTime = LocalTime.of(15, 30); // 14:15 + 75 min = 15:30
        assertEquals(expectedEndTime, BeautyUtils.endTime(appointment));
    }

    @Test
    @DisplayName("Should correctly compute pricePerMinute for a service")
    void testComputePricePerMinute() {
        BeautyService service = new BeautyService("SRV-M1", "Massage 90", ServiceCategory.MASSAGE, 90, 1350.0);

        double rate = BeautyUtils.pricePerMinute(service);
        assertEquals(15.0, rate, 0.0001); // 1350.0 / 90 = 15.0 UAH/min
    }

    @Test
    @DisplayName("Should reject invalid price in setter of mutable BeautyService")
    void testMutableServiceSetterValidation() {
        BeautyService service = new BeautyService("SRV-01", "Haircut", ServiceCategory.HAIRCUT, 30, 300.0);

        assertThrows(IllegalArgumentException.class, () -> service.setPrice(0.0));
        assertThrows(IllegalArgumentException.class, () -> service.setPrice(-25.0));
        assertEquals(300.0, service.getPrice()); // verify unchanged
    }
}
