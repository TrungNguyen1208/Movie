package ptit.nttrung.movie.ui.search;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Section;
import ptit.nttrung.movie.util.UrlBuilder;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SECTION = 1;
    private static final int TYPE_PERSON = 2;
    private static final int TYPE_MOVIE = 3;
    private List<Object> list;
    private OnMediaClickListener listener;

    SearchAdapter(List<Object> objects) {
        list = objects;
    }

    void setListener(OnMediaClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SECTION:
                return new SearchSectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_section, parent, false));
            case TYPE_MOVIE:
                return new SearchMovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_movie, parent, false));
            case TYPE_PERSON:
                return new SearchCastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_people, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) return;
        Object object = list.get(position);
        if (holder instanceof SearchSectionViewHolder) {
            SearchSectionViewHolder viewHolder = (SearchSectionViewHolder) holder;
            viewHolder.topDivider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            viewHolder.name.setText(((Section) object).name);

        } else if (holder instanceof SearchMovieViewHolder) {
            Media movie = (Media) object;
            SearchMovieViewHolder viewHolder = (SearchMovieViewHolder) holder;
            viewHolder.divider.setVisibility(position == list.size() - 1 ? View.VISIBLE : View.GONE);
            viewHolder.title.setText(movie.getTitle());
            Glide.with(viewHolder.itemView.getContext())
                    .load(UrlBuilder.getPosterUrl(movie.getPosterPath()))
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
                            movie.setBackgroundColor(color);
                            return false;
                        }
                    })
                    .into(viewHolder.poster);
            if (listener != null)
                RxView.clicks(viewHolder.itemView).subscribe(aVoid -> listener.onMovieClicked(viewHolder.itemView, movie));

        } else if (holder instanceof SearchCastViewHolder) {
            Media person = (Media) object;
            SearchCastViewHolder viewHolder = (SearchCastViewHolder) holder;
            viewHolder.divider.setVisibility(position == list.size() - 1 ? View.VISIBLE : View.GONE);
            viewHolder.name.setText(person.getName());
            Glide.with(holder.itemView.getContext()).load(UrlBuilder.getCastUrl(person.getProfilePath()))
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(viewHolder.image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(holder.itemView.getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            viewHolder.image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
            if (listener != null)
                RxView.clicks(viewHolder.itemView).subscribe(aVoid -> listener.onPersonClicked(viewHolder.itemView, person));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = list.get(position);
        if (object instanceof Section)
            return TYPE_SECTION;
        if (object instanceof Media) {
            return ((Media) object).getMediaType().equals("movie") ? TYPE_MOVIE : TYPE_PERSON;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SearchSectionViewHolder extends RecyclerView.ViewHolder {
        View topDivider;
        TextView name;

        SearchSectionViewHolder(View itemView) {
            super(itemView);
            topDivider = itemView.findViewById(R.id.top_divider);
            name = itemView.findViewById(R.id.name);
        }
    }

    static class SearchCastViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        View divider;

        SearchCastViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            divider = itemView.findViewById(R.id.bottom_divider);
        }
    }

    static class SearchMovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        View divider;

        SearchMovieViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            divider = itemView.findViewById(R.id.bottom_divider);
        }
    }

    interface OnMediaClickListener {
        void onMovieClicked(View view, Media movie);

        void onPersonClicked(View view, Media person);
    }
}
