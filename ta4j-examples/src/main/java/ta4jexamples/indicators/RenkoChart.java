/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Marc de Verdelhan & respective authors (see AUTHORS)
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
package ta4jexamples.indicators;


import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.joda.time.Period;
import ta4jexamples.loaders.CsvTradesLoader;

import java.awt.*;
import java.util.Date;


/**
 * This class builds a traditional candlestick chart.
 */
public class RenkoChart {

    /**
     * Builds a JFreeChart OHLC dataset from a ta4j time series.
     * @param series a time series
     * @return an Open-High-Low-Close dataset
     */
    private static OHLCDataset createRenkoDataset(TimeSeries series, int amplitude) {
        final int nbTicks = series.getTickCount();
        Date[] dates = new Date[nbTicks];
        double[] opens = new double[nbTicks];
        double[] highs = new double[nbTicks];
        double[] lows = new double[nbTicks];
        double[] closes = new double[nbTicks];
        double[] volumes = new double[nbTicks];
        Double initialValue = series.getFirstTick().getOpenPrice().toDouble();
        Double initialVolume = series.getFirstTick().getVolume().toDouble();
        int goBack = 0;
        for (int i = 0; i < nbTicks; i++) {
            Tick tick = series.getTick(i);
            Double currentValue = tick.getClosePrice().toDouble();
            Date currentDate = tick.getEndTime().toDate();
            Double currentVolume = tick.getVolume().toDouble();
            initialVolume += currentVolume;
            Double targetHigh = initialValue + amplitude;
            Double targetLow = initialValue - amplitude;
            if (currentValue > targetHigh || currentValue < targetLow) {
                dates[i] = currentDate;
                opens[i] = initialValue;
                volumes[i] = initialVolume;
                Double newValue;
                if(currentValue > initialValue) {
                    newValue = targetHigh + Math.floor((currentValue - targetHigh)/amplitude)*amplitude;
                    highs[i] = newValue;
                    lows[i] = initialValue;
                } else {
                    newValue = targetLow - Math.floor((targetLow - currentValue)/amplitude)*amplitude;
                    lows[i] = newValue;
                    highs[i] = initialValue;
                }
                closes[i] = newValue;
                for(int j = i - goBack; j < i; j++) {
                    dates[j] = currentDate;
                    opens[j] = initialValue;
                    volumes[j] = initialVolume;
                    closes[j] = newValue;
                    if(currentValue > initialValue) {
                        highs[j] = newValue;
                        lows[j] = initialValue;
                    } else {
                        lows[j] = newValue;
                        highs[j] = initialValue;
                    }
                }
                initialValue = newValue;
                initialVolume = 0.0;
                goBack = 0;
            } else {
                goBack++;
            }
        }

        Date lastDate = series.getTick(nbTicks - goBack).getEndTime().toDate();
        Double lastOpen = initialValue;
        Double lastClose = series.getLastTick().getClosePrice().toDouble();
        Double lastHigh = Math.max(lastOpen, lastClose);
        Double lastLow = Math.min(lastOpen, lastClose);
        Double lastVolume = series.getTick(nbTicks - goBack).getVolume().toDouble();
        for(int i = nbTicks - goBack + 1; i < nbTicks - 1; i++){
            Tick currentTick = series.getTick(i);
            Double currentValue = currentTick.getClosePrice().toDouble();
            lastVolume += currentTick.getVolume().toDouble();
            if(currentValue > lastHigh) {
                lastHigh = currentValue;
            } else if(currentValue < lastLow) {
                lastLow = currentValue;
            }
        }
        for(int i = nbTicks - goBack; i < nbTicks; i++){
            dates[i] = lastDate;
            opens[i] = lastOpen;
            highs[i] = lastHigh;
            lows[i] = lastLow;
            closes[i] = lastClose;
            volumes[i] = lastVolume;
        }

        return new DefaultHighLowDataset("btc", dates, highs, lows, opens, closes, volumes);
    }

    /**
     * Displays a chart in a frame.
     * @param chart the chart to be displayed
     */
    private static void displayChart(JFreeChart chart) {
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new java.awt.Dimension(740, 300));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame("Ta4j example - Candlestick chart");
        frame.setContentPane(panel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        /**
         * Getting time series
         */
        TimeSeries series = CsvTradesLoader.loadBitstampSeries().subseries(0, Period.hours(48));

        /**
         * Creating the OHLC dataset
         */
        OHLCDataset ohlcDataset = createRenkoDataset(series, 8);

        /**
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createCandlestickChart(
                "Bitstamp BTC price",
                "Time",
                "USD",
                ohlcDataset,
                true);
        // Candlestick rendering
        CandlestickRenderer renderer = new CandlestickRenderer();
        renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(renderer);
        // Misc
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setBackgroundPaint(Color.white);
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoRangeIncludesZero(false);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        /**
         * Displaying the chart
         */
        displayChart(chart);
    }
}
