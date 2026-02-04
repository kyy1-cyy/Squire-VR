package com.squire.vr;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes3.dex */
public class GameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DETAILS = 1;
    private static final int TYPE_GAME = 0;
    private List<Object> items;
    private OnGameClickListener listener;
    private boolean showInstalledLabels;
    private boolean showUpdateLabels = false;

    public interface OnGameClickListener {
        void onDownloadClick(Game game);
    }
    
    public interface OnGameFavoriteListener extends OnGameClickListener {
        void onFavoriteToggle(Game game);
    }
    
    public interface OnGameUninstallListener extends OnGameFavoriteListener {
        void onUninstallClick(Game game);
    }

    public static class DetailMarker {
        public Game game;

        public DetailMarker(Game g) {
            this.game = g;
        }
    }

    // Field to track the currently downloading game release name
    private String currentDownloadingRelease = null;

    // REVERTED: Remove logic that greys out button to allow re-queueing (per user request)
    public void setDownloadingGame(String releaseName) {
        this.currentDownloadingRelease = null; // FORCE NULL to disable grey-out logic
        notifyDataSetChanged();
    }

    public GameAdapter(List<Game> games, OnGameClickListener listener) {
        this.items = new ArrayList(games);
        this.listener = listener;
        setHasStableIds(true); // Enable stable IDs for better performance and less flickering
    }
    

    public void updateList(List list, boolean showInstalledLabels, boolean showUpdateLabels) {
        this.showInstalledLabels = showInstalledLabels;
        this.showUpdateLabels = showUpdateLabels;
        this.items = new ArrayList(list);
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return !(this.items.get(i) instanceof Game) ? 1 : 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card, parent, false);
            return new GameViewHolder(view);
        }
        FrameLayout frame = new FrameLayout(parent.getContext());
        frame.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        frame.setPadding(16, 8, 16, 16);
        TextView tvNotes = new TextView(parent.getContext());
        tvNotes.setId(android.R.id.text1);
        tvNotes.setTextColor(-2039584);
        tvNotes.setTextSize(14.0f);
        tvNotes.setBackgroundColor(-14342875);
        tvNotes.setPadding(32, 32, 32, 32);
        frame.addView(tvNotes);
        return new DetailViewHolder(frame);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GameViewHolder) {
            bindGame((GameViewHolder) holder, (Game) this.items.get(position), position);
        } else if (holder instanceof DetailViewHolder) {
            bindDetail((DetailViewHolder) holder, (DetailMarker) this.items.get(position));
        }
    }
    
    // Fix for flickering and glitching items
    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= items.size()) return RecyclerView.NO_ID;
        Object item = items.get(position);
        if (item instanceof Game) {
            Game g = (Game) item;
            return g.stableId;
        } else if (item instanceof DetailMarker) {
            DetailMarker m = (DetailMarker) item;
            if (m.game != null) {
                return m.game.stableId + 1000000; // Offset for detail view
            }
        }
        return position;
    }

    private void bindGame(GameViewHolder holder, final Game game, final int position) {
        StringBuilder sbAppend;
        try {
            // Apply hover effect to the whole item view
            holder.itemView.setBackgroundResource(R.drawable.card_selector);
            
            holder.name.setText(game.gameName);
            
            // Handle favorite star
            holder.favoriteStar.setImageResource(game.isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
            holder.favoriteStar.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    game.isFavorite = !game.isFavorite;
                    holder.favoriteStar.setImageResource(game.isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
                    if (GameAdapter.this.listener instanceof OnGameFavoriteListener) {
                        ((OnGameFavoriteListener) GameAdapter.this.listener).onFavoriteToggle(game);
                    }
                }
            });
            
            // Uninstall button logic
            if (this.showInstalledLabels && holder.uninstallBtn != null) {
                holder.uninstallBtn.setVisibility(View.VISIBLE);
                holder.uninstallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GameAdapter.this.listener instanceof OnGameUninstallListener) {
                            ((OnGameUninstallListener) GameAdapter.this.listener).onUninstallClick(game);
                        }
                    }
                });
            } else if (holder.uninstallBtn != null) {
                holder.uninstallBtn.setVisibility(View.GONE);
            }

            TextView textView = holder.meta;
            StringBuilder sb = new StringBuilder();
            if (this.showUpdateLabels) {
                // Updates tab: Show detailed version comparison
                String installed = game.installedVersion != null ? game.installedVersion : "Unknown";
                sb.append("v").append(installed).append("\nLatest: v").append(game.version);
            } else if (this.showInstalledLabels) {
                StringBuilder sbAppend2 = sb.append("Installed v");
                String str = game.installedVersion;
                if (str == null) {
                    str = game.version;
                }
                sbAppend = sbAppend2.append(str).append(" • Latest v");
                sbAppend.append(game.version);
            } else {
                sb.append("v").append(game.version);
            }
            // Append size
            if (!this.showUpdateLabels) {
                 sb.append(" (").append(game.sizeMb).append(")");
            } else {
                 sb.append(" (").append(game.sizeMb).append(")");
            }
            textView.setText(sb.toString());
            holder.meta.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.GameAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Context context = v.getContext();
                    Game game2 = game;
                    String str2 = game2.installedDate;
                    Toast.makeText(context, (!game2.needsUpdate || str2 == null) ? str2 != null ? str2 : game2.version : "Downloaded: " + str2 + "\nUpdated: " + game2.date, 1).show();
                }
            });
            TextView textView2 = holder.date;
            StringBuilder sbAppend3 = new StringBuilder().append(game.packageName).append("\n").append("Updated: ");
            sbAppend3.append(game.date);
            textView2.setText(sbAppend3.toString());
            Button button = holder.downloadBtn;
            button.setEnabled(true);
            button.setTextColor(-1);
            button.setBackgroundTintList(ColorStateList.valueOf(-16745729));
            if (this.showUpdateLabels && game.needsUpdate) {
                // If files exist, show "Resume" instead of "Update" to indicate partial download
                File gameDir = new File("/sdcard/Download/Squire Vr Games/" + game.releaseName);
                File tempDir = new File("/sdcard/Download/Squire Vr Games/" + game.releaseName + "/temp_archives");
                
                // If the game is fully installed, tempDir shouldn't exist ideally. 
                // But if it does, it might be a partial update.
                
                if (tempDir.exists() && tempDir.isDirectory() && tempDir.list() != null && tempDir.list().length > 0) {
                     button.setText("Resume");
                } else {
                     button.setText("Update");
                }
            } else if (this.showInstalledLabels) {
                button.setText("Downloaded");
                button.setEnabled(false);
                button.setTextColor(-7829368);
                button.setBackgroundTintList(ColorStateList.valueOf(-13421773));
            } else {
                // Check for partial downloads (temp_archives) to show Resume
                File gameDir = new File("/sdcard/Download/Squire Vr Games/" + game.releaseName);
                File tempDir = new File("/sdcard/Download/Squire Vr Games/" + game.releaseName + "/temp_archives");
                
                // USER REQUEST: Disable Resume entirely if the app was restarted (i.e. not currently downloading).
                // If the app crashed or closed, the files are likely corrupt/incomplete for resuming without complex state.
                // So, treat any partial download as invalid and force "Redownload" (which restarts).
                
                boolean hasPartial = tempDir.exists() && tempDir.isDirectory() && tempDir.list() != null && tempDir.list().length > 0;
                
                // Resume logic
                if (hasPartial) {
                    button.setText("Resume");
                } else if (gameDir.exists() && gameDir.isDirectory() && !hasPartial) {
                    // Already downloaded?
                    // Check if it's actually installed?
                    // For "All" tab, if folder exists but not installed, it's "Redownload" or "Install"
                    button.setText("Download"); 
                } else {
                    button.setText("Download");
                }
            }
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.GameAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GameAdapter.this.lambda$bindGame$0(game, view);
                }
            });
            
            // Removed thumb click listener so it propagates to itemView
            holder.thumb.setClickable(false);

            // Set popularity text
            // Always show popularity to help debug missing scores
            holder.popularity.setVisibility(View.VISIBLE);
            if (game.popularityScore > 0) {
                holder.popularity.setText(String.format(java.util.Locale.US, "Popularity: %.1f", game.popularityScore));
                holder.popularity.setTextColor(ColorStateList.valueOf(-1)); // White
            } else {
                holder.popularity.setText("Popularity: N/A");
                holder.popularity.setTextColor(ColorStateList.valueOf(-7829368)); // Gray
            }
            
            // Show installed indicator if game is installed
            if (holder.installedIndicator != null) {
                if (game.installedVersion != null) {
                    holder.installedIndicator.setVisibility(View.VISIBLE);
                } else {
                    holder.installedIndicator.setVisibility(View.GONE);
                }
            }
            
            if (game.thumbnailPath != null) {
                try {
                    Glide.with(holder.itemView.getContext()).load(new File(game.thumbnailPath)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(android.R.drawable.ic_menu_gallery).centerCrop().into(holder.thumb);
                } catch (Exception e) {
                    holder.thumb.setImageDrawable(null);
                }
            } else {
                holder.thumb.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.GameAdapter$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GameAdapter.this.lambda$bindGame$2(position, view);
                }
            });
        } catch (Exception e2) {
            Log.e("TRD_ADAPTER", "Error binding view", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindGame$0(Game game, View v) {
        this.listener.onDownloadClick(game);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindGame$1(int position, View v) {
        toggleExpansion(position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindGame$2(int position, View v) {
        toggleExpansion(position);
    }

    private void bindDetail(DetailViewHolder holder, DetailMarker marker) {
        TextView tv = (TextView) holder.itemView.findViewById(android.R.id.text1);
        if (marker.game.noteContent != null && !marker.game.noteContent.isEmpty()) {
            tv.setText("Release Notes:\n\n" + marker.game.noteContent);
        } else {
            tv.setText("No release notes available.");
        }
    }

    private void toggleExpansion(int position) {
        Object item = this.items.get(position);
        if (item instanceof Game) {
            Game game = (Game) item;
            boolean wasExpanded = game.isExpanded;
            int i = 0;
            while (true) {
                if (i >= this.items.size()) {
                    break;
                }
                if (!(this.items.get(i) instanceof DetailMarker)) {
                    i++;
                } else {
                    this.items.remove(i);
                    notifyItemRemoved(i);
                    for (Object o : this.items) {
                        if (o instanceof Game) {
                            ((Game) o).isExpanded = false;
                        }
                    }
                    if (i <= position) {
                        position--;
                    }
                }
            }
            if (!wasExpanded) {
                game.isExpanded = true;
                this.items.add(position + 1, new DetailMarker(game));
                notifyItemInserted(position + 1);
                return;
            }
            game.isExpanded = false;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.items.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        Button downloadBtn;
        TextView meta;
        TextView name;
        ImageView thumb;
        ImageView favoriteStar;
        ImageView uninstallBtn;
        TextView popularity;
        TextView installedIndicator;

        public GameViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.game_title);
            this.meta = (TextView) itemView.findViewById(R.id.game_meta);
            this.date = (TextView) itemView.findViewById(R.id.game_date);
            this.downloadBtn = (Button) itemView.findViewById(R.id.download_btn);
            this.thumb = (ImageView) itemView.findViewById(R.id.game_thumb);
            this.favoriteStar = (ImageView) itemView.findViewById(R.id.game_favorite_star);
            this.uninstallBtn = (ImageView) itemView.findViewById(R.id.btn_uninstall);
            this.popularity = (TextView) itemView.findViewById(R.id.game_popularity);
            this.installedIndicator = (TextView) itemView.findViewById(R.id.installed_indicator);
        }
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        public DetailViewHolder(View itemView) {
            super(itemView);
        }
    }
}
