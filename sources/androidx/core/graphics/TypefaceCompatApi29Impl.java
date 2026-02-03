package androidx.core.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.FontsContractCompat;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class TypefaceCompatApi29Impl extends TypefaceCompatBaseImpl {
    private static int getMatchScore(FontStyle o1, FontStyle o2) {
        return (Math.abs(o1.getWeight() - o2.getWeight()) / 100) + (o1.getSlant() == o2.getSlant() ? 0 : 2);
    }

    private Font findBaseFont(FontFamily family, int style) {
        FontStyle desiredStyle = new FontStyle((style & 1) != 0 ? TypedValues.TransitionType.TYPE_DURATION : 400, (style & 2) != 0 ? 1 : 0);
        Font bestFont = family.getFont(0);
        int bestScore = getMatchScore(desiredStyle, bestFont.getStyle());
        for (int i = 1; i < family.getSize(); i++) {
            Font candidate = family.getFont(i);
            int score = getMatchScore(desiredStyle, candidate.getStyle());
            if (score < bestScore) {
                bestFont = candidate;
                bestScore = score;
            }
        }
        return bestFont;
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    protected FontsContractCompat.FontInfo findBestInfo(FontsContractCompat.FontInfo[] fonts, int style) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    protected Typeface createFromInputStream(Context context, InputStream is) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromFontInfo(Context context, CancellationSignal cancellationSignal, FontsContractCompat.FontInfo[] fonts, int style) throws IOException {
        FontFamily.Builder familyBuilder = null;
        ContentResolver resolver = context.getContentResolver();
        try {
            for (FontsContractCompat.FontInfo font : fonts) {
                try {
                    ParcelFileDescriptor pfd = resolver.openFileDescriptor(font.getUri(), "r", cancellationSignal);
                    if (pfd != null) {
                        try {
                            Font platformFont = new Font.Builder(pfd).setWeight(font.getWeight()).setSlant(font.isItalic() ? 1 : 0).setTtcIndex(font.getTtcIndex()).build();
                            if (familyBuilder == null) {
                                familyBuilder = new FontFamily.Builder(platformFont);
                            } else {
                                familyBuilder.addFont(platformFont);
                            }
                            if (pfd != null) {
                                pfd.close();
                            }
                        } catch (Throwable th) {
                            if (pfd != null) {
                                try {
                                    pfd.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } else if (pfd != null) {
                        pfd.close();
                    }
                } catch (IOException e) {
                }
            }
            if (familyBuilder == null) {
                return null;
            }
            FontFamily family = familyBuilder.build();
            return new Typeface.CustomFallbackBuilder(family).setStyle(findBaseFont(family, style).getStyle()).build();
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontResourcesParserCompat.FontFamilyFilesResourceEntry familyEntry, Resources resources, int style) throws IOException {
        FontFamily.Builder familyBuilder = null;
        try {
            for (FontResourcesParserCompat.FontFileResourceEntry entry : familyEntry.getEntries()) {
                try {
                    Font platformFont = new Font.Builder(resources, entry.getResourceId()).setWeight(entry.getWeight()).setSlant(entry.isItalic() ? 1 : 0).setTtcIndex(entry.getTtcIndex()).setFontVariationSettings(entry.getVariationSettings()).build();
                    if (familyBuilder == null) {
                        familyBuilder = new FontFamily.Builder(platformFont);
                    } else {
                        familyBuilder.addFont(platformFont);
                    }
                } catch (IOException e) {
                }
            }
            if (familyBuilder == null) {
                return null;
            }
            FontFamily family = familyBuilder.build();
            return new Typeface.CustomFallbackBuilder(family).setStyle(findBaseFont(family, style).getStyle()).build();
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromResourcesFontFile(Context context, Resources resources, int id, String path, int style) throws IOException {
        try {
            Font font = new Font.Builder(resources, id).build();
            FontFamily family = new FontFamily.Builder(font).build();
            return new Typeface.CustomFallbackBuilder(family).setStyle(font.getStyle()).build();
        } catch (Exception e) {
            return null;
        }
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    Typeface createWeightStyle(Context context, Typeface base, int weight, boolean italic) {
        return Typeface.create(base, weight, italic);
    }
}
