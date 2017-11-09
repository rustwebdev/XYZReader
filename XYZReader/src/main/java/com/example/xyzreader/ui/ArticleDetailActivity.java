package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utils.Constants;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private Cursor mCursor;
  private long mStartId;

  private long mSelectedItemId;
  private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
  private int mTopInset;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_article_detail);

    getLoaderManager().initLoader(0, null, this);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newInstanceForItemId(this,
        getIntent().getIntExtra(Constants.ARTICLE_POSITION, 0));
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mCursor = cursor;
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mCursor = null;
  }
}
