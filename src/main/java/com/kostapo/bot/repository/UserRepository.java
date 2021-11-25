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

    @Query(value = "select id from User where Ban = false and block = false")
    List<Integer> findUserBan();

    @Query(value = "select id from User where level = (:lvl)")
    List<Integer> findLvl(Integer lvl);


    @Query(value = "SELECT id,balance,chat_id,level,referral,user_name,payment,purse,Ban FROM User where id = (:id)")
    Object findUserById(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set level = 1 where id = (:id)")
    void updateLvL_1(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set level = level + 1 where id = (:id)")
    void updateLevel(Integer id);

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
    @Query(value = "UPDATE User set payment = (:pay) where id = (:id)")
    void updatePayment_Num(BigDecimal pay, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance = (:balance) where id = (:id)")
    void updateBalance(Double balance, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance_vkl = (:balance) where id = (:id)")
    void updateBalanceVkl(Double balance, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance_vkl = balance_vkl + (:balance) where id = (:id)")
    void updatePlusBalanceVkl(Double balance, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set balance_vkl = balance_vkl - (:balance) where id = (:id)")
    void updateMinusBalanceVkl(Double balance, Integer id);

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
    @Query(value = "UPDATE User set user_name = (:username) where id = (:id)")
    void updateUsername(String username, Integer id);

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
    @Query(value = "UPDATE User set block = (:block) where id = (:id)")
    void block(Boolean block, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set Ban = (:ban) where id = (:id)")
    void unban(Boolean ban, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set intScore = (:score) where id = 1016547568")
    void updateIntScore(Integer score);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set intScore = intScore + 1 where id = 1016547568")
    void setIntScore();

    @Query(value = "select intScore from User where id = 1016547568")
    Integer intScore();

    @Query(value = "select id from User where id = (:id)")
    Integer getId(Integer id);

    @Query(value = "select id from User where id = 1016547568")
    Integer getUser();

    @Query(value = "select balance from User where id = (:id)")
    Double getBalance(Integer id);

    @Query(value = "select balance_vkl from User where id = (:id)")
    Double getBalanceVkl(Integer id);

    @Query(value = "select chat_id from User where id = (:id)")
    Integer getchatId(Integer id);

    @Query(value = "select level from User where id = (:id)")
    Integer getlevel(Integer id);

    @Query(value = "select referral from User where id = (:id)")
    String getReferral(Integer id);

    @Query(value = "select Referral_2 from User where id = (:id)")
    String getReferral_2(Integer id);

    @Query(value = "select user_name from User where id = (:id)")
    String getUsername(Integer id);

    @Query(value = "select payment from User where id = (:id)")
    BigDecimal getPay(Integer id);

    @Query(value = "select purse from User where id = (:id)")
    String getPurse(Integer id);

    @Query(value = "select  Ban from User where id = (:id)")
    Boolean getBan(Integer id);

    @Query(value = "select  block from User where id = (:id)")
    Boolean getBlock(Integer id);

    @Query(value = "select lastMessage from User where id = (:id)")
    String getLastMessage(Integer id);

    @Query(value = "select password from User where id = (:id)")
    String getPassword(Integer id);

    @Query(value = "select top from User where id = (:id)")
    Double getTop(Integer id);

    @Query(value = "select unt from User where id = (:id)")
    Double getUnt(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set lastMessage = (:lastMessage) where id = (:id)")
    void updateLastMessage(String lastMessage, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set top = (:top) where id = (:id)")
    void updateTop(Double top, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set block = true where id = (:id)")
    void updateBlockTrue(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set block = false where id = (:id)")
    void updateBlockFalse(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set unt = (:unt) where id = (:id)")
    void updateUnt(Double unt, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set unt = unt + (:unt) where id = (:id)")
    void updatePlusUnt(Double unt, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set unt = unt - (:unt) where id = (:id)")
    void updateMinusUnt(Double unt, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User set password = (:pass) where id = (:id)")
    void updatePass(String pass, Integer id);


    @Query(value = "SELECT Referral_2 FROM User where Referral_2 = (:referral)")
    List<String> ref_2_lvl(String referral);

    @Query(value = "SELECT Referral_2 FROM User where Referral_2 = (:referral) and level >= 5")
    List<String> ref_2_lvl_5(String referral);

    @Query(value = "SELECT referral FROM User where referral = (:referral) and level = 10")
    List<String> ref_lvl_10(String referral);


    List<User> findAllByReferral(String referral);
}
