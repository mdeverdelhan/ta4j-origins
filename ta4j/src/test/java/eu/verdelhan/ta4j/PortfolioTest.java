package eu.verdelhan.ta4j;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class PortfolioTest {

    private TradingRecord emptyRecord, openedRecord1, openedRecord2, closedRecord;
    private Portfolio allRecords, noRecords;

    @Before
    public void setUp() {
        emptyRecord = new TradingRecord();
        openedRecord1 = new TradingRecord(Order.buyAt(0), Order.sellAt(3),
                Order.buyAt(7));
        openedRecord2 = new TradingRecord(Order.buyAt(1), Order.sellAt(4),
                Order.buyAt(8));
        closedRecord = new TradingRecord(Order.buyAt(0), Order.sellAt(3),
                Order.buyAt(7), Order.sellAt(8));
        
        noRecords = new Portfolio();
        allRecords = new Portfolio(emptyRecord, openedRecord1, openedRecord2, closedRecord);
    }

    @Test
    public void getCurrentTrade() {
    	assertFalse(noRecords.getOpenTrades().iterator().hasNext());
    	assertTrue(allRecords.getOpenTrades().iterator().hasNext());
    }

    @Test
    public void getTradeCount() {
        assertEquals(0, noRecords.getTradeCount());
        assertEquals(4, allRecords.getTradeCount());
    }

}
