package ptit.nttrung.movie.ui.detail;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.DetailResponse;
import ptit.nttrung.movie.util.UrlBuilder;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    public static final int TYPE_SUMMARY = 1;
    public static final int TYPE_FULL = 2;

    private List<DetailResponse.Cast> casts;
    private int type;

    public CastAdapter(List<DetailResponse.Cast> list, int typeDef) {
        casts = list;
        type = typeDef;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(type == TYPE_SUMMARY ? R.layout.item_cast : R.layout.item_cast_table, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        DetailResponse.Cast cast = casts.get(position);

        Glide.with(holder.itemView.getContext()).load(UrlBuilder.getCastUrl(cast.getProfilePath()))
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(holder.image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(holder.itemView.getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.image.setImageDrawable(circularBitmapDrawable);
                    }
                });
        if (type == TYPE_FULL) {
            holder.name.setText(cast.getName());
            holder.character.setText(TextUtils.isEmpty(cast.getCharacter()) ? "Unknown character" : cast.getCharacter());
        }
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView character;

        CastViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.cast_image);
            name = (TextView) itemView.findViewById(R.id.name);
            character = (TextView) itemView.findViewById(R.id.character);
            setIsRecyclable(false);
        }
    }
}
