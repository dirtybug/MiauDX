package com.Runner.CQMiau;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class WindowInsetsHelper {

    private WindowInsetsHelper() {
    }

    public static void apply(Activity activity) {
        final View content = activity.findViewById(android.R.id.content);
        if (!(content instanceof ViewGroup)) {
            return;
        }
        final ViewGroup contentGroup = (ViewGroup) content;
        if (contentGroup.getChildCount() == 0) {
            return;
        }
        final View root = contentGroup.getChildAt(0);
        final int origLeft = root.getPaddingLeft();
        final int origTop = root.getPaddingTop();
        final int origRight = root.getPaddingRight();
        final int origBottom = root.getPaddingBottom();

        final int[] systemBottomInset = new int[]{0};

        final Runnable updatePadding = () -> {
            View actionBarContainer = activity.findViewById(androidx.appcompat.R.id.action_bar_container);
            int overlapTop = 0;
            if (actionBarContainer != null && actionBarContainer.getVisibility() == View.VISIBLE) {
                overlapTop = Math.max(0, actionBarContainer.getBottom() - content.getTop());
            }

            int overlapBottom = 0;
            if (content.getParent() instanceof View) {
                View parent = (View) content.getParent();
                int bottomMarginApplied = Math.max(0, parent.getHeight() - content.getBottom());
                overlapBottom = Math.max(0, systemBottomInset[0] - bottomMarginApplied);
            }

            int newTop = origTop + overlapTop;
            int newBottom = origBottom + overlapBottom;
            if (root.getPaddingTop() != newTop || root.getPaddingBottom() != newBottom) {
                root.setPadding(origLeft, newTop, origRight, newBottom);
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
                            | WindowInsetsCompat.Type.ime()
            );
            systemBottomInset[0] = bars.bottom;
            v.post(updatePadding);
            return insets;
        });

        content.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                updatePadding.run());

        View actionBarContainer = activity.findViewById(androidx.appcompat.R.id.action_bar_container);
        if (actionBarContainer != null) {
            actionBarContainer.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                    updatePadding.run());
        }
    }
}
