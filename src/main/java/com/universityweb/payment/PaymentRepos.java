package com.universityweb.payment;

import com.universityweb.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepos extends JpaRepository<Payment, Long> {
}
