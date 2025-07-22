package dev.plant.backend.domain.transaction.service;

import dev.plant.backend.domain.transaction.domain.Transaction;
import dev.plant.backend.domain.transaction.dto.DailyRecordResponse;
import dev.plant.backend.domain.transaction.dto.MonthlyRecordResponse;
import dev.plant.backend.domain.transaction.dto.RecordDetailDto;
import dev.plant.backend.domain.transaction.model.Type;
import dev.plant.backend.domain.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlyRecordResponse getMonthlyRecords(Long userId, YearMonth yearMonth) {
        List<Transaction> transactions = transactionRepository.findByMonth(userId, yearMonth);

        int totalBalance = transactions.stream()
                .mapToInt(t -> t.getType() == Type.INCOME ? t.getAmount() : -t.getAmount())
                .sum();

        List<RecordDetailDto> recordDtos = transactions.stream()
                .map(t -> RecordDetailDto.builder()
                        .id(t.getId())
                        .type(t.getType().name())
                        .amount(t.getAmount())
                        .category(t.getCategory().name())
                        .memo(t.getMemo())
                        .date(t.getDate())
                        .build())
                .toList();

        return MonthlyRecordResponse.builder()
                .yearMonth(
                        yearMonth.atDay(1)
                                .atStartOfDay()
                                .toEpochSecond(ZoneOffset.UTC)
                )
                .totalBalance(totalBalance)
                .monthlyRecords(recordDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public DailyRecordResponse getDailyRecords(Long userId, LocalDate date) {
        List<Transaction> transactions = transactionRepository.findByDate(userId, date);

        int totalBalance = transactions.stream()
                .mapToInt(t -> t.getType() == Type.INCOME ? t.getAmount() : -t.getAmount())
                .sum();

        List<RecordDetailDto> recordDtos = transactions.stream()
                .map(t -> RecordDetailDto.builder()
                        .id(t.getId())
                        .type(t.getType().name())
                        .amount(t.getAmount())
                        .category(t.getCategory().name())
                        .memo(t.getMemo())
                        .date(t.getDate())
                        .build())
                .toList();

        return DailyRecordResponse.builder()
                .date(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)) // 유닉스 타임스탬프
                .totalBalance(totalBalance)
                .dailyRecords(recordDtos)
                .build();
    }
}