package com.example.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.utils.ArticleDateUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.text.ParseException;
import java.util.Date;

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
        context.startActivity(new Intent(Intent.ACTION_VIEW,
            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
      }
    });
    return vh;
  }

  private Date parsePublishedDate() {
    try {
      String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
      return ArticleDateUtils.dateFormat.parse(date);
    } catch (ParseException ex) {
      Log.e(LOG_TAG, ex.getMessage());
      Log.i(LOG_TAG, "passing today's date");
      return new Date();
    }
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    mCursor.moveToPosition(position);
    holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
    Date publishedDate = parsePublishedDate();
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
    Picasso.with(context).load(mCursor.getString(ArticleLoader.Query.THUMB_URL)).into(new Target() {
      @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        assert holder.thumbnailView != null;
        holder.thumbnailView.setImageBitmap(bitmap);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
          @Override public void onGenerated(Palette palette) {
            Palette.Swatch swatch =
                new Palette.Swatch(context.getResources().getColor(R.color.white), 2);
            if (palette.getLightVibrantSwatch() != null) {
              swatch = palette.getLightVibrantSwatch();
            } else if (palette.getVibrantSwatch() != null) {
              swatch = palette.getVibrantSwatch();
            } else if (palette.getDarkVibrantSwatch() != null) {
              swatch = palette.getDarkVibrantSwatch();
            } else if (palette.getMutedSwatch() != null) {
              swatch = palette.getMutedSwatch();
            }
            holder.linearLayout.setBackgroundColor(swatch.getRgb());
          }
        });
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {

      }

      @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

      }
    });
    holder.thumbnailView.setImageUrl(mCursor.getString(ArticleLoader.Query.THUMB_URL),
        ImageLoaderHelper.getInstance(context).getImageLoader());

    holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
  }

  @Override public int getItemCount() {
    return mCursor.getCount();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    DynamicHeightNetworkImageView thumbnailView;
    TextView titleView;
    TextView subtitleView;
    ConstraintLayout linearLayout;

    ViewHolder(View view) {
      super(view);
      thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
      titleView = (TextView) view.findViewById(R.id.article_title);
      subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
      linearLayout = (ConstraintLayout) view.findViewById(R.id.grid_item_layout);
    }
  }

}


