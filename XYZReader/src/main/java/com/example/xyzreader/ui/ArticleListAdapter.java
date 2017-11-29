package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utils.ArticleDateUtils;
import com.example.xyzreader.utils.Constants;
import com.squareup.picasso.Picasso;
import java.util.Date;

import static com.example.xyzreader.utils.ArticleDateUtils.parsePublishedDate;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
  private static final String LOG_TAG = ArticleListAdapter.class.getSimpleName();
  private Cursor mCursor;
  private Context context;

  public ArticleListAdapter(Cursor cursor, Context context) {
    this.mCursor = cursor;
    this.context = context;
  }

  @Override public long getItemId(int position) {
    mCursor.moveToPosition(position);
    return mCursor.getLong(ArticleLoader.Query._ID);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.list_item_article, parent, false);
    final ViewHolder vh = new ViewHolder(view);
    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        mCursor.moveToPosition(vh.getAdapterPosition());
        Bundle bundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          bundle = ActivityOptions.makeSceneTransitionAnimation((ArticleListActivity) context,
              vh.thumbnailView, context.getResources().getString(R.string.shared_img)).toBundle();
        }
        intent.putExtra(Constants.ARTICLE_POSITION, mCursor.getInt(ArticleLoader.Query._ID));
        context.startActivity(intent, bundle);
      }
    });
    return vh;
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    mCursor.moveToPosition(position);
    holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      holder.thumbnailView.setTransitionName(context.getResources().getString(R.string.shared_img));
    }
    Date publishedDate = parsePublishedDate(mCursor, LOG_TAG);
    if (!publishedDate.before(ArticleDateUtils.START_OF_EPOCH.getTime())) {

      holder.subtitleView.setText(Html.fromHtml(
          DateUtils.getRelativeTimeSpanString(publishedDate.getTime(), System.currentTimeMillis(),
              DateUtils.HOUR_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString() + "<br/>"

              + mCursor.getString(ArticleLoader.Query.AUTHOR)));
    } else {
      holder.subtitleView.setText(
          Html.fromHtml(ArticleDateUtils.outputFormat.format(publishedDate) + "<br/><br/>"

              + mCursor.getString(ArticleLoader.Query.AUTHOR)));
    }

    Picasso.with(context)
        .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
        .placeholder(R.mipmap.ic_launcher)
        .into(holder.thumbnailView);
  }

  @Override public int getItemCount() {
    return mCursor.getCount();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    DynamicHeightNetworkImageView thumbnailView;
    TextView titleView;
    TextView subtitleView;
    ConstraintLayout linearLayout;
    int color;

    ViewHolder(View view) {
      super(view);
      thumbnailView = view.findViewById(R.id.thumbnail);
      titleView = view.findViewById(R.id.article_title);
      subtitleView = view.findViewById(R.id.article_subtitle);
      linearLayout = view.findViewById(R.id.grid_item_layout);
    }
  }
}


