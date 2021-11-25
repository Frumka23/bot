package com.kostapo.bot.repository;

import com.kostapo.bot.model.Qiwi;
import com.kostapo.bot.model.QiwiVkl;
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
public interface QiwiVklRepository extends CrudRepository<QiwiVkl,Integer> {


    @Query(value = "SELECT idPay from QiwiVkl where data = (SELECT max(data) from QiwiVkl) and user_id IN (:userId)")
    String findByBillId(String userId);



    @Query(value = "from QiwiVkl where data < (:limit) and status = 'WAITING'")
    List<QiwiVkl> Data(ZonedDateTime limit);

    @Query(value = "select status from QiwiVkl where data = (SELECT max(data) from QiwiVkl) and user_id IN (:userId)")
    String findStatus(String userId);

    @Query(value = "select sum(amount) from QiwiVkl where status = 'PAID'")
    String sumBalance();

    @Query(value = "select sum(amount) from QiwiVkl where status = 'WAITING'")
    String sumNoBalance();

    @Transactional
    @Modifying
    @Query(value = "update QiwiVkl set status = 'PAID' where idPay = (:idPayment)")
    void updateStatus(String idPayment);

    @Query(value = "select amount from QiwiVkl where idPay = (:pay)")
    String sumBalanceVkl(String pay);

}
