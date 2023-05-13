package com.tanthanh.accountservice.repository;

import com.tanthanh.accountservice.data.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<Account,String> {
}
