package com.squire.vr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<Game> games;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onDownloadClick(Game game);
    }

    public GameAdapter(List<Game> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    public void updateList(List<Game> newGames) {
        this.games = newGames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.name.setText(game.gameName);
        holder.meta.setText("v" + game.versionCode + " | " + game.sizeMb);
        holder.downloadBtn.setOnClickListener(v -> listener.onDownloadClick(game));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, meta;
        Button downloadBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.game_title);
            meta = itemView.findViewById(R.id.game_meta);
            downloadBtn = itemView.findViewById(R.id.download_btn);
        }
    }
}
