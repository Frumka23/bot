package com.kostapo.bot.repository;

import com.kostapo.bot.model.User;
import com.kostapo.bot.model.Withdraw;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface WithdrawRepository extends CrudRepository<Withdraw, Integer> {

    List<Withdraw> findAll();

    List<Withdraw> findAllByStatus(String status);

    @Query(value = "SELECT max(id_draw) from Withdraw where id_user = (:userId)")
    String getIdDraw(String userId);

    @Query(value = "select sum(amount) from Withdraw where status = 'SUCCESS'")
    String sumWithdraw();

    @Transactional
    @Modifying
    @Query(value = "update Withdraw set status = 'SUCCESS' where id_draw = (:id_draw)")
    void updateStatus(Integer id_draw);

    @Transactional
    @Modifying
    @Query(value = "update Withdraw set purse = (:purse) where id_user = (:id_user)")
    void updatePurse(String purse, String id_user);

}
