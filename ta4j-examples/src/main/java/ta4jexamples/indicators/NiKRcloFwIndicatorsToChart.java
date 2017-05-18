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
package ta4jexamples.indicators;

import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.statistics.SimpleLinearRegressionIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsUpperIndicator;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import ta4jexamples.loaders.CsvTicksLoader;

/**
 * This class builds a graphical chart showing values from indicators.
 */
public class NiKRcloFwIndicatorsToChart {

   /**
    * Builds a JFreeChart time series from a Ta4j time series and an indicator.
    *
    * @param tickSeries the ta4j time series
    * @param indicator the indicator
    * @param name the name of the chart time series
    * @return the JFreeChart time series
    */
   private static org.jfree.data.time.TimeSeries buildChartTimeSeries(TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
      org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
      for (int i = 0; i < tickSeries.getTickCount(); i++) {
         Tick tick = tickSeries.getTick(i);
         chartTimeSeries.add(new Day(tick.getEndTime().toDate()), indicator.getValue(i).toDouble());
      }
      return chartTimeSeries;
   }

   /**
    * Displays a chart in a frame.
    *
    * @param chart the chart to be displayed
    */
   private static void displayChart(JFreeChart chart) {
      // Chart panel
      ChartPanel panel = new ChartPanel(chart);
      panel.setFillZoomRectangle(true);
      panel.setMouseWheelEnabled(true);
      panel.setPreferredSize(new java.awt.Dimension(500, 270));
      // Application frame
      ApplicationFrame frame = new ApplicationFrame("Ta4j example - Indicators to chart");
      frame.setContentPane(panel);
      frame.pack();
      RefineryUtilities.centerFrameOnScreen(frame);
      frame.setVisible(true);
   }

   /**
    * Builds a JFreeChart OHLC dataset from a ta4j time series.
    *
    * @param series a time series
    * @return an Open-High-Low-Close dataset
    */
   private static OHLCDataset createOHLCDataset(TimeSeries series) {
      final int nbTicks = series.getTickCount();

      Date[] dates = new Date[nbTicks];
      double[] opens = new double[nbTicks];
      double[] highs = new double[nbTicks];
      double[] lows = new double[nbTicks];
      double[] closes = new double[nbTicks];
      double[] volumes = new double[nbTicks];

      for (int i = 0; i < nbTicks; i++) {
         Tick tick = series.getTick(i);
         dates[i] = tick.getEndTime().toDate();
         opens[i] = tick.getOpenPrice().toDouble();
         highs[i] = tick.getMaxPrice().toDouble();
         lows[i] = tick.getMinPrice().toDouble();
         closes[i] = tick.getClosePrice().toDouble();
         volumes[i] = tick.getVolume().toDouble();
      }

      OHLCDataset dataset = new DefaultHighLowDataset("btc", dates, highs, lows, opens, closes, volumes);

      return dataset;
   }

   public static void main(String[] args) {

      /**
       * Getting time series
       */
      TimeSeries series = CsvTicksLoader.loadAppleIncSeries();

      /**
       * Creating the OHLC dataset
       */
      OHLCDataset ohlcDataset = createOHLCDataset(series);

      /**
       * Creating indicators
       */
      // Close price
      ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
      // Bollinger bands
      BollingerBandsMiddleIndicator middleBBand = new BollingerBandsMiddleIndicator(closePrice);
      BollingerBandsLowerIndicator minZ1BBand = new BollingerBandsLowerIndicator(middleBBand, closePrice, Decimal.ONE); //Z1
      BollingerBandsUpperIndicator plusZ1BBand = new BollingerBandsUpperIndicator(middleBBand, closePrice, Decimal.ONE);
      BollingerBandsLowerIndicator minZ2BBand = new BollingerBandsLowerIndicator(middleBBand, closePrice); //Z2
      BollingerBandsUpperIndicator plusZ2BBand = new BollingerBandsUpperIndicator(middleBBand, closePrice);
      BollingerBandsLowerIndicator minZ3BBand = new BollingerBandsLowerIndicator(middleBBand, closePrice, Decimal.THREE); //Z2
      BollingerBandsUpperIndicator plusZ3BBand = new BollingerBandsUpperIndicator(middleBBand, closePrice, Decimal.THREE);

      SimpleLinearRegressionIndicator slRL10 = new SimpleLinearRegressionIndicator(closePrice, 10);
      SimpleLinearRegressionIndicator slRL30 = new SimpleLinearRegressionIndicator(closePrice, 30);

      SMAIndicator sma10 = new SMAIndicator(closePrice, 10);
      SMAIndicator sma30 = new SMAIndicator(closePrice, 30);
      SMAIndicator sma90 = new SMAIndicator(closePrice, 90);
      SMAIndicator sma270 = new SMAIndicator(closePrice, 270);

      /**
       * Building chart dataset
       */
      TimeSeriesCollection dataset = new TimeSeriesCollection();
      dataset.addSeries(buildChartTimeSeries(series, closePrice, "Apple Inc. (AAPL) - NASDAQ GS"));
      dataset.addSeries(buildChartTimeSeries(series, minZ1BBand, "-Z1"));
      dataset.addSeries(buildChartTimeSeries(series, plusZ1BBand, "+Z1"));
      dataset.addSeries(buildChartTimeSeries(series, minZ2BBand, "-Z2"));
      dataset.addSeries(buildChartTimeSeries(series, plusZ2BBand, "+Z2"));
      dataset.addSeries(buildChartTimeSeries(series, minZ3BBand, "-Z3"));
      dataset.addSeries(buildChartTimeSeries(series, plusZ3BBand, "+Z3"));

      dataset.addSeries(buildChartTimeSeries(series, slRL10, "RL10"));      
      dataset.addSeries(buildChartTimeSeries(series, slRL30, "RL30"));       

//      dataset.addSeries(buildChartTimeSeries(series, sma10, "SMA10"));
//      dataset.addSeries(buildChartTimeSeries(series, sma30, "SMA30"));
//      dataset.addSeries(buildChartTimeSeries(series, sma90, "SMA90"));
//      dataset.addSeries(buildChartTimeSeries(series, sma270, "SMA270"));

      /**
       * Creating the chart
       */
      JFreeChart chart = ChartFactory.createTimeSeriesChart(
         "Apple Inc. 2013 Close Prices", // title
         "Date", // x-axis label
         "Price Per Unit", // y-axis label
         dataset, // data
         true, // create legend?
         true, // generate tooltips?
         false // generate URLs?
      );
      XYPlot plot = (XYPlot) chart.getPlot();

      DateAxis axis = (DateAxis) plot.getDomainAxis();
      axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

      /**
       * Displaying the chart
       */
      displayChart(chart);
   }

}
