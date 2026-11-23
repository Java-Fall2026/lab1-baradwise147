package ua.beauty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.beauty.io.ServiceLoader;
import ua.beauty.model.BeautyService;
import ua.beauty.util.BeautyUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * Main demonstration class for Lab 3 (Files, Logging, and Exception Handling).
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("======================================================================");
        System.out.println("   ЛАБОРАТОРНА РОБОТА №3: ФАЙЛИ, ЛОГУВАННЯ ТА ОБРОБКА ВИКЛЮЧЕНЬ");
        System.out.println("   Варіант 11: Салон краси та СПА");
        System.out.println("======================================================================\n");

        ServiceLoader loader = new ServiceLoader();

        // 1. Читання CSV-файла з коректними й некоректними рядками
        System.out.println("--- 1. Завантаження послуг із CSV-файлу (обробка некоректних рядків) ---");
        Path csvPath = Path.of("data", "services.csv");
        System.out.println("Читання файлу: " + csvPath.toAbsolutePath() + "\n");

        List<BeautyService> loadedServices = loader.loadFromFile(csvPath);

        System.out.println("\nУспішно завантажені послуги (некоректні рядки пропущено без падіння програми):");
        for (int i = 0; i < loadedServices.size(); i++) {
            BeautyService s = loadedServices.get(i);
            System.out.printf("  [%d] %-8s | %-22s | %-10s | %3d хв | %s%n",
                    i + 1, s.getServiceCode(), s.getName(), s.getCategory(),
                    s.getDurationMinutes(), BeautyUtils.formatMoney(s.getPrice()));
        }
        System.out.println();

        // 2. Спроба прочитати відсутній файл (програма не зупиняється)
        System.out.println("--- 2. Спроба відкрити неіснуючий файл (програма не падає) ---");
        Path nonExistentPath = Path.of("data", "missing_file_example.csv");
        System.out.println("Звернення до неіснуючого шляху: " + nonExistentPath);
        List<BeautyService> emptyList = loader.loadFromFile(nonExistentPath);
        System.out.println("Результат читання відсутнього файлу: повернуто порожній список розміром "
                + emptyList.size() + "\n");

        // 3. Некоректне значення в сеттері змінюваного класу та його обробка
        System.out.println("--- 3. Спроба встановити некоректне значення через сеттер ---");
        if (!loadedServices.isEmpty()) {
            BeautyService serviceToUpdate = loadedServices.get(0);
            System.out.println("Обрана послуга: " + serviceToUpdate.getName()
                    + ", поточна ціна: " + BeautyUtils.formatMoney(serviceToUpdate.getPrice()));
            try {
                System.out.print("Спроба встановити ціну -99.99 грн: ");
                serviceToUpdate.setPrice(-99.99);
            } catch (IllegalArgumentException e) {
                System.out.println("Спіймано виключення: " + e.getMessage());
                log.warn("Rejected invalid price update for service {}: {}",
                        serviceToUpdate.getServiceCode(), e.getMessage());
            }
            System.out.println("Ціна послуги після невдалої спроби залишилась: "
                    + BeautyUtils.formatMoney(serviceToUpdate.getPrice()) + "\n");
        }

        // 4. Демонстрація логування трьох рівнів (INFO, WARN, ERROR)
        System.out.println("--- 4. Демонстрація логування всіх трьох рівнів через SLF4J/Logback ---");
        log.info("Демонстрація рівня INFO: операція виконана штатно (завантажено {} послуг)", loadedServices.size());
        log.warn("Демонстрація рівня WARN: попередження про пропущені некоректні рядки у вхідному файлі");
        log.error("Демонстрація рівня ERROR: зафіксовано помилку при спробі відкриття неіснуючого ресурсу");

        System.out.println("\nДемонстрацію Лабораторної роботи №3 успішно завершено!");
    }
}
