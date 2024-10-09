package com.universityweb.payment;

import com.universityweb.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepos extends JpaRepository<Payment, Long> {
    Page<Payment> findByUserUsername(String username, Pageable pageable);

    Page<Payment> findByUserUsernameAndStatus(String username, Payment.EStatus status, Pageable pageable);
}
