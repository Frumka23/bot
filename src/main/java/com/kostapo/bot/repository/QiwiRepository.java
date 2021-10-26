package com.kostapo.bot.repository;

import com.kostapo.bot.model.Qiwi;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface QiwiRepository extends CrudRepository<Qiwi,Integer> {


    @Query(value = "SELECT idPay from Qiwi where data = (SELECT max(data) from Qiwi) and user_id IN (:userId)")
    String findByBillId(String userId);



    @Query(value = "from Qiwi where data < (:limit) and status = 'WAITING'")
    List<Qiwi> Data(ZonedDateTime limit);

    @Query(value = "select status from Qiwi where data = (SELECT max(data) from Qiwi) and user_id IN (:userId)")
    String findStatus(String userId);

    @Query(value = "select sum(amount) from Qiwi where status = 'PAID'")
    String sumBalance();

    @Query(value = "select sum(amount) from Qiwi where status = 'WAITING'")
    String sumNoBalance();

    @Transactional
    @Modifying
    @Query(value = "update Qiwi set status = 'PAID' where idPay = (:idPayment)")
    void updateStatus(String idPayment);

}
