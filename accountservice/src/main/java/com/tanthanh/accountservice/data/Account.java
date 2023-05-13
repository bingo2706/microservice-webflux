package com.tanthanh.accountservice.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table
public class Account {
    @Id
    private String id;
    private String email;
    private String currency;
    private double balance;
    private double reserved;

    @Version
    private Long version;

    public Account(String id, String email, String currency, double balance, double reserved) {
        this.id = id;
        this.email = email;
        this.currency = currency;
        this.balance = balance;
        this.reserved = reserved;
    }
}
