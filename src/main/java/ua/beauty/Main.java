package ua.beauty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.beauty.exception.DuplicateIdentityException;
import ua.beauty.io.ServiceLoader;
import ua.beauty.model.AppointmentState;
import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.Master;
import ua.beauty.model.ServiceCategory;
import ua.beauty.repository.GenericRepository;
import ua.beauty.util.BeautyUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main demonstration class for Lab 4 (Generics, Collections, and Custom Repository).
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("======================================================================");
        System.out.println("   ЛАБОРАТОРНА РОБОТА №4: GENERICS, КОЛЕКЦІЇ ТА ВЛАСНИЙ РЕПОЗИТОРІЙ");
        System.out.println("   Варіант 11: Салон краси та СПА");
        System.out.println("======================================================================\n");

        // ----------------------------------------------------------------
        // 1. Три окремі репозиторії з різними типами та лямбда-екстракторами
        // ----------------------------------------------------------------
        System.out.println("--- 1. Репозиторії для BeautyClient (record), Master (record) і BeautyService (клас) ---");

        GenericRepository<BeautyClient> clientRepo =
                new GenericRepository<>(BeautyClient.class, BeautyClient::phone);

        GenericRepository<Master> masterRepo =
                new GenericRepository<>(Master.class, Master::employeeId);

        GenericRepository<BeautyService> serviceRepo =
                new GenericRepository<>(BeautyService.class, BeautyService::getServiceCode);

        // Populate clients
        BeautyClient client1 = new BeautyClient("+380671234567", "Олена Мороз",   LocalDate.of(1995, 3, 14));
        BeautyClient client2 = new BeautyClient("+380509876543", "Марія Коваль",  LocalDate.of(1988, 7, 22));
        BeautyClient client3 = new BeautyClient("+380631112233", "Анна Лисенко",  LocalDate.of(2000, 11, 5));
        clientRepo.add(client1);
        clientRepo.add(client2);
        clientRepo.add(client3);
        System.out.printf("Клієнтів у репозиторії: %d%n", clientRepo.size());

        // Populate masters
        Master master1 = new Master("EMP-001", "Іванна Шевченко", ServiceCategory.HAIRCUT);
        Master master2 = new Master("EMP-002", "Катерина Бойко",  ServiceCategory.MANICURE);
        masterRepo.add(master1);
        masterRepo.add(master2);
        System.out.printf("Майстрів у репозиторії: %d%n%n", masterRepo.size());

        // ----------------------------------------------------------------
        // 2. Search: found and not found
        // ----------------------------------------------------------------
        System.out.println("--- 2. Пошук за ідентифікатором (знайдено / не знайдено) ---");

        Optional<BeautyClient> found = clientRepo.findByIdentity("+380671234567");
        found.ifPresent(c -> System.out.println("Знайдено клієнта: " + c.name()));

        Optional<Master> notFound = masterRepo.findByIdentity("EMP-999");
        System.out.println("Пошук EMP-999: " + (notFound.isPresent() ? notFound.get() : "не знайдено") + "\n");

        // ----------------------------------------------------------------
        // 3. Завантаження BeautyService з CSV-файлу (коректні + некоректні рядки)
        // ----------------------------------------------------------------
        System.out.println("--- 3. Завантаження послуг з CSV (некоректні рядки пропускаються) ---");
        ServiceLoader loader = new ServiceLoader();
        Path csvPath = Path.of("data", "services.csv");
        List<BeautyService> loaded = loader.loadFromFile(csvPath);

        // Populate serviceRepo via addAll — duplicates skipped automatically
        serviceRepo.addAll(loaded);
        System.out.printf("CSV повернув %d послуг; у репозиторії після addAll: %d%n%n",
                loaded.size(), serviceRepo.size());

        // ----------------------------------------------------------------
        // 4. Відсутній файл — програма не зупиняється
        // ----------------------------------------------------------------
        System.out.println("--- 4. Спроба відкрити відсутній файл ---");
        List<BeautyService> empty = loader.loadFromFile(Path.of("data", "no_such_file.csv"));
        System.out.println("Результат: повернуто порожній список розміром " + empty.size() + "\n");

        // ----------------------------------------------------------------
        // 5. DuplicateIdentityException через add() та тихе ігнорування через addIfAbsent()
        // ----------------------------------------------------------------
        System.out.println("--- 5. Дублікат: add() кидає виключення, addIfAbsent() повертає false ---");
        BeautyService duplicate = new BeautyService("SRV-H01", "Duplicate Haircut",
                ServiceCategory.HAIRCUT, 30, 350.0);
        try {
            serviceRepo.add(duplicate);
        } catch (DuplicateIdentityException e) {
            System.out.println("add() → спіймано: " + e.getMessage());
            log.warn("Duplicate add attempted for identity 'SRV-H01': {}", e.getMessage());
        }
        boolean wasAdded = serviceRepo.addIfAbsent(duplicate);
        System.out.println("addIfAbsent() → повернув: " + wasAdded + " (репозиторій не змінився)\n");

        // ----------------------------------------------------------------
        // 6. removeByIdentity — існуючий та відсутній
        // ----------------------------------------------------------------
        System.out.println("--- 6. Видалення за ідентифікатором ---");
        boolean removedClient  = clientRepo.removeByIdentity("+380631112233");
        boolean removedGhost   = clientRepo.removeByIdentity("+380000000000");
        System.out.printf("Видалено +380631112233: %b | Видалено +380000000000: %b%n",
                removedClient, removedGhost);
        System.out.printf("Клієнтів після видалення: %d%n%n", clientRepo.size());

        // ----------------------------------------------------------------
        // 7. Некоректне значення в сеттері змінюваного класу
        // ----------------------------------------------------------------
        System.out.println("--- 7. Некоректне значення в сеттері BeautyService ---");
        Optional<BeautyService> svcOpt = serviceRepo.findByIdentity("SRV-M01");
        svcOpt.ifPresent(svc -> {
            System.out.printf("Поточна ціна '%s': %s%n",
                    svc.getName(), BeautyUtils.formatMoney(svc.getPrice()));
            try {
                svc.setPrice(-1.0);
            } catch (IllegalArgumentException e) {
                System.out.println("setPrice(-1.0) → спіймано: " + e.getMessage());
                log.warn("Rejected invalid price for {}: {}", svc.getServiceCode(), e.getMessage());
            }
            System.out.println("Ціна після невдалої спроби: " + BeautyUtils.formatMoney(svc.getPrice()));
        });

        // ----------------------------------------------------------------
        // 8. Зміна стану ClientAppointment через setState()
        // ----------------------------------------------------------------
        System.out.println("\n--- 8. Оновлення стану ClientAppointment ---");
        try {
            var appt = new ua.beauty.model.ClientAppointment(
                    client1, master1, loaded.get(0),
                    LocalDate.now().plusDays(3),
                    java.time.LocalTime.of(11, 0),
                    AppointmentState.CONFIRMED);
            System.out.println("Поточний стан запису: " + appt.getState());
            appt.setState(AppointmentState.COMPLETED);
            System.out.println("Оновлено стан запису: " + appt.getState());
        } catch (Exception e) {
            log.error("Unexpected error in appointment demo: {}", e.getMessage(), e);
        }

        // ----------------------------------------------------------------
        // 9. findAll повертає незмінний список
        // ----------------------------------------------------------------
        System.out.println("\n--- 9. findAll() повертає захищений незмінний список ---");
        List<BeautyClient> snapshot = clientRepo.findAll();
        try {
            snapshot.add(client3);
        } catch (UnsupportedOperationException e) {
            System.out.println("Спроба змінити список з findAll() → UnsupportedOperationException (очікувано)");
        }

        // ----------------------------------------------------------------
        // 10. Підсумок та демонстрація логування
        // ----------------------------------------------------------------
        System.out.println("\n--- 10. Підсумок логування трьох рівнів (INFO / WARN / ERROR) ---");
        System.out.println("Усі 3 рівні логування зафіксовані в консолі:");
        System.out.println("  • INFO  — додавання та видалення сутностей, читання CSV-файлу;");
        System.out.println("  • WARN  — відхилені дублікати, невдалі пошуки, биті рядки CSV, помилка сеттера;");
        System.out.println("  • ERROR — спроба читання неіснуючого файлу.");
        log.info("Lab 4 execution finished successfully: clients={}, masters={}, services={}",
                clientRepo.size(), masterRepo.size(), serviceRepo.size());

        System.out.println("\nДемонстрацію Лабораторної роботи №4 успішно завершено!");
    }
}
