package dev.plant.backend.domain.user.domain;

import dev.plant.backend.domain.monthlyReport.domain.MonthlyReport;
import dev.plant.backend.domain.transaction.domain.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    private Integer accountBalance = 0;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>(); // 거래내역을 삭제할경우, 사용자의 정보에 거래내역에서도 같이 삭제되게

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonthlyReport> monthlyReports = new ArrayList<>();

    @Builder
    public User(Long kakaoId, String nickname, String profileImage, Integer accountBalance){
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.accountBalance = accountBalance;
    }


    //User -> Transaction 편의 매서드
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        if(transaction.getUser() != this) {
            transaction.setUser(this);
        }
    }

    // User -> MonthlyReport 편의 매서드
    public void addMonthlyReport(MonthlyReport monthlyReport) {
        this.monthlyReports.add(monthlyReport);
        if (monthlyReport.getUser() != this) {
            monthlyReport.setUser(this);
        }
    }

    //잔액 입력
    public void updateAccountBalance(Integer newAccountBalance) {
        this.accountBalance = newAccountBalance != null ? newAccountBalance : 0; //사용자가 Null로 보내면 0으로 처리
    }


}
