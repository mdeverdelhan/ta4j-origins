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
package ta4jexamples.loaders;

import au.com.bytecode.opencsv.CSVReader;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuandlTicksLoader {

  public static class QuandlDataFeed {
    private final String quandlCode;
    private final DateTime startDate;
    private final DateTime endDate;

    private static final String ISO_8601_DATE_FMT = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(ISO_8601_DATE_FMT);

    // changes these to a Builder if more params
    public QuandlDataFeed(String quandlCode) {
      this(quandlCode, DateTime.now().minusYears(1), DateTime.now());
    }

    public QuandlDataFeed(String quandlCode, DateTime startdate) {
      this(quandlCode, startdate, DateTime.now());
    }

    public QuandlDataFeed(String quandlCode, DateTime startDate, DateTime endDate) {
      this.quandlCode = quandlCode;
      this.startDate = startDate;
      this.endDate = endDate;
    }

    public TimeSeries load() {
      return get(buildURL());
    }

    private URL buildURL() {
      String urlString =  String.format("https://www.quandl.com/api/v3/datasets/%s.csv?start_date=%s&end_date=%s&order=asc",
              quandlCode, DATE_FORMATTER.print(startDate), DATE_FORMATTER.print(endDate));
      System.out.println(String.format("Quandl Dataset URL for %s", urlString));

      try {
        return new URL(urlString);
      } catch (MalformedURLException e) {
        throw new RuntimeException("Invalid URL");
      }
    }

    private TimeSeries toTimeSeries(InputStream is) {
      List<Tick> ticks = new ArrayList<Tick>();

      CSVReader csvReader = new CSVReader(new InputStreamReader(is, Charset.forName("UTF-8")), ',', '"', 1);
      try {
        String[] line;
        while ((line = csvReader.readNext()) != null) {
          DateTime date = new DateTime(DATE_FORMATTER.parseDateTime(line[0]));
          double open = Double.parseDouble(line[1]);
          double high = Double.parseDouble(line[2]);
          double low = Double.parseDouble(line[3]);
          double close = Double.parseDouble(line[4]);
          double volume = Double.parseDouble(line[5]);

          ticks.add(new Tick(date, open, high, low, close, volume));
        }
      } catch (IOException ioe) {
        Logger.getLogger(CsvTicksLoader.class.getName()).log(Level.SEVERE, "Unable to load ticks from CSV", ioe);
      } catch (NumberFormatException nfe) {
        Logger.getLogger(CsvTicksLoader.class.getName()).log(Level.SEVERE, "Error while parsing value", nfe);
      }

      return new TimeSeries(quandlCode + "_ticks", ticks);
    }

    private TimeSeries get(URL url) {
      HttpURLConnection connection = null;
      try {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(true);
        return toTimeSeries(new BufferedInputStream(connection.getInputStream()));
      } catch (FileNotFoundException fnfe) {
        throw new RuntimeException(String.format("%s is not a valid Quandl URL", url));
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } finally {
        if(connection != null) {
          connection.disconnect();
        }
      }
    }
  }

  public static void main(String args[]) {
    QuandlDataFeed quandl = new QuandlDataFeed("YAHOO/TWTR", DateTime.now().minusMonths(6));
    TimeSeries series = quandl.load();

    System.out.println("Series: " + series.getName() + " (" + series.getSeriesPeriodDescription() + ")");
    System.out.println("Number of ticks: " + series.getTickCount());
    System.out.println("Last tick: \n"
            + "\tVolume: " + series.getLastTick().getVolume() + "\n"
            + "\tOpen price: " + series.getLastTick().getOpenPrice()+ "\n"
            + "\tClose price: " + series.getLastTick().getClosePrice());
  }
}
