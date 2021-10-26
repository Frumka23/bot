package com.kostapo.bot.repository;

import com.kostapo.bot.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll();

    @Query(value = "select id from User")
    List<Integer> findId();

    @Query(value = "SELECT id,balance,chat_id,level,referral,user_name,payment,purse,Ban FROM User where id = (:id)")
    Object findUserById(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set level = 1 where id = (:id)")
    void updateLvL_1(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set level = 2 where id = (:id)")
    void updateLvL_2(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set level = (:lvl) where id = (:id)")
    void updateLvL(Integer lvl ,Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set payment = payment + (:pay) where id = (:id)")
    void updatePayment(BigDecimal pay, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance = (:balance) where id = (:id)")
    void updateBalance(Double balance, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance = balance + (:balance) where id = (:id)")
    void updatePlusBalance(Double balance, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance = balance - (:balance) where id = (:id)")
    void updateMinusBalance(Double balance, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set purse = (:purse) where id = (:id)")
    void updatePurse(String purse, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set referral = (:ref) where id = (:id)")
    void updateRef(String ref, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set Ban = (:ban) where id = (:id)")
    void ban(Boolean ban, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set Ban = (:ban) where id = (:id)")
    void unban(Boolean ban, Integer id);

    @Query(value = "select id from User where id = (:id)")
    Integer getId(Integer id);

    @Query(value = "select balance from User where id = (:id)")
    Double getBalance(Integer id);

    @Query(value = "select chat_id from User where id = (:id)")
    Integer getchatId(Integer id);

    @Query(value = "select level from User where id = (:id)")
    Integer getlevel(Integer id);

    @Query(value = "select referral from User where id = (:id)")
    String getReferral(Integer id);

    @Query(value = "select user_name from User where id = (:id)")
    String getUsername(Integer id);

    @Query(value = "select payment from User where id = (:id)")
    BigDecimal getPay(Integer id);

    @Query(value = "select purse from User where id = (:id)")
    String getPurse(Integer id);

    @Query(value = "select Ban from User where id = (:id)")
    Boolean getBan(Integer id);





    List<User> findAllByReferral(String referral);
}
