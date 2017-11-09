package com.example.xyzreader.utils;

import android.database.Cursor;
import android.util.Log;
import com.example.xyzreader.data.ArticleLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public final class ArticleDateUtils {
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
  // Use default locale format
  public static final SimpleDateFormat outputFormat = new SimpleDateFormat();
  // Most time functions can only handle 1902 - 2037
  public static final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
  public static final Date parsePublishedDate(Cursor mCursor, String LOG_TAG) {
    try {
      String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
      return ArticleDateUtils.dateFormat.parse(date);
    } catch (ParseException ex) {
      Log.e(LOG_TAG, ex.getMessage());
      Log.i(LOG_TAG, "passing today's date");
      return new Date();
    }
  }
}
