package com.google.android.material.color.utilities;

import com.google.errorprone.annotations.CheckReturnValue;

@CheckReturnValue
/* loaded from: classes.dex */
public class Scheme {
    private int background;
    private int error;
    private int errorContainer;
    private int inverseOnSurface;
    private int inversePrimary;
    private int inverseSurface;
    private int onBackground;
    private int onError;
    private int onErrorContainer;
    private int onPrimary;
    private int onPrimaryContainer;
    private int onSecondary;
    private int onSecondaryContainer;
    private int onSurface;
    private int onSurfaceVariant;
    private int onTertiary;
    private int onTertiaryContainer;
    private int outline;
    private int outlineVariant;
    private int primary;
    private int primaryContainer;
    private int scrim;
    private int secondary;
    private int secondaryContainer;
    private int shadow;
    private int surface;
    private int surfaceVariant;
    private int tertiary;
    private int tertiaryContainer;

    public Scheme() {
    }

    public Scheme(int primary, int onPrimary, int primaryContainer, int onPrimaryContainer, int secondary, int onSecondary, int secondaryContainer, int onSecondaryContainer, int tertiary, int onTertiary, int tertiaryContainer, int onTertiaryContainer, int error, int onError, int errorContainer, int onErrorContainer, int background, int onBackground, int surface, int onSurface, int surfaceVariant, int onSurfaceVariant, int outline, int outlineVariant, int shadow, int scrim, int inverseSurface, int inverseOnSurface, int inversePrimary) {
        this.primary = primary;
        this.onPrimary = onPrimary;
        this.primaryContainer = primaryContainer;
        this.onPrimaryContainer = onPrimaryContainer;
        this.secondary = secondary;
        this.onSecondary = onSecondary;
        this.secondaryContainer = secondaryContainer;
        this.onSecondaryContainer = onSecondaryContainer;
        this.tertiary = tertiary;
        this.onTertiary = onTertiary;
        this.tertiaryContainer = tertiaryContainer;
        this.onTertiaryContainer = onTertiaryContainer;
        this.error = error;
        this.onError = onError;
        this.errorContainer = errorContainer;
        this.onErrorContainer = onErrorContainer;
        this.background = background;
        this.onBackground = onBackground;
        this.surface = surface;
        this.onSurface = onSurface;
        this.surfaceVariant = surfaceVariant;
        this.onSurfaceVariant = onSurfaceVariant;
        this.outline = outline;
        this.outlineVariant = outlineVariant;
        this.shadow = shadow;
        this.scrim = scrim;
        this.inverseSurface = inverseSurface;
        this.inverseOnSurface = inverseOnSurface;
        this.inversePrimary = inversePrimary;
    }

    public static Scheme light(int argb) {
        return lightFromCorePalette(CorePalette.of(argb));
    }

    public static Scheme dark(int argb) {
        return darkFromCorePalette(CorePalette.of(argb));
    }

    public static Scheme lightContent(int argb) {
        return lightFromCorePalette(CorePalette.contentOf(argb));
    }

    public static Scheme darkContent(int argb) {
        return darkFromCorePalette(CorePalette.contentOf(argb));
    }

    private static Scheme lightFromCorePalette(CorePalette core) {
        return new Scheme().withPrimary(core.a1.tone(40)).withOnPrimary(core.a1.tone(100)).withPrimaryContainer(core.a1.tone(90)).withOnPrimaryContainer(core.a1.tone(10)).withSecondary(core.a2.tone(40)).withOnSecondary(core.a2.tone(100)).withSecondaryContainer(core.a2.tone(90)).withOnSecondaryContainer(core.a2.tone(10)).withTertiary(core.a3.tone(40)).withOnTertiary(core.a3.tone(100)).withTertiaryContainer(core.a3.tone(90)).withOnTertiaryContainer(core.a3.tone(10)).withError(core.error.tone(40)).withOnError(core.error.tone(100)).withErrorContainer(core.error.tone(90)).withOnErrorContainer(core.error.tone(10)).withBackground(core.n1.tone(99)).withOnBackground(core.n1.tone(10)).withSurface(core.n1.tone(99)).withOnSurface(core.n1.tone(10)).withSurfaceVariant(core.n2.tone(90)).withOnSurfaceVariant(core.n2.tone(30)).withOutline(core.n2.tone(50)).withOutlineVariant(core.n2.tone(80)).withShadow(core.n1.tone(0)).withScrim(core.n1.tone(0)).withInverseSurface(core.n1.tone(20)).withInverseOnSurface(core.n1.tone(95)).withInversePrimary(core.a1.tone(80));
    }

