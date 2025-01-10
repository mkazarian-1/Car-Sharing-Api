package org.example.carsharingapi.telegram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.User;

@Entity
@Getter
@Setter
@Table(name = "telegram_users")
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "chat_id",nullable = false)
    private long chatId;
}
