package com.dbtraining.reconx.service;

import com.dbtraining.reconx.repository.entity.Counterparty;
import com.dbtraining.reconx.repository.entity.Trade;
import com.dbtraining.reconx.repository.CounterpartyRepository;
import com.dbtraining.reconx.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TradeLookupServiceTest {

    @Mock
    private TradeRepository tradeRepo;

    @Mock
    private CounterpartyRepository cpRepo;

    @InjectMocks
    private TradeLookupService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCounterpartyForTradeRef_found() {
        Counterparty cp = new Counterparty();
        java.lang.reflect.Field idField = null;
        try {
            idField = Counterparty.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(cp, 100L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        cp.setName("TEST_CP");

        Trade trade = new Trade();
        trade.setCounterparty(cp);
        trade.setTradeRef("TRD-1");

        when(tradeRepo.findByTradeRef("TRD-1")).thenReturn(Optional.of(trade));
        when(cpRepo.findById(100L)).thenReturn(Optional.of(cp));

        Counterparty result = service.counterpartyForTradeRef("TRD-1");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TEST_CP");
    }

    @Test
    void testCounterpartyForTradeRef_tradeMissing() {
        when(tradeRepo.findByTradeRef("TRD-2")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.counterpartyForTradeRef("TRD-2"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("TRD-2");
    }

    @Test
    void testCounterpartyForTradeRef_counterpartyMissing() {
        Counterparty cp = new Counterparty();
        try {
            java.lang.reflect.Field idField = Counterparty.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(cp, 999L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        Trade trade = new Trade();
        trade.setCounterparty(cp);
        trade.setTradeRef("TRD-3");

        when(tradeRepo.findByTradeRef("TRD-3")).thenReturn(Optional.of(trade));
        when(cpRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.counterpartyForTradeRef("TRD-3"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("TRD-3");
    }
}
