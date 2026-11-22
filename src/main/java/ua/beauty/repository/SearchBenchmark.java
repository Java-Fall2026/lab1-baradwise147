package ua.beauty.repository;

import ua.beauty.model.BeautyService;
import ua.beauty.model.ServiceCategory;

import java.util.List;
import java.util.Optional;

/**
 * Benchmarks Map-based search (via {@link GenericRepository#findByIdentity})
 * against a linear List scan for a large collection of {@link BeautyService} items.
 *
 * <p>Three scenarios are measured:
 * <ol>
 *   <li>Target is the first element (best case for linear scan)</li>
 *   <li>Target is the last element (worst case for linear scan)</li>
 *   <li>Target is absent (worst case for both, full traversal for list)</li>
 * </ol>
 *
 * <p>Each measurement repeats {@value #RUNS} times after a warm-up pass;
 * the minimum run time is reported to reduce JIT noise.</p>
 */
public class SearchBenchmark {

    private static final int SIZE = 100_000;
    private static final int RUNS = 7;

    public static void main(String[] args) {
        System.out.println("=== SearchBenchmark: Map vs List, N=" + SIZE + " ===\n");

        // Build repository and extract the flat list once — before any timing
        GenericRepository<BeautyService> repo = buildRepository(SIZE);
        List<BeautyService> list = repo.findAll();      // snapshot taken once
        IdentityExtractor<BeautyService> extractor = BeautyService::getServiceCode;

        String firstId  = list.get(0).getServiceCode();
        String lastId   = list.get(SIZE - 1).getServiceCode();
        String absentId = "SRV-MISSING";

        // Warm-up (not measured)
        for (int i = 0; i < 3; i++) {
            repo.findByIdentity(firstId);
            linearSearch(list, firstId, extractor);
        }

        System.out.printf("%-30s %12s %12s%n", "Scenario", "Map (ns)", "List (ns)");
        System.out.println("-".repeat(56));

        measure("First element (best case)",  repo, list, firstId,  extractor);
        measure("Last element (worst case)",  repo, list, lastId,   extractor);
        measure("Absent element (miss)",      repo, list, absentId, extractor);

        System.out.println();
        System.out.println("Conclusion:");
        System.out.println("  Map lookup is O(1) and stays near-constant across all scenarios.");
        System.out.println("  List scan is O(n): fastest for the first element,");
        System.out.println("  but up to ~" + SIZE + "x slower for last/absent elements.");
        System.out.println("  For large collections, GenericRepository (LinkedHashMap) is the");
        System.out.println("  right choice whenever identity-based lookup is the primary operation.");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void measure(String label,
                                GenericRepository<BeautyService> repo,
                                List<BeautyService> list,
                                String identity,
                                IdentityExtractor<BeautyService> extractor) {
        long mapMin  = Long.MAX_VALUE;
        long listMin = Long.MAX_VALUE;

        for (int i = 0; i < RUNS; i++) {
            long t0 = System.nanoTime();
            repo.findByIdentity(identity);
            long t1 = System.nanoTime();
            linearSearch(list, identity, extractor);
            long t2 = System.nanoTime();

            mapMin  = Math.min(mapMin,  t1 - t0);
            listMin = Math.min(listMin, t2 - t1);
        }

        System.out.printf("%-30s %12d %12d%n", label, mapMin, listMin);
    }

    /**
     * Generic linear search over a list using an {@link IdentityExtractor}.
     * This method is intentionally not part of {@link GenericRepository};
     * it simulates what callers would have to do if the repository exposed only a list.
     *
     * @param items     the list to scan
     * @param identity  the identity to look for
     * @param extractor how to obtain the identity from each element
     * @param <E>       the element type
     * @return an {@link Optional} with the first matching element, or empty
     */
    private static <E> Optional<E> linearSearch(List<E> items,
                                                String identity,
                                                IdentityExtractor<E> extractor) {
        for (E item : items) {
            if (identity.equals(extractor.identityOf(item))) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /**
     * Builds a {@link GenericRepository} pre-populated with {@code count} generated services.
     */
    private static GenericRepository<BeautyService> buildRepository(int count) {
        GenericRepository<BeautyService> repo =
                new GenericRepository<>(BeautyService.class, BeautyService::getServiceCode);

        ServiceCategory[] categories = ServiceCategory.values();
        for (int i = 0; i < count; i++) {
            String code = String.format("SRV-%06d", i);
            ServiceCategory cat = categories[i % categories.length];
            repo.add(new BeautyService(code, "Service " + i, cat, 30 + (i % 90), 100.0 + i));
        }
        return repo;
    }
}
