package com.tanthanh.profileservice.repository;

import com.tanthanh.profileservice.data.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProfileRepository extends ReactiveCrudRepository<Profile,Long> {
}
