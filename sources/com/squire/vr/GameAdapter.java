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

    public static class DetailMarker {
        public Game game;

        public DetailMarker(Game g) {
            this.game = g;
        }
    }

    public GameAdapter(List<Game> games, OnGameClickListener listener) {
        this.items = new ArrayList(games);
        this.listener = listener;
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
        TextView tv = new TextView(parent.getContext());
        tv.setId(android.R.id.text1);
        tv.setTextColor(-2039584);
        tv.setTextSize(14.0f);
        tv.setBackgroundColor(-14342875);
        tv.setPadding(32, 32, 32, 32);
        frame.addView(tv);
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

    private void bindGame(GameViewHolder holder, final Game game, final int position) {
        StringBuilder sbAppend;
        try {
            holder.name.setText(game.gameName);
            TextView textView = holder.meta;
            StringBuilder sb = new StringBuilder();
            if (this.showInstalledLabels) {
                StringBuilder sbAppend2 = sb.append("Installed v");
                String str = game.installedVersion;
                if (str == null) {
                    str = game.version;
                }
                sbAppend = sbAppend2.append(str).append(" • Latest v");
                sbAppend.append(game.version);
            } else {
                sbAppend = sb.append("v");
                sbAppend.append(game.version);
            }
            textView.setText(sbAppend.append(" (").append(game.sizeMb).append(")").toString());
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
                button.setText("Update");
            } else if (this.showInstalledLabels) {
                button.setText("Downloaded");
                button.setEnabled(false);
                button.setTextColor(-7829368);
                button.setBackgroundTintList(ColorStateList.valueOf(-13421773));
            } else {
                button.setText("Download");
            }
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.GameAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$bindGame$0(game, view);
                }
            });
            holder.thumb.setOnClickListener(new View.OnClickListener() { // from class: com.squire.vr.GameAdapter$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$bindGame$1(position, view);
                }
            });
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
                    this.f$0.lambda$bindGame$2(position, view);
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

        public GameViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.game_title);
            this.meta = (TextView) itemView.findViewById(R.id.game_meta);
            this.date = (TextView) itemView.findViewById(R.id.game_date);
            this.downloadBtn = (Button) itemView.findViewById(R.id.download_btn);
            this.thumb = (ImageView) itemView.findViewById(R.id.game_thumb);
        }
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        public DetailViewHolder(View itemView) {
            super(itemView);
        }
    }
}
