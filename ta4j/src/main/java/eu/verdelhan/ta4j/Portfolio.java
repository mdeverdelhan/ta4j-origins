/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.verdelhan.ta4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import eu.verdelhan.ta4j.util.ExtendableIterator;

/**
 * A history/record of multiple trading sessions.
 * <p>
 * Holds the full trading record when running a {@link Strategy strategy}.
 * It is used to:
 * <ul>
 * <li>check to satisfaction of some trading rules (when running a strategy)
 * <li>combine the long and short performance of a strategy
 * <li>analyze the performance of a trading strategy over multiple markets/sessions
 * </ul>
 */
public class Portfolio implements Serializable, TradesRecord {

	private static final long serialVersionUID = 574766583620749767L;
	
	private List<TradingRecord> tradingRecords;

	public Portfolio() {
		tradingRecords = new ArrayList<TradingRecord>();
	}
	
	public Portfolio(TradingRecord... tradingRecords) {
		this.tradingRecords = Arrays.asList(tradingRecords);
	}
	
	public Portfolio(List<TradingRecord> tradingRecords) {
		this.tradingRecords = tradingRecords;
	}

	@Override
	public Iterator<Trade> iterator() {
		ExtendableIterator<Trade> itr = new ExtendableIterator<Trade>();
		for(TradingRecord record : tradingRecords) {
			itr.extend(record.iterator());
		}
		return itr;
	}
	
	@Override
	public Iterable<Trade> getOpenTrades() {
		List<Trade> list = new ArrayList<Trade>();
		for(TradingRecord record : tradingRecords) {
			for(Trade trade : record.getOpenTrades()) {
				list.add(trade);
			}
		}
		return list;
	}
	
	@Override
	public int getTradeCount() {
		int trades = 0;
		for(TradingRecord record : tradingRecords) {
			trades += record.getTradeCount();
		}
		return trades;
	}
	
	public List<TradingRecord> getTradingRecords() {
		return tradingRecords;
	}

	public void setTradingRecords(List<TradingRecord> tradingRecords) {
		this.tradingRecords = tradingRecords;
	}

}
