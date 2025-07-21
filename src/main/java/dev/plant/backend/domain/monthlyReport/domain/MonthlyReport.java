package dev.plant.backend.domain.monthlyReport.domain;

import dev.plant.backend.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monthly_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    //default 0으로 설정

    @Column(nullable = false)
    private Integer total_income = 0;

    @Column(nullable = false)
    private Integer total_expense = 0;

    @Column(nullable = false)
    private Integer total_balance = 0;

    @Column(nullable = false)
    private Long date;

    @Builder
    public MonthlyReport(Integer total_income, Integer total_expense, Integer total_balance, Long date) {
        this.total_income = total_income;
        this.total_expense = total_expense;
        this.total_balance = total_balance;
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getMonthlyReports().contains(this)) {
            user.getMonthlyReports().add(this);
        }
    }
}
