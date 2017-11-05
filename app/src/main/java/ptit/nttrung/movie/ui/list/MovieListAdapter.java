package ptit.nttrung.movie.ui.list;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.util.UrlBuilder;

/**
 * Created by TrungNguyen on 11/4/2017.
 */

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_ITEM = 1;
    private List<Media> list;
    private OnMovieClickListener onMovieClickListener;

    public MovieListAdapter(List<Media> movies) {
        this.list = movies;
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.onMovieClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder viewHolder = (MovieViewHolder) holder;
            Media media = list.get(position);

//            RxView.clicks(viewHolder.itemView).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(aVoid -> {
//                onMovieClickListener.onMovieClicked(media, viewHolder.itemView);
//            });

            viewHolder.title.setText(media.getTitle());
            viewHolder.year.setText(media.getReleaseDate().split("-")[0]);
            Glide.clear(viewHolder.poster);
            holder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.poster.getContext(), R.color.colorPrimary));

            Log.e("media ", media.getPosterPath());
            Log.e("media ", UrlBuilder.getPosterUrl(media.getPosterPath()));

            Glide.with(viewHolder.poster.getContext())
                    .load(UrlBuilder.getPosterUrl(media.getPosterPath()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                            Palette palette = new Palette.Builder(bitmap).generate();
                            int defaultColor = 0xFF333333;
                            int color = palette.getDarkMutedColor(defaultColor);
                            list.get(position).setBackgroundColor(color);
                            holder.itemView.setBackgroundColor(color);
                            return false;
                        }
                    })
                    .into(viewHolder.poster);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        TextView year;

        MovieViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            year= (TextView) itemView.findViewById(R.id.year);
            poster = (ImageView) itemView.findViewById(R.id.poster);
        }
    }

    interface OnMovieClickListener {
        void onMovieClicked(Media media, View view);
    }
}
