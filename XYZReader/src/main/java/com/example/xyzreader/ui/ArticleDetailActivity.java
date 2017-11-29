package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final String LOG_TAG = ArticleDetailActivity.class.getSimpleName();

  Cursor mCursor;
  String title;
  String bodyContent;
  boolean isPlaying = false;
  MediaPlayer mediaPlayer;

  @BindView(R.id.detail_toolbar) CustomToolbar toolbar;
  @BindView(R.id.ctl_img) ImageView ctlImage;
  @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout ctl;
  @BindView(R.id.content_tv) TextView contentTextView;
  @BindView(R.id.toolbar_text_view) TextView toolbarTextView;
  @BindView(R.id.toolbar_author_text_view) TextView toolbarAuthorTextView;
  @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
  @BindView(R.id.header_ll) LinearLayout headerLl;
  @BindView(R.id.floatingActionButton) FloatingActionButton fab;

  @OnClick(R.id.floatingActionButton) public void fabClick() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      if (!isPlaying) {
        AnimatedVectorDrawable playToPause =
            (AnimatedVectorDrawable) getDrawable(R.drawable.avd_play_to_pause);
        fab.setImageDrawable(playToPause);
        assert playToPause != null;
        playToPause.start();
        isPlaying ^= true;
        mediaPlayer.start();
      } else {
        AnimatedVectorDrawable pauseToPlay =
            (AnimatedVectorDrawable) getDrawable(R.drawable.avd_pause_to_play_animation);
        fab.setImageDrawable(pauseToPlay);
        assert pauseToPlay != null;
        pauseToPlay.start();
        isPlaying ^= true;
        mediaPlayer.pause();
      }
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_detail);
    ButterKnife.bind(this);
    getLoaderManager().initLoader(1, null, this);
    setSupportActionBar(toolbar);
    //noinspection ConstantConditions
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mediaPlayer = MediaPlayer.create(this, R.raw.scarlet_plague);
    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      boolean isShow = true;
      int scrollRange = -1;

      @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
          ctl.setTitle(title);
          toolbar.setItemColor(getResources().getColor(R.color.white));
          isShow = true;
          ViewCompat.setElevation(appBarLayout,
              4 * getResources().getDisplayMetrics().density + 0.5f);
          headerLl.setVisibility(View.INVISIBLE);
        } else if (isShow) {
          ctl.setTitle(" ");
          isShow = false;
          headerLl.setVisibility(View.VISIBLE);

          toolbar.setItemColor(getResources().getColor(R.color.white));
          ViewCompat.setElevation(appBarLayout,
              4 * getResources().getDisplayMetrics().density + 0.5f);
        } else {

          ViewCompat.setElevation(appBarLayout,
              4 * getResources().getDisplayMetrics().density + 0.5f);
        }
      }
    });
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newInstanceForItemId(this,
        getIntent().getIntExtra(Constants.ARTICLE_POSITION, 0));
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    if (cursor.moveToFirst()) {
      mCursor = cursor;
      toolbarTextView.setText(cursor.getString(ArticleLoader.Query.TITLE));
      toolbarAuthorTextView.setText(cursor.getString(ArticleLoader.Query.AUTHOR));
      title = cursor.getString(ArticleLoader.Query.TITLE);
      Picasso.with(this).load(cursor.getString(ArticleLoader.Query.PHOTO_URL)).into(ctlImage);
      bodyContent = mCursor.getString(ArticleLoader.Query.BODY).replace("\r\n\r\n", "\n\n");
      bodyContent = bodyContent.replace("\r\n", " ");
      if (bodyContent.length() >= 4000) {
        contentTextView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)
            .substring(0, 4000)));
        ;
      } else {
        contentTextView.setText(Html.fromHtml(
            mCursor.getString(ArticleLoader.Query.BODY)));
      }
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

  }

  @Override protected void onPause() {
    super.onPause();
    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
    }
  }
}
