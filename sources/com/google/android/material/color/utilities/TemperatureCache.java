package com.google.android.material.color.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/* loaded from: classes.dex */
public final class TemperatureCache {
    private final Hct input;
    private Hct precomputedComplement;
    private List<Hct> precomputedHctsByHue;
    private List<Hct> precomputedHctsByTemp;
    private Map<Hct, Double> precomputedTempsByHct;

    private TemperatureCache() {
        throw new UnsupportedOperationException();
    }

    public TemperatureCache(Hct input) {
        this.input = input;
    }

    public Hct getComplement() {
        double startHue;
        double directionOfRotation;
        if (this.precomputedComplement != null) {
            return this.precomputedComplement;
        }
        double coldestHue = getColdest().getHue();
        double coldestTemp = getTempsByHct().get(getColdest()).doubleValue();
        double warmestHue = getWarmest().getHue();
        double warmestTemp = getTempsByHct().get(getWarmest()).doubleValue();
        double range = warmestTemp - coldestTemp;
        boolean startHueIsColdestToWarmest = isBetween(this.input.getHue(), coldestHue, warmestHue);
        double startHue2 = startHueIsColdestToWarmest ? warmestHue : coldestHue;
        double endHue = startHueIsColdestToWarmest ? coldestHue : warmestHue;
        double directionOfRotation2 = 1.0d;
        Hct answer = getHctsByHue().get((int) Math.round(this.input.getHue()));
        double complementRelativeTemp = 1.0d - getRelativeTemperature(this.input);
        double smallestError = 1000.0d;
        double hueAddend = 0.0d;
        while (hueAddend <= 360.0d) {
            double hue = MathUtils.sanitizeDegreesDouble(startHue2 + (directionOfRotation2 * hueAddend));
            if (!isBetween(hue, startHue2, endHue)) {
                startHue = startHue2;
                directionOfRotation = directionOfRotation2;
            } else {
                startHue = startHue2;
                directionOfRotation = directionOfRotation2;
                Hct possibleAnswer = getHctsByHue().get((int) Math.round(hue));
                double relativeTemp = (getTempsByHct().get(possibleAnswer).doubleValue() - coldestTemp) / range;
                double error = Math.abs(complementRelativeTemp - relativeTemp);
                if (error < smallestError) {
                    smallestError = error;
                    answer = possibleAnswer;
                }
            }
            hueAddend += 1.0d;
            startHue2 = startHue;
            directionOfRotation2 = directionOfRotation;
        }
        this.precomputedComplement = answer;
        return this.precomputedComplement;
    }

    public List<Hct> getAnalogousColors() {
        return getAnalogousColors(5, 12);
    }

    public List<Hct> getAnalogousColors(int count, int divisions) {
        int startHue = (int) Math.round(this.input.getHue());
        Hct startHct = getHctsByHue().get(startHue);
        double lastTemp = getRelativeTemperature(startHct);
        List<Hct> allColors = new ArrayList<>();
        allColors.add(startHct);
        double absoluteTotalTempDelta = 0.0d;
        for (int i = 0; i < 360; i++) {
            int hue = MathUtils.sanitizeDegreesInt(startHue + i);
            double temp = getRelativeTemperature(getHctsByHue().get(hue));
            double tempDelta = Math.abs(temp - lastTemp);
            lastTemp = temp;
            absoluteTotalTempDelta += tempDelta;
        }
        int hueAddend = 1;
        double tempStep = absoluteTotalTempDelta / divisions;
        double totalTempDelta = 0.0d;
        double desiredTotalTempDeltaForIndex = getRelativeTemperature(startHct);
        while (true) {
            Hct startHct2 = startHct;
            if (allColors.size() >= divisions) {
                break;
            }
            int hue2 = MathUtils.sanitizeDegreesInt(startHue + hueAddend);
            Hct hct = getHctsByHue().get(hue2);
            double temp2 = getRelativeTemperature(hct);
            double tempDelta2 = Math.abs(temp2 - desiredTotalTempDeltaForIndex);
            totalTempDelta += tempDelta2;
            int startHue2 = startHue;
            double lastTemp2 = allColors.size();
            double desiredTotalTempDeltaForIndex2 = lastTemp2 * tempStep;
            boolean indexSatisfied = totalTempDelta >= desiredTotalTempDeltaForIndex2;
            int indexAddend = 1;
            while (indexSatisfied && allColors.size() < divisions) {
                allColors.add(hct);
                double desiredTotalTempDeltaForIndex3 = allColors.size() + indexAddend;
                desiredTotalTempDeltaForIndex2 = desiredTotalTempDeltaForIndex3 * tempStep;
                indexSatisfied = totalTempDelta >= desiredTotalTempDeltaForIndex2;
                indexAddend++;
            }
            desiredTotalTempDeltaForIndex = temp2;
            hueAddend++;
            if (hueAddend > 360) {
                while (allColors.size() < divisions) {
                    allColors.add(hct);
                }
            } else {
                startHct = startHct2;
                startHue = startHue2;
            }
        }
        List<Hct> answers = new ArrayList<>();
        answers.add(this.input);
        int ccwCount = (int) Math.floor((count - 1.0d) / 2.0d);
        for (int i2 = 1; i2 < ccwCount + 1; i2++) {
            int index = 0 - i2;
            while (index < 0) {
                index += allColors.size();
            }
            if (index >= allColors.size()) {
                index %= allColors.size();
            }
            answers.add(0, allColors.get(index));
        }
        int cwCount = (count - ccwCount) - 1;
        for (int i3 = 1; i3 < cwCount + 1; i3++) {
            int index2 = i3;
            while (index2 < 0) {
                index2 += allColors.size();
            }
            if (index2 >= allColors.size()) {
                index2 %= allColors.size();
            }
            answers.add(allColors.get(index2));
        }
        return answers;
    }

