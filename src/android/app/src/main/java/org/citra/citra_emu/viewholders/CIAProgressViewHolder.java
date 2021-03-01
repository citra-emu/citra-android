package org.citra.citra_emu.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.citra.citra_emu.R;

public class CIAProgressViewHolder extends RecyclerView.ViewHolder {
    private View itemView;
    public TextView textFileName;
    public TextView textInstallState;
    public ProgressBar progressInstallation;

    public CIAProgressViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        itemView.setTag(this);
        textFileName = itemView.findViewById(R.id.text_filename);
        textInstallState = itemView.findViewById(R.id.text_install_state);
        progressInstallation = itemView.findViewById(R.id.progress_installation);
    }

    public View getItemView() {
        return itemView;
    }
}
