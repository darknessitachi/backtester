package be.stehey.foxy.backtester.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import be.stehey.foxy.backtester.MessageBus;

public class HistoricCsvDataHandler extends DataHandler {

	// HistoricCsvDatahandler is designed to read Csv files for
	// each requested symbol from disk and provide an interface
	// to obtain the "latest" bar in a manner identical to a live
	// trading interface
	
	private String csvFolder;
	
	private Map<String, TreeSet<OHLCBar>> symbolData = new HashMap<String, TreeSet<OHLCBar>>();
	private Map<String, List<OHLCBar>> latestSymbolData = new HashMap<String, List<OHLCBar>>();
	private Map<String, Iterator<OHLCBar>> symbolIterators = new HashMap<String, Iterator<OHLCBar>>();
	
	//private static int symbolDataIndex = 0;
	
	public HistoricCsvDataHandler() {
		super();
	}
	
	public HistoricCsvDataHandler(MessageBus messageBus, String csvFolder, List<String> symbolList) {
		this.messageBus = messageBus;
		this.csvFolder = csvFolder;
		this.symbolList = symbolList;
		for(String symbol : symbolList) {
			TreeSet<OHLCBar> symbolData = readCsvAndPopulateMapFor(symbol);
			this.symbolData.put(symbol, symbolData);
			this.symbolIterators.put(symbol, symbolData.iterator());
			this.latestSymbolData.put(symbol, new ArrayList<OHLCBar>());
		}
	}

	@Override
	public List<OHLCBar> getLatestBars(String symbol, Integer numberOfBars) {
		if(this.latestSymbolData.get(symbol) == null)
			return null;
		else {
			if(this.latestSymbolData.get(symbol).size() <= numberOfBars)
				return this.latestSymbolData.get(symbol);
			else
				return this.latestSymbolData.get(symbol).subList(this.latestSymbolData.get(symbol).size() - numberOfBars, this.latestSymbolData.get(symbol).size());
		}
	}

	@Override
	public void updateBars() {
		// pushes the latest bar to the latestSymbolData structure
		// for all symbols in the symbol list
		boolean endOfDataReached = false;
		for(String symbol : this.symbolList) {
			try {
				OHLCBar ohlcBar = (OHLCBar) this.symbolIterators.get(symbol).next();
				this.latestSymbolData.get(symbol).add(ohlcBar);
			} catch(NoSuchElementException e) {
				endOfDataReached = true;
			}
		}
		if(!endOfDataReached) {
			//HistoricCsvDataHandler.symbolDataIndex = HistoricCsvDataHandler.symbolDataIndex + 1;
			messageBus.publishEvent(new MarketEvent());
		} else {
			continueBackTest = false;
		}
	}

	private TreeSet<OHLCBar> readCsvAndPopulateMapFor(String symbol) {
		
		TreeSet<OHLCBar> ohlcBarList = new TreeSet<OHLCBar>();
		BufferedReader br = null;
		String line = "";
		String csvFile = this.csvFolder + "/" + symbol + ".csv";
		String cvsSplitBy = ",";
	 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] barData = line.split(cvsSplitBy);
				
				Date timestamp;
				try {
					if(barData[1].length() != 0) {
						timestamp = sdf.parse(barData[0]);
						if(new Float(barData[1]).intValue() != 0) {
							OHLCBar ohlcBar = new OHLCBar();
							ohlcBar.setSymbol(symbol);
							ohlcBar.setTimestamp(timestamp);
							ohlcBar.setOpenPrice(new BigDecimal(barData[1]));
							ohlcBar.setHighPrice(new BigDecimal(barData[2]));
							ohlcBar.setLowPrice(new BigDecimal(barData[3]));
							ohlcBar.setClosePrice(new BigDecimal(barData[4]));
							ohlcBarList.add(ohlcBar);
						}
					}
				} catch (ParseException e) {
					//e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ohlcBarList;
	}

	@Override
	public void resetBars() {
		this.latestSymbolData = new HashMap<String, List<OHLCBar>>();
		//HistoricCsvDataHandler.symbolDataIndex = 0;
	}

}
