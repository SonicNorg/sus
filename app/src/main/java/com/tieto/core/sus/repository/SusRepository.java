package com.tieto.core.sus.repository;

import lombok.extern.slf4j.Slf4j;
import com.tieto.core.sus.model.DataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SusRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String select_by_accountid_query = "SELECT * FROM LSMSISDN WHERE ACCOUNTID = ?";
    private static final String select_by_accountid_and_msisdn_query = "SELECT * FROM LSMSISDN WHERE ACCOUNTID = ? AND MSISDN = ?";
    private static final String update_query_with_msisdn = "UPDATE LSMSISDN SET STATUS = ?, MSISDN = ? WHERE ACCOUNTID = ?";
    private static final String update_query_without_msisdn = "UPDATE LSMSISDN SET STATUS = ? WHERE ACCOUNTID = ?";

    @Autowired
    public SusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DataEntity findByAccountId(String accountId) throws DataAccessException {
        List<DataEntity> result = jdbcTemplate.query(select_by_accountid_query, new Object[]{accountId}, (rs, rowNum) -> new DataEntity(
                rs.getString("msisdn"), rs.getString("accountId"), rs.getString("status")));
        return result.isEmpty() ? null : result.get(0);
    }

    public DataEntity findByAccountIdAndMsisdn(String accountId, String msisdn) throws DataAccessException {
        List<DataEntity> result = jdbcTemplate.query(select_by_accountid_and_msisdn_query, new Object[]{accountId, msisdn}, (rs, rowNum) -> new DataEntity(
                rs.getString("msisdn"), rs.getString("accountId"), rs.getString("status")));
        return result.isEmpty() ? null : result.get(0);
    }

    public int updateDataEntity(String accountId, String status, String msisdn) throws DataAccessException {
        if (msisdn != null) {
            return jdbcTemplate.update(update_query_with_msisdn, status, msisdn, accountId);
        } else {
            return jdbcTemplate.update(update_query_without_msisdn, status, accountId);
        }
    }


}
