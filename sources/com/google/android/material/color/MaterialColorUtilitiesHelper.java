package com.google.android.material.color;

import com.google.android.material.R;
import com.google.android.material.color.utilities.DynamicColor;
import com.google.android.material.color.utilities.DynamicScheme;
import com.google.android.material.color.utilities.MaterialDynamicColors;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class MaterialColorUtilitiesHelper {
    private static final Map<Integer, DynamicColor> colorResourceIdToColorValue;

    private MaterialColorUtilitiesHelper() {
    }

    static {
        Map<Integer, DynamicColor> map = new HashMap<>();
        map.put(Integer.valueOf(R.color.material_personalized_color_primary), MaterialDynamicColors.primary);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_primary), MaterialDynamicColors.onPrimary);
        map.put(Integer.valueOf(R.color.material_personalized_color_primary_inverse), MaterialDynamicColors.primaryInverse);
        map.put(Integer.valueOf(R.color.material_personalized_color_primary_container), MaterialDynamicColors.primaryContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_primary_container), MaterialDynamicColors.onPrimaryContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_secondary), MaterialDynamicColors.secondary);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_secondary), MaterialDynamicColors.onSecondary);
        map.put(Integer.valueOf(R.color.material_personalized_color_secondary_container), MaterialDynamicColors.secondaryContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_secondary_container), MaterialDynamicColors.onSecondaryContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_tertiary), MaterialDynamicColors.tertiary);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_tertiary), MaterialDynamicColors.onTertiary);
        map.put(Integer.valueOf(R.color.material_personalized_color_tertiary_container), MaterialDynamicColors.tertiaryContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_tertiary_container), MaterialDynamicColors.onTertiaryContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_background), MaterialDynamicColors.background);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_background), MaterialDynamicColors.onBackground);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface), MaterialDynamicColors.surface);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_surface), MaterialDynamicColors.onSurface);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_variant), MaterialDynamicColors.surfaceVariant);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_surface_variant), MaterialDynamicColors.onSurfaceVariant);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_inverse), MaterialDynamicColors.surfaceInverse);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_surface_inverse), MaterialDynamicColors.onSurfaceInverse);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_bright), MaterialDynamicColors.surfaceBright);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_dim), MaterialDynamicColors.surfaceDim);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_container), MaterialDynamicColors.surfaceContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_container_low), MaterialDynamicColors.surfaceSub1);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_container_high), MaterialDynamicColors.surfaceAdd1);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_container_lowest), MaterialDynamicColors.surfaceSub2);
        map.put(Integer.valueOf(R.color.material_personalized_color_surface_container_highest), MaterialDynamicColors.surfaceAdd2);
        map.put(Integer.valueOf(R.color.material_personalized_color_outline), MaterialDynamicColors.outline);
        map.put(Integer.valueOf(R.color.material_personalized_color_outline_variant), MaterialDynamicColors.outlineVariant);
        map.put(Integer.valueOf(R.color.material_personalized_color_error), MaterialDynamicColors.error);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_error), MaterialDynamicColors.onError);
        map.put(Integer.valueOf(R.color.material_personalized_color_error_container), MaterialDynamicColors.errorContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_on_error_container), MaterialDynamicColors.onErrorContainer);
        map.put(Integer.valueOf(R.color.material_personalized_color_control_activated), MaterialDynamicColors.controlActivated);
        map.put(Integer.valueOf(R.color.material_personalized_color_control_normal), MaterialDynamicColors.controlNormal);
        map.put(Integer.valueOf(R.color.material_personalized_color_control_highlight), MaterialDynamicColors.controlHighlight);
        map.put(Integer.valueOf(R.color.material_personalized_color_text_primary_inverse), MaterialDynamicColors.textPrimaryInverse);
        map.put(Integer.valueOf(R.color.material_personalized_color_text_secondary_and_tertiary_inverse), MaterialDynamicColors.textSecondaryAndTertiaryInverse);
        map.put(Integer.valueOf(R.color.material_personalized_color_text_secondary_and_tertiary_inverse_disabled), MaterialDynamicColors.textSecondaryAndTertiaryInverseDisabled);
        map.put(Integer.valueOf(R.color.material_personalized_color_text_primary_inverse_disable_only), MaterialDynamicColors.textPrimaryInverseDisableOnly);
        map.put(Integer.valueOf(R.color.material_personalized_color_text_hint_foreground_inverse), MaterialDynamicColors.textHintInverse);
        colorResourceIdToColorValue = Collections.unmodifiableMap(map);
    }

    public static Map<Integer, Integer> createColorResourcesIdsToColorValues(DynamicScheme colorScheme) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (Map.Entry<Integer, DynamicColor> entry : colorResourceIdToColorValue.entrySet()) {
            map.put(entry.getKey(), Integer.valueOf(entry.getValue().getArgb(colorScheme)));
        }
        return Collections.unmodifiableMap(map);
    }
}
