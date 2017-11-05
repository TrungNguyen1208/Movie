package ptit.nttrung.movie.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.GenreManager;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<GenreManager.Genre> genres = new ArrayList<>();

    GenreAdapter(List<GenreManager.Genre> list) {
        this.genres = list;
    }

    @Override public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false));
    }

    @Override public void onBindViewHolder(GenreViewHolder holder, int position) {
        GenreManager.Genre genre = genres.get(position);
        holder.genre.setText(genre.getName());
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        Button genre;

        GenreViewHolder(View itemView) {
            super(itemView);
            genre = (Button) itemView.findViewById(R.id.genre);
        }
    }
}
