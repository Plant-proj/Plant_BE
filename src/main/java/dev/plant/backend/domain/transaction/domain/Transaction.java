package dev.plant.backend.domain.transaction.domain;

import dev.plant.backend.domain.transaction.model.Category;
import dev.plant.backend.domain.transaction.model.Type;
import dev.plant.backend.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer amount = 0;

    @Column(length = 255)
    private String memo;

    @Column(nullable = false)
    private Long date;

    @Builder
    public Transaction(Type type, Category category, String title, Integer amount, String memo, Long date) {
        this.type = type;
        this.category = category;
        this.title = title;
        this.amount = amount;
        this.memo = memo;
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getTransactions().contains(this)) {
            user.getTransactions().add(this);
        }
    }


}
