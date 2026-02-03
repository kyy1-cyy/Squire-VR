package com.google.android.material.color.utilities;

import java.util.function.Function;

/* loaded from: classes.dex */
public final class MaterialDynamicColors {
    private static final double CONTAINER_ACCENT_TONE_DELTA = 15.0d;
    public static final DynamicColor background = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda75
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda113
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 6.0d : 98.0d);
        }
    });
    public static final DynamicColor onBackground = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda125
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda137
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 90.0d : 10.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda149
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.background;
        }
    });
    public static final DynamicColor surface = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda9
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda21
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 6.0d : 98.0d);
        }
    });
    public static final DynamicColor surfaceInverse = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda34
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda46
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 90.0d : 20.0d);
        }
    });
    public static final DynamicColor surfaceBright = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda58
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda44
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 24.0d : 98.0d);
        }
    });
    public static final DynamicColor surfaceDim = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda69
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda81
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 6.0d : 87.0d);
        }
    });
    public static final DynamicColor surfaceSub2 = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda93
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda105
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 4.0d : 100.0d);
        }
    });
    public static final DynamicColor surfaceSub1 = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda107
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda109
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 10.0d : 96.0d);
        }
    });
    public static final DynamicColor surfaceContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda110
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda111
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 12.0d : 94.0d);
        }
    });
    public static final DynamicColor surfaceAdd1 = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda112
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda114
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 17.0d : 92.0d);
        }
    });
    public static final DynamicColor surfaceAdd2 = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda115
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda116
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 22.0d : 90.0d);
        }
    });
    public static final DynamicColor onSurface = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda117
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda118
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 90.0d : 10.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda120
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onSurfaceInverse = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda121
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda122
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 20.0d : 95.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda123
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.surfaceInverse;
        }
    });
    public static final DynamicColor surfaceVariant = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda124
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralVariantPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda126
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 30.0d : 90.0d);
        }
    });
    public static final DynamicColor onSurfaceVariant = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda127
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralVariantPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda128
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 80.0d : 30.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda129
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.surfaceVariant;
        }
    });
    public static final DynamicColor outline = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda131
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralVariantPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda132
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(50.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda133
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor outlineVariant = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda134
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralVariantPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda135
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(80.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda136
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor primaryContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda138
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda139
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$41((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda140
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onPrimaryContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda142
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda143
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$44((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda144
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.primaryContainer;
        }
    }, null);
    public static final DynamicColor primary = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda145
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda146
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 80.0d : 40.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda147
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda148
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$49((DynamicScheme) obj);
        }
    });
    public static final DynamicColor primaryInverse = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda150
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda151
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 40.0d : 80.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda1
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.surfaceInverse;
        }
    });
    public static final DynamicColor onPrimary = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda2
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda3
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 20.0d : 100.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda4
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.primary;
        }
    });
    public static final DynamicColor secondaryContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda5
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda6
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$57((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda7
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onSecondaryContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda8
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda10
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$60((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda12
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.secondaryContainer;
        }
    });
    public static final DynamicColor secondary = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda13
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda14
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 80.0d : 40.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda15
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda16
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$65((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onSecondary = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda17
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda18
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 20.0d : 100.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda19
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.secondary;
        }
    });
    public static final DynamicColor tertiaryContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda20
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda23
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$70((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda24
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onTertiaryContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda25
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda26
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$73((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda27
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.tertiaryContainer;
        }
    });
    public static final DynamicColor tertiary = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda28
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda29
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 80.0d : 40.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda30
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda31
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$78((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onTertiary = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda32
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda35
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 20.0d : 100.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda36
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.tertiary;
        }
    });
    public static final DynamicColor errorContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda37
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).errorPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda38
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 30.0d : 90.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda39
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onErrorContainer = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda40
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).errorPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda41
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 90.0d : 10.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda42
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.errorContainer;
        }
    });
    public static final DynamicColor error = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda43
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).errorPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda45
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 80.0d : 40.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda47
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda48
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.lambda$static$91((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onError = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda49
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).errorPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda50
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 20.0d : 100.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda51
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.error;
        }
    });
    public static final DynamicColor primaryFixed = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda52
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda53
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(90.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda54
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor primaryFixedDarker = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda56
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda57
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(80.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda86
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onPrimaryFixed = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda97
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda108
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(10.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda119
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.primaryFixedDarker;
        }
    });
    public static final DynamicColor onPrimaryFixedVariant = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda130
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda141
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(30.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda152
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.primaryFixedDarker;
        }
    });
    public static final DynamicColor secondaryFixed = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda11
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda22
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(90.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda33
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor secondaryFixedDarker = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda55
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda59
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(80.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda60
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onSecondaryFixed = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda61
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda62
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(10.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda63
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.secondaryFixedDarker;
        }
    });
    public static final DynamicColor onSecondaryFixedVariant = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda65
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).secondaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda66
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(30.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda67
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.secondaryFixedDarker;
        }
    });
    public static final DynamicColor tertiaryFixed = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda68
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda70
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(90.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda71
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor tertiaryFixedDarker = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda72
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda73
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(80.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda74
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.highestSurface((DynamicScheme) obj);
        }
    });
    public static final DynamicColor onTertiaryFixed = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda76
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda77
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(10.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda78
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.tertiaryFixedDarker;
        }
    });
    public static final DynamicColor onTertiaryFixedVariant = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda79
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).tertiaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda80
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(30.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda82
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MaterialDynamicColors.tertiaryFixedDarker;
        }
    });
    public static final DynamicColor controlActivated = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda83
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).primaryPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda84
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 30.0d : 90.0d);
        }
    }, null);
    public static final DynamicColor controlNormal = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda85
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralVariantPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda87
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 80.0d : 30.0d);
        }
    });
    public static final DynamicColor controlHighlight = new DynamicColor(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda88
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(0.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda89
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(0.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda90
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 100.0d : 0.0d);
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda91
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 0.2d : 0.12d);
        }
    }, null, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda92
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(DynamicColor.toneMinContrastDefault(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda64
                @Override // java.util.function.Function
                public final Object apply(Object obj2) {
                    return Double.valueOf(((DynamicScheme) obj2).isDark ? 100.0d : 0.0d);
                }
            }, null, (DynamicScheme) obj, null));
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda94
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(DynamicColor.toneMaxContrastDefault(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj2) {
                    return Double.valueOf(((DynamicScheme) obj2).isDark ? 100.0d : 0.0d);
                }
            }, null, (DynamicScheme) obj, null));
        }
    }, null);
    public static final DynamicColor textPrimaryInverse = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda95
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda96
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 10.0d : 90.0d);
        }
    });
    public static final DynamicColor textSecondaryAndTertiaryInverse = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda98
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralVariantPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda99
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 30.0d : 80.0d);
        }
    });
    public static final DynamicColor textPrimaryInverseDisableOnly = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda100
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda101
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 10.0d : 90.0d);
        }
    });
    public static final DynamicColor textSecondaryAndTertiaryInverseDisabled = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda102
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda103
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 10.0d : 90.0d);
        }
    });
    public static final DynamicColor textHintInverse = DynamicColor.fromPalette(new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda104
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((DynamicScheme) obj).neutralPalette;
        }
    }, new Function() { // from class: com.google.android.material.color.utilities.MaterialDynamicColors$$ExternalSyntheticLambda106
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Double.valueOf(((DynamicScheme) obj).isDark ? 10.0d : 90.0d);
        }
    });

    private MaterialDynamicColors() {
    }

    public static DynamicColor highestSurface(DynamicScheme s) {
        return s.isDark ? surfaceBright : surfaceDim;
    }

    static /* synthetic */ Double lambda$static$41(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(performAlbers(s.sourceColorHct, s));
        }
        return Double.valueOf(s.isDark ? 30.0d : 90.0d);
    }

    static /* synthetic */ Double lambda$static$44(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(DynamicColor.contrastingTone(primaryContainer.getTone(s), 4.5d));
        }
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    static /* synthetic */ ToneDeltaConstraint lambda$static$49(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, primaryContainer, s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    static /* synthetic */ Double lambda$static$57(DynamicScheme s) {
        double initialTone = s.isDark ? 30.0d : 90.0d;
        if (!isFidelity(s)) {
            return Double.valueOf(initialTone);
        }
        double answer = findDesiredChromaByTone(s.secondaryPalette.getHue(), s.secondaryPalette.getChroma(), initialTone, !s.isDark);
        return Double.valueOf(performAlbers(s.secondaryPalette.getHct(answer), s));
    }

    static /* synthetic */ Double lambda$static$60(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(DynamicColor.contrastingTone(secondaryContainer.getTone(s), 4.5d));
        }
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    static /* synthetic */ ToneDeltaConstraint lambda$static$65(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, secondaryContainer, s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    static /* synthetic */ Double lambda$static$70(DynamicScheme s) {
        if (!isFidelity(s)) {
            return Double.valueOf(s.isDark ? 30.0d : 90.0d);
        }
        double albersTone = performAlbers(s.tertiaryPalette.getHct(s.sourceColorHct.getTone()), s);
        Hct proposedHct = s.tertiaryPalette.getHct(albersTone);
        return Double.valueOf(DislikeAnalyzer.fixIfDisliked(proposedHct).getTone());
    }

    static /* synthetic */ Double lambda$static$73(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(DynamicColor.contrastingTone(tertiaryContainer.getTone(s), 4.5d));
        }
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    static /* synthetic */ ToneDeltaConstraint lambda$static$78(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, tertiaryContainer, s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    static /* synthetic */ ToneDeltaConstraint lambda$static$91(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, errorContainer, s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    private static ViewingConditions viewingConditionsForAlbers(DynamicScheme scheme) {
        return ViewingConditions.defaultWithBackgroundLstar(scheme.isDark ? 30.0d : 80.0d);
    }

    private static boolean isFidelity(DynamicScheme scheme) {
        return scheme.variant == Variant.FIDELITY || scheme.variant == Variant.CONTENT;
    }

    static double findDesiredChromaByTone(double hue, double chroma, double tone, boolean byDecreasingTone) {
        double answer = tone;
        Hct closestToChroma = Hct.from(hue, chroma, tone);
        if (closestToChroma.getChroma() < chroma) {
            Hct closestToChroma2 = closestToChroma;
            double chromaPeak = closestToChroma.getChroma();
            while (closestToChroma2.getChroma() < chroma) {
                double answer2 = answer + (byDecreasingTone ? -1.0d : 1.0d);
                Hct potentialSolution = Hct.from(hue, chroma, answer2);
                if (chromaPeak > potentialSolution.getChroma() || Math.abs(potentialSolution.getChroma() - chroma) < 0.4d) {
                    return answer2;
                }
                double potentialDelta = Math.abs(potentialSolution.getChroma() - chroma);
                double currentDelta = Math.abs(closestToChroma2.getChroma() - chroma);
                if (potentialDelta < currentDelta) {
                    closestToChroma2 = potentialSolution;
                }
                chromaPeak = Math.max(chromaPeak, potentialSolution.getChroma());
                answer = answer2;
            }
            return answer;
        }
        return answer;
    }

    static double performAlbers(Hct prealbers, DynamicScheme scheme) {
        Hct albersd = prealbers.inViewingConditions(viewingConditionsForAlbers(scheme));
        if (DynamicColor.tonePrefersLightForeground(prealbers.getTone()) && !DynamicColor.toneAllowsLightForeground(albersd.getTone())) {
            return DynamicColor.enableLightForeground(prealbers.getTone());
        }
        return DynamicColor.enableLightForeground(albersd.getTone());
    }
}
