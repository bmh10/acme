package com.acmetelecom;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A simple file logger implemented on top of the Java logger (java.util.logging).
 */
public class FileLogger {
	
	private static final String logPrefix = "logs/log-";
	private static final String logFileExtension = ".log";
	
	private static Logger logger;
	
	/*
	 * Creates a new logger which appends to a log file or returns existing logger if already created.
	 */
	public static Logger create() {
		if (logger != null) {
			return logger;
		}

		try {
			// Create new file if does not already exist.
			String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
			File file = new File(logPrefix + date + logFileExtension);
			file.createNewFile();
			
			logger = Logger.getLogger("logger");
			FileHandler fh = new FileHandler(file.getPath(), true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Logger initialised.");
		
		return logger;
	}
	
	/*
	 * Turns the logger on or off.
	 */
	public static void setActive(boolean active) {
		if (logger != null) {
			if (active) logger.setLevel(Level.ALL);
			else logger.setLevel(Level.OFF);
		}
	}
}
