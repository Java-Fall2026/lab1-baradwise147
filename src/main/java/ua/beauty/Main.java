package ua.beauty;

import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ClientAppointment;
import ua.beauty.model.Master;
import ua.beauty.util.BeautyUtils;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Main demonstration class for Beauty Salon & Spa domain.
 */
public class Main {

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));

        System.out.println("======================================================================");
        System.out.println("   ЛАБОРАТОРНА РОБОТА №1 — ВАРІАНТ 11: САЛОН КРАСИ ТА СПА");
        System.out.println("======================================================================\n");

        // 1. Створення обʼєкта конструктором і створення фабричним методом
        System.out.println("--- 1. Створення об'єктів (публічний конструктор vs статична фабрика) ---");
        Master master1 = new Master("EMP-001", "  Ірина Шевченко  ", "haircut");
        System.out.println("Створено майстра через public конструктор: " + master1.getFullName()
                + " [категорія: " + master1.getCategory() + "]");

        BeautyClient client1 = BeautyClient.of("  +380501234567  ", "  Анна Коваль  ", LocalDate.of(2000, 5, 15));
        System.out.println("Створено клієнта через фабричний метод of(...): " + client1.getName()
                + " [тел: " + client1.getPhone() + "]");

        BeautyService serviceHaircut = BeautyService.of("SRV-H01", "  Модельна стрижка  ", "  haircut  ", 60, 650.0);
        System.out.println("Створено послугу через фабричний метод of(...): " + serviceHaircut.getName()
                + " [ціна: " + BeautyUtils.formatMoney(serviceHaircut.getPrice()) + "]");

        ClientAppointment appointment1 = new ClientAppointment(
                client1, master1, serviceHaircut, LocalDate.now().plusDays(1), LocalTime.of(10, 0), "confirmed"
        );
        System.out.println("Створено запис через public конструктор на "
                + appointment1.getAppointmentDate() + " о " + appointment1.getStartTime()
                + " [статус: " + appointment1.getState() + "]\n");

        // 2. Демонстрація нормалізації
        System.out.println("--- 2. Нормалізація вхідних даних (trim та переведення у потрібний регістр) ---");
        System.out.println("Вхідний телефон майстра/клієнта: '  +380501234567  ' -> '" + client1.getPhone() + "'");
        System.out.println("Вхідна категорія: '  haircut  ' -> '" + master1.getCategory() + "'");
        System.out.println("Вхідний стан запису: 'confirmed' -> '" + appointment1.getState() + "'\n");

        // 3. Три різні порушення правил валідації у try/catch
        System.out.println("--- 3. Обробка помилок валідації (три різні бізнес-правила) ---");

        // Порушення 1: Вік клієнта менше 14 років
        try {
            System.out.print("Тест 3.1 (вік клієнта < 14 років): ");
            BeautyClient.of("+380990001122", "Малюк", LocalDate.now().minusYears(10));
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано виняток: " + e.getMessage());
        }

        // Порушення 2: Неприпустима ціна послуги (ціна <= 0)
        try {
            System.out.print("Тест 3.2 (ціна послуги <= 0): ");
            BeautyService.of("SRV-BAD", "Безкоштовна стрижка", "HAIRCUT", 45, -50.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано виняток: " + e.getMessage());
        }

        // Порушення 3: Невідповідність категорії майстра та послуги
        try {
            System.out.print("Тест 3.3 (невідповідність категорій майстра і послуги): ");
            Master massageMaster = new Master("EMP-002", "Олег Масажист", "MASSAGE");
            new ClientAppointment(
                    client1, massageMaster, serviceHaircut, LocalDate.now().plusDays(2), LocalTime.of(12, 0), "CONFIRMED"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано виняток: " + e.getMessage());
        }
        System.out.println();

        // 4. Спроба зіпсувати валідний обʼєкт сеттером
        System.out.println("--- 4. Спроба зіпсувати валідний об'єкт через сеттер ---");
        System.out.println("Поточна ціна послуги: " + BeautyUtils.formatMoney(serviceHaircut.getPrice()));
        try {
            System.out.print("Спроба встановити ціну 0.0 грн: ");
            serviceHaircut.setPrice(0.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано виняток: " + e.getMessage());
        }
        System.out.println("Ціна після невдалої спроби залишилась: "
                + BeautyUtils.formatMoney(serviceHaircut.getPrice()) + "\n");

        // 5. Порівняння обʼєктів: ==, equals, hashCode (однакові бізнес-ключі та різні)
        System.out.println("--- 5. Порівняння об'єктів: ==, equals та hashCode ---");
        BeautyClient client1DuplicateKey = BeautyClient.of(
                client1.getPhone(), "Анна Інше Прізвище", LocalDate.of(1995, 1, 1)
        );
        BeautyClient differentClient = BeautyClient.of(
                "+380999999999", "Богдан", LocalDate.of(1990, 8, 20)
        );

        System.out.println("Два об'єкти з однаковим ключовим полем phone (" + client1.getPhone() + "):");
        System.out.println("  client1 == client1DuplicateKey : " + (client1 == client1DuplicateKey) + " (різні посилання в пам'яті)");
        System.out.println("  client1.equals(client1DuplicateKey) : " + client1.equals(client1DuplicateKey) + " (однаковий бізнес-ключ)");
        System.out.println("  client1.hashCode() == client1DuplicateKey.hashCode() : "
                + (client1.hashCode() == client1DuplicateKey.hashCode()));

        System.out.println("Порівняння з іншим клієнтом (phone=" + differentClient.getPhone() + "):");
        System.out.println("  client1.equals(differentClient) : " + client1.equals(differentClient) + "\n");

        // 6. Виклик обчислюваних методів
        System.out.println("--- 6. Обчислювані методи (не зберігаються в полях) ---");
        LocalTime appointmentEndTime = BeautyUtils.endTime(appointment1);
        System.out.println("Запис розпочинається о " + appointment1.getStartTime()
                + ", тривалість послуги: " + serviceHaircut.getDurationMinutes() + " хв.");
        System.out.println("Обчислений час завершення (endTime): " + appointmentEndTime);

        double ratePerMin = BeautyUtils.pricePerMinute(serviceHaircut);
        System.out.println("Вартість послуги: " + BeautyUtils.formatMoney(serviceHaircut.getPrice())
                + ", тривалість: " + serviceHaircut.getDurationMinutes() + " хв.");
        System.out.printf("Обчислена вартість за хвилину (pricePerMinute): %.2f грн/хв%n%n", ratePerMin);

        // 7. Вивід усіх сутностей через toString()
        System.out.println("--- 7. Вивід усіх сутностей варіанта через toString() ---");
        System.out.println("1) " + client1);
        System.out.println("2) " + master1);
        System.out.println("3) " + serviceHaircut);
        System.out.println("4) " + appointment1);
        System.out.println();

        // 8. Закоментовані рядки, які не компілюються через модифікатори доступу:
        // Рядок нижче не скомпілюється, оскільки клас ValidationHelper має package-private доступ
        // і не доступний ззовні пакета ua.beauty.util:
        // ua.beauty.util.ValidationHelper.requireNotBlank("test", "testField");

        // Рядок нижче не скомпілюється, оскільки конструктор BeautyClient позначено private (створення тільки через of(...)):
        // BeautyClient directInst = new BeautyClient("+380501234567", "Анна", LocalDate.of(2000, 1, 1));

    }
}
