package com.example.xyzreader.utils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public final class ArticleDateUtils {
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
  // Use default locale format
  public static final SimpleDateFormat outputFormat = new SimpleDateFormat();
  // Most time functions can only handle 1902 - 2037
  public static final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
}
