package ua.beauty;

import ua.beauty.model.AppointmentState;
import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ClientAppointment;
import ua.beauty.model.Master;
import ua.beauty.model.ServiceCategory;
import ua.beauty.util.BeautyUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Main demonstration class for Lab 2 (Enum, Record, and Switch expressions).
 */
public class Main {

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("======================================================================");
        System.out.println("   ЛАБОРАТОРНА РОБОТА №2: ENUM, RECORD ТА SWITCH-ВИРАЗИ");
        System.out.println("   Варіант 11: Салон краси та СПА");
        System.out.println("======================================================================\n");

        // 1. Створення record-обʼєкта і звернення до його компонентів
        System.out.println("--- 1. Створення record-об'єктів та звернення до їхніх компонентів ---");
        BeautyClient client = new BeautyClient("  +380501234567  ", "  Анна Коваль  ", LocalDate.of(2001, 7, 20));
        System.out.println("Створено record BeautyClient:");
        System.out.println("  client.phone()     = " + client.phone());
        System.out.println("  client.name()      = " + client.name());
        System.out.println("  client.birthDate() = " + client.birthDate());
        System.out.println("  client.createdAt() = " + client.createdAt());

        Master master = new Master("EMP-001", "Ірина Шевченко", ServiceCategory.HAIRCUT);
        System.out.println("\nСтворено record Master:");
        System.out.println("  master.employeeId() = " + master.employeeId());
        System.out.println("  master.fullName()   = " + master.fullName());
        System.out.println("  master.category()   = " + master.category());
        System.out.println("  master.createdAt()  = " + master.createdAt() + "\n");

        // 2. Enum у роботі: перебір values(), доступ до власного поля, valueOf у try/catch
        System.out.println("--- 2. Enum у роботі (values(), власні поля, обробка помилок valueOf) ---");
        System.out.println("Перелік усіх категорій послуг через ServiceCategory.values():");
        for (ServiceCategory cat : ServiceCategory.values()) {
            System.out.printf("  Категорія: %-10s | Ціновий коефіцієнт (priceFactor): %.1fx%n",
                    cat.name(), cat.getPriceFactor());
        }

        System.out.println("\nПрямий доступ до поля константи:");
        System.out.println("  ServiceCategory.MASSAGE.getPriceFactor() = " + ServiceCategory.MASSAGE.getPriceFactor());

        System.out.println("\nСпроба викликати ServiceCategory.valueOf із некоректним рядком:");
        try {
            ServiceCategory invalid = ServiceCategory.valueOf("SOLARIUM");
            System.out.println("Знайдено: " + invalid);
        } catch (IllegalArgumentException e) {
            System.out.println("  Спіймано виняток для 'SOLARIUM': " + e.getMessage() + "\n");
        }

        // 3. Обидва switch-вирази
        System.out.println("--- 3. Демонстрація двох switch-виразів (Java 14+) ---");
        System.out.println("Switch-вираз #1 (вичерпний по enum ServiceCategory без default):");
        for (ServiceCategory cat : ServiceCategory.values()) {
            System.out.println("  " + cat + " -> " + BeautyUtils.getCategoryDescription(cat));
        }

        BeautyService service = new BeautyService("SRV-H01", "Модельна стрижка", ServiceCategory.HAIRCUT, 60, 650.0);
        ClientAppointment appointment = new ClientAppointment(
                client, master, service, LocalDate.now().plusDays(1), LocalTime.of(11, 0), AppointmentState.CONFIRMED
        );

        System.out.println("\nSwitch-вираз #2 (вибір дії за статусом запису):");
        for (AppointmentState state : AppointmentState.values()) {
            System.out.println("  Статус " + state + " -> " + BeautyUtils.getNextAction(state));
        }
        System.out.println("Поточна дія для запису клієнта: " + BeautyUtils.getNextAction(appointment.getState()) + "\n");

        // 4. Record з невалідними даними -> IllegalArgumentException із компактного конструктора
        System.out.println("--- 4. Валідація даних у компактному конструкторі record ---");
        try {
            System.out.print("Спроба створити BeautyClient з віком менше 14 років: ");
            new BeautyClient("+380990001122", "Малюк", LocalDate.now().minusYears(10));
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано виняток: " + e.getMessage());
        }

        try {
            System.out.print("Спроба створити Master із порожнім employeeId: ");
            new Master("   ", "Олексій", ServiceCategory.MANICURE);
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано виняток: " + e.getMessage() + "\n");
        }

        // 5. Два однакові record-обʼєкти: equals і hashCode без жодного рядка нашого коду
        System.out.println("--- 5. Автоматичні equals та hashCode для record ---");
        LocalDateTime fixedTime = LocalDateTime.of(2026, 11, 15, 12, 0);
        BeautyClient clientA = new BeautyClient("+380509998877", "Олена", LocalDate.of(1998, 3, 10), fixedTime);
        BeautyClient clientB = new BeautyClient("+380509998877", "Олена", LocalDate.of(1998, 3, 10), fixedTime);
        BeautyClient clientC = new BeautyClient("+380500000000", "Олена", LocalDate.of(1998, 3, 10), fixedTime);

        System.out.println("Порівняння clientA та clientB з ідентичними полями:");
        System.out.println("  clientA == clientB               : " + (clientA == clientB) + " (різні екземпляри в пам'яті)");
        System.out.println("  clientA.equals(clientB)          : " + clientA.equals(clientB) + " (автоматично згенерований equals)");
        System.out.println("  clientA.hashCode() == clientB.hashCode() : "
                + (clientA.hashCode() == clientB.hashCode()) + " (автоматично згенерований hashCode)");
        System.out.println("Порівняння з іншим clientC:");
        System.out.println("  clientA.equals(clientC)          : " + clientA.equals(clientC) + "\n");

        // 6. Вивід record через згенерований toString()
        System.out.println("--- 6. Автоматичний toString() для record ---");
        System.out.println("client: " + client);
        System.out.println("master: " + master);
        System.out.println("\nСутності, що залишилися звичайними класами (через наявність мутабельних полів ✎):");
        System.out.println("service (мутабельне price): " + service);
        System.out.println("appointment (мутабельне state): " + appointment);

    }
}
