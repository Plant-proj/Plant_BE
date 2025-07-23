package dev.plant.backend.domain.transaction.repository;

import dev.plant.backend.domain.transaction.domain.Transaction;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {

    private final EntityManager em;

    public List<Transaction> findByMonth(Long userId, YearMonth yearMonth) {
        long start = yearMonth.atDay(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long end = yearMonth.atEndOfMonth().atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        return em.createQuery("""
                SELECT t FROM Transaction t
                WHERE t.user.id = :userId AND t.date BETWEEN :start AND :end
                """, Transaction.class)
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<Transaction> findByDate(Long userId, LocalDate date) {
        long startOfDay = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long endOfDay = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) - 1;

        return em.createQuery("""
                SELECT t FROM Transaction t
                WHERE t.user.id = :userId AND t.date BETWEEN :start AND :end
                """, Transaction.class)
                .setParameter("userId", userId)
                .setParameter("start", startOfDay)
                .setParameter("end", endOfDay)
                .getResultList();
    }

    public void save(Transaction transaction) {
        em.persist(transaction);
    }
    public void delete(Transaction transaction) {
        em.remove(transaction);
    }
    public Optional<Transaction> findById(Long id) {
        Transaction transaction = em.find(Transaction.class, id);
        return Optional.ofNullable(transaction);
    }


}
