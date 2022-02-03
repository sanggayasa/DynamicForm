package io.kecoakburikk.dynamicform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder> {
    ArrayList<CheckboxRecycler> arrayListMovies;

    public MoviesRecyclerViewAdapter(ArrayList<CheckboxRecycler> arrayListMovies) {
        this.arrayListMovies = arrayListMovies;
    }

    @NonNull
    @Override
    public MoviesRecyclerViewAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesRecyclerViewAdapter.MovieViewHolder holder, int position) {
        final CheckboxRecycler movies = arrayListMovies.get(position);
        holder.textViewTitle.setText(movies.getTitle());


    }

    @Override
    public int getItemCount() {
        return arrayListMovies.size();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {
        CheckBox textViewTitle;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
