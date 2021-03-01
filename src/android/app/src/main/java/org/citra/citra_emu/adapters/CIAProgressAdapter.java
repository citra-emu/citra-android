package org.citra.citra_emu.adapters;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import org.citra.citra_emu.NativeLibrary;
import org.citra.citra_emu.R;
import org.citra.citra_emu.ui.DividerItemDecoration;
import org.citra.citra_emu.viewholders.CIAProgressViewHolder;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class CIAProgressAdapter extends RecyclerView.Adapter<CIAProgressViewHolder> implements NativeLibrary.CIAInstallationCallback {
    // Ordinal of enum used as index into ciaStateText
    private enum CIAState {
        INSTALLING,
        SUCCESS,
        FAILURE
    }

    private final @StringRes
    int[] ciaStateText = new int[] {
            R.string.cia_install_state_installing,
            R.string.cia_install_state_success,
            R.string.cia_install_state_failed
    };

    public interface CompletionCallback {
        void notifyInstallationComplete();
    }

    private final String[] ciaFileNames;
    private final long[] ciaProgress;
    private final long[] ciaSize;
    private final CIAState[] ciaState;

    private final AtomicInteger numComplete = new AtomicInteger(0);;
    private final Handler updateHandler = new Handler();
    private CompletionCallback completionCallback;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param ciaFileNames String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CIAProgressAdapter(String[] ciaFileNames) {
        this.ciaFileNames = ciaFileNames;
        this.ciaProgress = new long[ciaFileNames.length];
        this.ciaSize = new long[ciaFileNames.length];
        // Set the default size to 1 so the progress bar isn't filled by default
        Arrays.fill(this.ciaSize, 1);
        this.ciaState = new CIAState[ciaFileNames.length];
        Arrays.fill(this.ciaState, CIAState.INSTALLING);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CIAProgressViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View ciaProgress = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cia_progress, viewGroup, false);

        return new CIAProgressViewHolder(ciaProgress);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CIAProgressViewHolder holder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.textFileName.setText(ciaFileNames[position]);
        holder.textInstallState.setText(ciaStateText[ciaState[position].ordinal()]);
        holder.progressInstallation.setProgress((int)(1000L*ciaProgress[position]/ciaSize[position]));
        holder.progressInstallation.setMax(1000);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ciaFileNames.length;
    }

    public void setCompletionCallback(CompletionCallback completionCallback) {
        this.completionCallback = completionCallback;
    }

    public void postItemChange(int which) {
        updateHandler.post(() -> notifyItemChanged(which));
    }

    public void updateCIAStatus(int which, long newProgress, long newSize) {
        ciaSize[which] = newSize;
        ciaProgress[which] = newProgress;
        postItemChange(which);
    }

    public void updateCIACompletion(int which, boolean success) {
        ciaState[which] = success ? CIAState.SUCCESS : CIAState.FAILURE;
        if(numComplete.incrementAndGet() >= ciaFileNames.length) {
            if(completionCallback != null)
                completionCallback.notifyInstallationComplete();
        }
        postItemChange(which);
    }

    public static class SpacesItemDecoration extends DividerItemDecoration {
        private int space;

        public SpacesItemDecoration(Drawable divider, int space) {
            super(divider);
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = space;
            outRect.top = 0;
        }
    }
}
