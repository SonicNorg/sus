package com.tieto.core.sus.repository;

import com.tieto.core.sus.model.DataEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SusRepositoryTest {

    private final JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);

    private final SusRepository susRepository = new SusRepository(jdbcTemplate);

    private static final String ACCOUNT_ID = "123456";
    private static final String MSISDN = "9993332211";
    private static final String STATUS = "testStatus";
    private static final PermissionDeniedDataAccessException TEST_EXC = new PermissionDeniedDataAccessException("test exc", null);

    @Test
    public void findByAccountIdSuccessTest() {
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<Object>) Mockito.any()))
                .thenReturn(Collections.singletonList(expected));
        DataEntity actual = susRepository.findByAccountId(ACCOUNT_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void findByAccountIdZeroSizeTest() {
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<Object>) Mockito.any()))
                .thenReturn(Collections.emptyList());
        DataEntity actual = susRepository.findByAccountId(ACCOUNT_ID);
        assertNull(actual);
    }

    @Test
    public void findByAccountIdExceptionTest() {
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<Object>) Mockito.any()))
                .thenThrow(TEST_EXC);
        try {
            susRepository.findByAccountId(ACCOUNT_ID);
        } catch (DataAccessException ex) {
            Assert.isTrue(ex instanceof PermissionDeniedDataAccessException, "expected exception");
        }
    }

    @Test
    public void findByAccountIdAndMsisdnSuccessTest() {
        DataEntity expected = new DataEntity(MSISDN, ACCOUNT_ID, STATUS);
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<Object>) Mockito.any()))
                .thenReturn(Collections.singletonList(expected));
        DataEntity actual = susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        assertEquals(expected, actual);
    }

    @Test
    public void findByAccountIdAndMsisdnZeroSizeTest() {
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<Object>) Mockito.any()))
                .thenReturn(Collections.emptyList());
        DataEntity actual = susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        assertNull(actual);
    }

    @Test
    public void findByAccountIdAndMsisdnExceptionTest() {
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyVararg(), (RowMapper<Object>) Mockito.any()))
                .thenThrow(TEST_EXC);
        try {
            susRepository.findByAccountIdAndMsisdn(ACCOUNT_ID, MSISDN);
        } catch (DataAccessException ex) {
            Assert.isTrue(ex instanceof PermissionDeniedDataAccessException, "expected exception");
        }
    }

    @Test
    public void updateDataEntityWithMsisdnSuccessTest() {
        int invocationCount = 1;
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(invocationCount);
        int i = susRepository.updateDataEntity(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(invocationCount, i);
        verify(jdbcTemplate, times(invocationCount)).update(Mockito.anyString(), (Object[]) Mockito.anyVararg());
    }

    @Test
    public void updateDataEntityWithOutMsisdnSuccessTest() {
        int invocationCount = 1;
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(invocationCount);
        int i = susRepository.updateDataEntity(ACCOUNT_ID, STATUS, null);
        assertEquals(invocationCount, i);
        verify(jdbcTemplate, times(invocationCount)).update(Mockito.anyString(), (Object[]) Mockito.anyVararg());
    }

    @Test
    public void updateDataEntityWithOutMsisdnExceptionTest() {
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenThrow(TEST_EXC);
        try {
            susRepository.updateDataEntity(ACCOUNT_ID, STATUS, null);
        } catch (DataAccessException ex) {
            Assert.isTrue(ex instanceof PermissionDeniedDataAccessException, "expected exception");
        }
    }

    @Test
    public void updateDataEntityWithMsisdnExceptionTest() {
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenThrow(TEST_EXC);
        try {
            susRepository.updateDataEntity(ACCOUNT_ID, STATUS, MSISDN);
        } catch (DataAccessException ex) {
            Assert.isTrue(ex instanceof PermissionDeniedDataAccessException, "expected exception");
        }
    }

    @Test
    public void createDataEntitySuccessTest() {
        int invocationCount = 1;
        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.anyVararg())).thenReturn(invocationCount);
        int i = susRepository.createDataEntity(ACCOUNT_ID, STATUS, MSISDN);
        assertEquals(invocationCount, i);
        verify(jdbcTemplate, times(invocationCount)).update(Mockito.anyString(), (Object[]) Mockito.anyVararg());
    }
}
