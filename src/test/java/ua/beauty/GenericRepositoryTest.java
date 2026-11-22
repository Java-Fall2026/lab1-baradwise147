package ua.beauty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.beauty.exception.DuplicateIdentityException;
import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.Master;
import ua.beauty.model.ServiceCategory;
import ua.beauty.repository.GenericRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GenericRepository}.
 * No disk files are touched — all data is constructed in-memory.
 */
class GenericRepositoryTest {

    private GenericRepository<BeautyService> serviceRepo;
    private BeautyService haircut;
    private BeautyService manicure;

    @BeforeEach
    void setUp() {
        serviceRepo = new GenericRepository<>(BeautyService.class, BeautyService::getServiceCode);
        haircut  = new BeautyService("SRV-H01", "Classic Haircut",  ServiceCategory.HAIRCUT,  45, 450.0);
        manicure = new BeautyService("SRV-M01", "Gel Manicure",     ServiceCategory.MANICURE, 60, 550.0);
    }

    // ------------------------------------------------------------------
    // 1. add + findByIdentity — happy path
    // ------------------------------------------------------------------
    @Test
    @DisplayName("add() stores item that is then found by its identity")
    void add_thenFindByIdentity_returnsItem() {
        serviceRepo.add(haircut);
        Optional<BeautyService> result = serviceRepo.findByIdentity("SRV-H01");
        assertTrue(result.isPresent());
        assertSame(haircut, result.get());
    }

    // ------------------------------------------------------------------
    // 2. findByIdentity — absent key returns empty Optional (never null)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("findByIdentity() returns empty Optional for unknown identity")
    void findByIdentity_unknownKey_returnsEmpty() {
        Optional<BeautyService> result = serviceRepo.findByIdentity("SRV-UNKNOWN");
        assertNotNull(result, "Optional itself must not be null");
        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------------------------
    // 3. add duplicate throws DuplicateIdentityException
    // ------------------------------------------------------------------
    @Test
    @DisplayName("add() throws DuplicateIdentityException when identity already present")
    void add_duplicateIdentity_throwsDuplicateIdentityException() {
        serviceRepo.add(haircut);
        BeautyService sameCode = new BeautyService("SRV-H01", "Another Haircut",
                ServiceCategory.HAIRCUT, 30, 350.0);
        assertThrows(DuplicateIdentityException.class, () -> serviceRepo.add(sameCode));
    }

    // ------------------------------------------------------------------
    // 4. addIfAbsent returns false without changing size
    // ------------------------------------------------------------------
    @Test
    @DisplayName("addIfAbsent() returns false and size stays the same for a duplicate")
    void addIfAbsent_duplicate_returnsFalseAndSizeUnchanged() {
        serviceRepo.add(haircut);
        int before = serviceRepo.size();
        BeautyService duplicate = new BeautyService("SRV-H01", "Copy Haircut",
                ServiceCategory.HAIRCUT, 30, 300.0);
        boolean added = serviceRepo.addIfAbsent(duplicate);
        assertFalse(added);
        assertEquals(before, serviceRepo.size());
    }

    // ------------------------------------------------------------------
    // 5. removeByIdentity — existing item is removed
    // ------------------------------------------------------------------
    @Test
    @DisplayName("removeByIdentity() returns true and item is gone afterwards")
    void removeByIdentity_existing_returnsTrueAndItemGone() {
        serviceRepo.add(haircut);
        boolean removed = serviceRepo.removeByIdentity("SRV-H01");
        assertTrue(removed);
        assertTrue(serviceRepo.findByIdentity("SRV-H01").isEmpty());
        assertEquals(0, serviceRepo.size());
    }

    // ------------------------------------------------------------------
    // 6. removeByIdentity — absent identity returns false
    // ------------------------------------------------------------------
    @Test
    @DisplayName("removeByIdentity() returns false when identity is not present")
    void removeByIdentity_absent_returnsFalse() {
        boolean removed = serviceRepo.removeByIdentity("SRV-GHOST");
        assertFalse(removed);
    }

    // ------------------------------------------------------------------
    // 7. findAll returns unmodifiable list
    // ------------------------------------------------------------------
    @Test
    @DisplayName("findAll() returns an unmodifiable list that cannot corrupt the repository")
    void findAll_returnsUnmodifiableList() {
        serviceRepo.add(haircut);
        List<BeautyService> snapshot = serviceRepo.findAll();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(manicure));
        assertEquals(1, serviceRepo.size(), "Repository size must stay unchanged after attempted mutation");
    }

    // ------------------------------------------------------------------
    // 8. Same repository class works with two different domain types
    // ------------------------------------------------------------------
    @Test
    @DisplayName("GenericRepository works for BeautyClient (record) and Master (record)")
    void genericRepository_worksWithMultipleDomainTypes() {
        GenericRepository<BeautyClient> clientRepo =
                new GenericRepository<>(BeautyClient.class, BeautyClient::phone);
        GenericRepository<Master> masterRepo =
                new GenericRepository<>(Master.class, Master::employeeId);

        BeautyClient client = new BeautyClient("+380671234567", "Олена Мороз",
                LocalDate.of(1995, 3, 14));
        Master master = new Master("EMP-001", "Іванна Шевченко", ServiceCategory.HAIRCUT);

        clientRepo.add(client);
        masterRepo.add(master);

        assertEquals(1, clientRepo.size());
        assertEquals(1, masterRepo.size());
        assertSame(client, clientRepo.findByIdentity("+380671234567").orElseThrow());
        assertSame(master, masterRepo.findByIdentity("EMP-001").orElseThrow());
    }

    // ------------------------------------------------------------------
    // 9. add(null) throws IllegalArgumentException
    // ------------------------------------------------------------------
    @Test
    @DisplayName("add(null) throws IllegalArgumentException")
    void add_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> serviceRepo.add(null));
    }

    // ------------------------------------------------------------------
    // 10. addAll skips duplicates silently and adds only new items
    // ------------------------------------------------------------------
    @Test
    @DisplayName("addAll() adds new items and silently skips duplicates")
    void addAll_mixedList_addsNewSkipsDuplicates() {
        serviceRepo.add(haircut);
        // manicure is new; a second haircut is a duplicate
        BeautyService duplicateHaircut = new BeautyService("SRV-H01", "Copy",
                ServiceCategory.HAIRCUT, 20, 200.0);
        serviceRepo.addAll(List.of(manicure, duplicateHaircut));
        assertEquals(2, serviceRepo.size());
        assertTrue(serviceRepo.findByIdentity("SRV-M01").isPresent());
    }
}