    private static Scheme darkFromCorePalette(CorePalette core) {
        return new Scheme().withPrimary(core.a1.tone(80)).withOnPrimary(core.a1.tone(20)).withPrimaryContainer(core.a1.tone(30)).withOnPrimaryContainer(core.a1.tone(90)).withSecondary(core.a2.tone(80)).withOnSecondary(core.a2.tone(20)).withSecondaryContainer(core.a2.tone(30)).withOnSecondaryContainer(core.a2.tone(90)).withTertiary(core.a3.tone(80)).withOnTertiary(core.a3.tone(20)).withTertiaryContainer(core.a3.tone(30)).withOnTertiaryContainer(core.a3.tone(90)).withError(core.error.tone(80)).withOnError(core.error.tone(20)).withErrorContainer(core.error.tone(30)).withOnErrorContainer(core.error.tone(80)).withBackground(core.n1.tone(10)).withOnBackground(core.n1.tone(90)).withSurface(core.n1.tone(10)).withOnSurface(core.n1.tone(90)).withSurfaceVariant(core.n2.tone(30)).withOnSurfaceVariant(core.n2.tone(80)).withOutline(core.n2.tone(60)).withOutlineVariant(core.n2.tone(30)).withShadow(core.n1.tone(0)).withScrim(core.n1.tone(0)).withInverseSurface(core.n1.tone(90)).withInverseOnSurface(core.n1.tone(20)).withInversePrimary(core.a1.tone(40));
    }

    public int getPrimary() {
        return this.primary;
    }

    public void setPrimary(int primary) {
        this.primary = primary;
    }

    public Scheme withPrimary(int primary) {
        this.primary = primary;
        return this;
    }

    public int getOnPrimary() {
        return this.onPrimary;
    }

    public void setOnPrimary(int onPrimary) {
        this.onPrimary = onPrimary;
    }

    public Scheme withOnPrimary(int onPrimary) {
        this.onPrimary = onPrimary;
        return this;
    }

    public int getPrimaryContainer() {
        return this.primaryContainer;
    }

    public void setPrimaryContainer(int primaryContainer) {
        this.primaryContainer = primaryContainer;
    }

    public Scheme withPrimaryContainer(int primaryContainer) {
        this.primaryContainer = primaryContainer;
        return this;
    }

    public int getOnPrimaryContainer() {
        return this.onPrimaryContainer;
    }

    public void setOnPrimaryContainer(int onPrimaryContainer) {
        this.onPrimaryContainer = onPrimaryContainer;
    }

    public Scheme withOnPrimaryContainer(int onPrimaryContainer) {
        this.onPrimaryContainer = onPrimaryContainer;
        return this;
    }

    public int getSecondary() {
        return this.secondary;
    }

    public void setSecondary(int secondary) {
        this.secondary = secondary;
    }

    public Scheme withSecondary(int secondary) {
        this.secondary = secondary;
        return this;
    }

    public int getOnSecondary() {
        return this.onSecondary;
    }

    public void setOnSecondary(int onSecondary) {
        this.onSecondary = onSecondary;
    }

    public Scheme withOnSecondary(int onSecondary) {
        this.onSecondary = onSecondary;
        return this;
    }

    public int getSecondaryContainer() {
        return this.secondaryContainer;
    }

    public void setSecondaryContainer(int secondaryContainer) {
        this.secondaryContainer = secondaryContainer;
    }

    public Scheme withSecondaryContainer(int secondaryContainer) {
        this.secondaryContainer = secondaryContainer;
        return this;
    }

    public int getOnSecondaryContainer() {
        return this.onSecondaryContainer;
    }

    public void setOnSecondaryContainer(int onSecondaryContainer) {
        this.onSecondaryContainer = onSecondaryContainer;
    }

    public Scheme withOnSecondaryContainer(int onSecondaryContainer) {
        this.onSecondaryContainer = onSecondaryContainer;
        return this;
    }

    public int getTertiary() {
        return this.tertiary;
    }

    public void setTertiary(int tertiary) {
        this.tertiary = tertiary;
    }

    public Scheme withTertiary(int tertiary) {
        this.tertiary = tertiary;
        return this;
    }

    public int getOnTertiary() {
        return this.onTertiary;
    }

    public void setOnTertiary(int onTertiary) {
        this.onTertiary = onTertiary;
    }

    public Scheme withOnTertiary(int onTertiary) {
        this.onTertiary = onTertiary;
        return this;
    }

