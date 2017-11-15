package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final String LOG_TAG = ArticleListActivity.class.toString();
  private boolean mIsRefreshing = false;
  private GridItemDecoration mDividerItemDecoration;
  @BindView(R.id.main_toolbar) Toolbar mToolbar;
  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
  @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_list);
    ButterKnife.bind(this);
    setSupportActionBar(mToolbar);
    //noinspection ConstantConditions
    getSupportActionBar().setLogo(R.drawable.logo);
    getSupportActionBar().setTitle("");

    getLoaderManager().initLoader(0, null, this);
    mDividerItemDecoration = new GridItemDecoration(2, 2, false);
    if (savedInstanceState == null) {
      refresh();
    }
  }

  private void refresh() {
    startService(new Intent(this, UpdaterService.class));
  }

  @Override protected void onStart() {
    super.onStart();
    registerReceiver(mRefreshingReceiver,
        new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
  }

  @Override protected void onStop() {
    super.onStop();
    unregisterReceiver(mRefreshingReceiver);
  }

  private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
        mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
        updateRefreshingUI();
      }
    }
  };

  private void updateRefreshingUI() {
    mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newAllArticlesInstance(this);
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    ArticleListAdapter adapter = new ArticleListAdapter(cursor, this);
    adapter.setHasStableIds(true);
    mRecyclerView.setAdapter(adapter);
    int columnCount = getResources().getInteger(R.integer.list_column_count);
    GridLayoutManager sglm = new GridLayoutManager(this, columnCount);
    mRecyclerView.setLayoutManager(sglm);
    mRecyclerView.addItemDecoration(mDividerItemDecoration);
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    mRecyclerView.setAdapter(null);
  }
}