    public double getRelativeTemperature(Hct hct) {
        double range = getTempsByHct().get(getWarmest()).doubleValue() - getTempsByHct().get(getColdest()).doubleValue();
        double differenceFromColdest = getTempsByHct().get(hct).doubleValue() - getTempsByHct().get(getColdest()).doubleValue();
        if (range == 0.0d) {
            return 0.5d;
        }
        return differenceFromColdest / range;
    }

    public static double rawTemperature(Hct color) {
        double[] lab = ColorUtils.labFromArgb(color.toInt());
        double hue = MathUtils.sanitizeDegreesDouble(Math.toDegrees(Math.atan2(lab[2], lab[1])));
        double chroma = Math.hypot(lab[1], lab[2]);
        return ((Math.pow(chroma, 1.07d) * 0.02d) * Math.cos(Math.toRadians(MathUtils.sanitizeDegreesDouble(hue - 50.0d)))) - 0.5d;
    }

    private Hct getColdest() {
        return getHctsByTemp().get(0);
    }

    private List<Hct> getHctsByHue() {
        if (this.precomputedHctsByHue != null) {
            return this.precomputedHctsByHue;
        }
        List<Hct> hcts = new ArrayList<>();
        for (double hue = 0.0d; hue <= 360.0d; hue += 1.0d) {
            Hct colorAtHue = Hct.from(hue, this.input.getChroma(), this.input.getTone());
            hcts.add(colorAtHue);
        }
        this.precomputedHctsByHue = Collections.unmodifiableList(hcts);
        return this.precomputedHctsByHue;
    }

    private List<Hct> getHctsByTemp() {
        if (this.precomputedHctsByTemp != null) {
            return this.precomputedHctsByTemp;
        }
        List<Hct> hcts = new ArrayList<>(getHctsByHue());
        hcts.add(this.input);
        Comparator<Hct> temperaturesComparator = Comparator.comparing(new Function() { // from class: com.google.android.material.color.utilities.TemperatureCache$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.m55xb7a75f35((Hct) obj);
            }
        }, new Comparator() { // from class: com.google.android.material.color.utilities.TemperatureCache$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ((Double) obj).compareTo((Double) obj2);
            }
        });
        Collections.sort(hcts, temperaturesComparator);
        this.precomputedHctsByTemp = hcts;
        return this.precomputedHctsByTemp;
    }

    /* renamed from: lambda$getHctsByTemp$0$com-google-android-material-color-utilities-TemperatureCache, reason: not valid java name */
    /* synthetic */ Double m55xb7a75f35(Hct arg) {
        return getTempsByHct().get(arg);
    }

    private Map<Hct, Double> getTempsByHct() {
        if (this.precomputedTempsByHct != null) {
            return this.precomputedTempsByHct;
        }
        List<Hct> allHcts = new ArrayList<>(getHctsByHue());
        allHcts.add(this.input);
        Map<Hct, Double> temperaturesByHct = new HashMap<>();
        for (Hct hct : allHcts) {
            temperaturesByHct.put(hct, Double.valueOf(rawTemperature(hct)));
        }
        this.precomputedTempsByHct = temperaturesByHct;
        return this.precomputedTempsByHct;
    }

    private Hct getWarmest() {
        return getHctsByTemp().get(getHctsByTemp().size() - 1);
    }

    private static boolean isBetween(double angle, double a, double b) {
        return a < b ? a <= angle && angle <= b : a <= angle || angle <= b;
    }
}