    public int getTertiaryContainer() {
        return this.tertiaryContainer;
    }

    public void setTertiaryContainer(int tertiaryContainer) {
        this.tertiaryContainer = tertiaryContainer;
    }

    public Scheme withTertiaryContainer(int tertiaryContainer) {
        this.tertiaryContainer = tertiaryContainer;
        return this;
    }

    public int getOnTertiaryContainer() {
        return this.onTertiaryContainer;
    }

    public void setOnTertiaryContainer(int onTertiaryContainer) {
        this.onTertiaryContainer = onTertiaryContainer;
    }

    public Scheme withOnTertiaryContainer(int onTertiaryContainer) {
        this.onTertiaryContainer = onTertiaryContainer;
        return this;
    }

    public int getError() {
        return this.error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Scheme withError(int error) {
        this.error = error;
        return this;
    }

    public int getOnError() {
        return this.onError;
    }

    public void setOnError(int onError) {
        this.onError = onError;
    }

    public Scheme withOnError(int onError) {
        this.onError = onError;
        return this;
    }

    public int getErrorContainer() {
        return this.errorContainer;
    }

    public void setErrorContainer(int errorContainer) {
        this.errorContainer = errorContainer;
    }

    public Scheme withErrorContainer(int errorContainer) {
        this.errorContainer = errorContainer;
        return this;
    }

    public int getOnErrorContainer() {
        return this.onErrorContainer;
    }

    public void setOnErrorContainer(int onErrorContainer) {
        this.onErrorContainer = onErrorContainer;
    }

    public Scheme withOnErrorContainer(int onErrorContainer) {
        this.onErrorContainer = onErrorContainer;
        return this;
    }

    public int getBackground() {
        return this.background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public Scheme withBackground(int background) {
        this.background = background;
        return this;
    }

    public int getOnBackground() {
        return this.onBackground;
    }

    public void setOnBackground(int onBackground) {
        this.onBackground = onBackground;
    }

    public Scheme withOnBackground(int onBackground) {
        this.onBackground = onBackground;
        return this;
    }

    public int getSurface() {
        return this.surface;
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public Scheme withSurface(int surface) {
        this.surface = surface;
        return this;
    }

    public int getOnSurface() {
        return this.onSurface;
    }

    public void setOnSurface(int onSurface) {
        this.onSurface = onSurface;
    }

    public Scheme withOnSurface(int onSurface) {
        this.onSurface = onSurface;
        return this;
    }

    public int getSurfaceVariant() {
        return this.surfaceVariant;
    }

    public void setSurfaceVariant(int surfaceVariant) {
        this.surfaceVariant = surfaceVariant;
    }

    public Scheme withSurfaceVariant(int surfaceVariant) {
        this.surfaceVariant = surfaceVariant;
        return this;
    }

    public int getOnSurfaceVariant() {
        return this.onSurfaceVariant;
    }

    public void setOnSurfaceVariant(int onSurfaceVariant) {
        this.onSurfaceVariant = onSurfaceVariant;
    }

    public Scheme withOnSurfaceVariant(int onSurfaceVariant) {
        this.onSurfaceVariant = onSurfaceVariant;
        return this;
    }

    public int getOutline() {
        return this.outline;
    }

    public void setOutline(int outline) {
        this.outline = outline;
    }

    public Scheme withOutline(int outline) {
        this.outline = outline;
        return this;
    }

    public int getOutlineVariant() {
        return this.outlineVariant;
    }

    public void setOutlineVariant(int outlineVariant) {
        this.outlineVariant = outlineVariant;
    }

    public Scheme withOutlineVariant(int outlineVariant) {
        this.outlineVariant = outlineVariant;
        return this;
    }

    public int getShadow() {
        return this.shadow;
    }

    public void setShadow(int shadow) {
        this.shadow = shadow;
    }

    public Scheme withShadow(int shadow) {
        this.shadow = shadow;
        return this;
    }

    public int getScrim() {
        return this.scrim;
    }

    public void setScrim(int scrim) {
        this.scrim = scrim;
    }

    public Scheme withScrim(int scrim) {
        this.scrim = scrim;
        return this;
    }

    public int getInverseSurface() {
        return this.inverseSurface;
    }

    public void setInverseSurface(int inverseSurface) {
        this.inverseSurface = inverseSurface;
    }

    public Scheme withInverseSurface(int inverseSurface) {
        this.inverseSurface = inverseSurface;
        return this;
    }

    public int getInverseOnSurface() {
        return this.inverseOnSurface;
    }

    public void setInverseOnSurface(int inverseOnSurface) {
        this.inverseOnSurface = inverseOnSurface;
    }

    public Scheme withInverseOnSurface(int inverseOnSurface) {
        this.inverseOnSurface = inverseOnSurface;
        return this;
    }

    public int getInversePrimary() {
        return this.inversePrimary;
    }

    public void setInversePrimary(int inversePrimary) {
        this.inversePrimary = inversePrimary;
    }

    public Scheme withInversePrimary(int inversePrimary) {
        this.inversePrimary = inversePrimary;
        return this;
    }

    public String toString() {
        return "Scheme{primary=" + this.primary + ", onPrimary=" + this.onPrimary + ", primaryContainer=" + this.primaryContainer + ", onPrimaryContainer=" + this.onPrimaryContainer + ", secondary=" + this.secondary + ", onSecondary=" + this.onSecondary + ", secondaryContainer=" + this.secondaryContainer + ", onSecondaryContainer=" + this.onSecondaryContainer + ", tertiary=" + this.tertiary + ", onTertiary=" + this.onTertiary + ", tertiaryContainer=" + this.tertiaryContainer + ", onTertiaryContainer=" + this.onTertiaryContainer + ", error=" + this.error + ", onError=" + this.onError + ", errorContainer=" + this.errorContainer + ", onErrorContainer=" + this.onErrorContainer + ", background=" + this.background + ", onBackground=" + this.onBackground + ", surface=" + this.surface + ", onSurface=" + this.onSurface + ", surfaceVariant=" + this.surfaceVariant + ", onSurfaceVariant=" + this.onSurfaceVariant + ", outline=" + this.outline + ", outlineVariant=" + this.outlineVariant + ", shadow=" + this.shadow + ", scrim=" + this.scrim + ", inverseSurface=" + this.inverseSurface + ", inverseOnSurface=" + this.inverseOnSurface + ", inversePrimary=" + this.inversePrimary + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Scheme) || !super.equals(object)) {
            return false;
        }
        Scheme scheme = (Scheme) object;
        return this.primary == scheme.primary && this.onPrimary == scheme.onPrimary && this.primaryContainer == scheme.primaryContainer && this.onPrimaryContainer == scheme.onPrimaryContainer && this.secondary == scheme.secondary && this.onSecondary == scheme.onSecondary && this.secondaryContainer == scheme.secondaryContainer && this.onSecondaryContainer == scheme.onSecondaryContainer && this.tertiary == scheme.tertiary && this.onTertiary == scheme.onTertiary && this.tertiaryContainer == scheme.tertiaryContainer && this.onTertiaryContainer == scheme.onTertiaryContainer && this.error == scheme.error && this.onError == scheme.onError && this.errorContainer == scheme.errorContainer && this.onErrorContainer == scheme.onErrorContainer && this.background == scheme.background && this.onBackground == scheme.onBackground && this.surface == scheme.surface && this.onSurface == scheme.onSurface && this.surfaceVariant == scheme.surfaceVariant && this.onSurfaceVariant == scheme.onSurfaceVariant && this.outline == scheme.outline && this.outlineVariant == scheme.outlineVariant && this.shadow == scheme.shadow && this.scrim == scheme.scrim && this.inverseSurface == scheme.inverseSurface && this.inverseOnSurface == scheme.inverseOnSurface && this.inversePrimary == scheme.inversePrimary;
    }

    public int hashCode() {
        int result = super.hashCode();
        return (((((((((((((((((((((((((((((((((((((((((((((((((((((((((result * 31) + this.primary) * 31) + this.onPrimary) * 31) + this.primaryContainer) * 31) + this.onPrimaryContainer) * 31) + this.secondary) * 31) + this.onSecondary) * 31) + this.secondaryContainer) * 31) + this.onSecondaryContainer) * 31) + this.tertiary) * 31) + this.onTertiary) * 31) + this.tertiaryContainer) * 31) + this.onTertiaryContainer) * 31) + this.error) * 31) + this.onError) * 31) + this.errorContainer) * 31) + this.onErrorContainer) * 31) + this.background) * 31) + this.onBackground) * 31) + this.surface) * 31) + this.onSurface) * 31) + this.surfaceVariant) * 31) + this.onSurfaceVariant) * 31) + this.outline) * 31) + this.outlineVariant) * 31) + this.shadow) * 31) + this.scrim) * 31) + this.inverseSurface) * 31) + this.inverseOnSurface) * 31) + this.inversePrimary;
    }
}
