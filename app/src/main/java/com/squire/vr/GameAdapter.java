package com.squire.vr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_GAME = 0;
    private static final int TYPE_DETAILS = 1;

    private List<Object> items; // Can be Game or DetailMarker
    private OnGameClickListener listener;
    private boolean showUpdateLabels = false;
    public static class DetailMarker {
        public Game game;
        public DetailMarker(Game g) { this.game = g; }
    }

    public interface OnGameClickListener {
        void onDownloadClick(Game game);
    }

    public GameAdapter(List<Game> games, OnGameClickListener listener) {
        this.items = new ArrayList<>(games);
        this.listener = listener;
    }

    public void updateList(List<Game> newGames, boolean showUpdateLabels) {
        this.showUpdateLabels = showUpdateLabels;
        this.items = new ArrayList<>(newGames);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof Game) ? TYPE_GAME : TYPE_DETAILS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GAME) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card, parent, false);
            return new GameViewHolder(view);
        } else {
            // New layout for details row - we can reuse or make a small simple one
            // For now, a simple TextView in a frame
            android.widget.FrameLayout frame = new android.widget.FrameLayout(parent.getContext());
            frame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            frame.setPadding(16, 8, 16, 16);
            
            TextView tv = new TextView(parent.getContext());
            tv.setId(android.R.id.text1);
            tv.setTextColor(0xFFE0E0E0);
            tv.setTextSize(14);
            tv.setBackgroundColor(0xFF252525);
            tv.setPadding(32, 32, 32, 32);
            // Add rounded corners via code is hard without drawable, so just flat rect for now
            frame.addView(tv);
            return new DetailViewHolder(frame);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GameViewHolder) {
            bindGame((GameViewHolder) holder, (Game) items.get(position), position);
        } else if (holder instanceof DetailViewHolder) {
            bindDetail((DetailViewHolder) holder, (DetailMarker) items.get(position));
        }
    }

    private void bindGame(GameViewHolder holder, Game game, int position) {
        try {
            holder.name.setText(game.gameName);
            holder.meta.setText("v" + game.version + " (" + game.sizeMb + ")");
            holder.date.setText("Updated: " + game.date);
            
            if (showUpdateLabels && game.needsUpdate) {
                holder.downloadBtn.setText("Update");
            } else {
                holder.downloadBtn.setText("Download");
            }
            holder.downloadBtn.setOnClickListener(v -> listener.onDownloadClick(game));
            
            // Image Loading
            holder.thumb.setOnClickListener(v -> toggleExpansion(position));
            if (game.thumbnailPath != null) {
                try {
                    Glide.with(holder.itemView.getContext())
                         .load(new File(game.thumbnailPath))
                         .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                         .placeholder(android.R.drawable.ic_menu_gallery)
                         .centerCrop()
                         .into(holder.thumb);
                } catch (Exception e) {
                    holder.thumb.setImageDrawable(null);
                }
            } else {
                holder.thumb.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Click on card to toggle expansion
            holder.itemView.setOnClickListener(v -> toggleExpansion(position));
        } catch (Exception e) {
            android.util.Log.e("TRD_ADAPTER", "Error binding view", e);
        }
    }

    private void bindDetail(DetailViewHolder holder, DetailMarker marker) {
        TextView tv = holder.itemView.findViewById(android.R.id.text1);
        if (marker.game.noteContent != null && !marker.game.noteContent.isEmpty()) {
            tv.setText("Release Notes:\n\n" + marker.game.noteContent);
        } else {
            tv.setText("No release notes available.");
        }
    }

    private void toggleExpansion(int position) {
        Object item = items.get(position);
        if (!(item instanceof Game)) return;
        
        Game game = (Game) item;
        boolean wasExpanded = game.isExpanded;
        
        // Close any other open ones (Optional but cleaner)
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof DetailMarker) {
                items.remove(i);
                notifyItemRemoved(i);
                // Also reset the game's flag
                for(Object o : items) if(o instanceof Game) ((Game)o).isExpanded = false;
                if (i <= position) position--;
                break;
            }
        }

        if (!wasExpanded) {
            game.isExpanded = true;
            items.add(position + 1, new DetailMarker(game));
            notifyItemInserted(position + 1);
        } else {
            game.isExpanded = false;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView name, meta, date;
        Button downloadBtn;
        ImageView thumb;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.game_title);
            meta = itemView.findViewById(R.id.game_meta);
            date = itemView.findViewById(R.id.game_date);
            downloadBtn = itemView.findViewById(R.id.download_btn);
            thumb = itemView.findViewById(R.id.game_thumb);
        }
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
