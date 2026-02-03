package com.google.android.material.color.utilities;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/* loaded from: classes.dex */
public final class QuantizerWsmeans {
    private static final int MAX_ITERATIONS = 10;
    private static final double MIN_MOVEMENT_DISTANCE = 3.0d;

    private QuantizerWsmeans() {
    }

    private static final class Distance implements Comparable<Distance> {
        int index = -1;
        double distance = -1.0d;

        Distance() {
        }

        @Override // java.lang.Comparable
        public int compareTo(Distance other) {
            return Double.valueOf(this.distance).compareTo(Double.valueOf(other.distance));
        }
    }

    public static Map<Integer, Integer> quantize(int[] inputPixels, int[] startingClusters, int maxColors) {
        int[] pixelCountSums;
        PointProvider pointProvider;
        PointProvider pointProvider2;
        int pointCount;
        int pointCount2;
        double[] componentASums;
        int[] pixelCountSums2;
        Random random = new Random(272008L);
        Map<Integer, Integer> pixelToCount = new LinkedHashMap<>();
        double[][] points = new double[inputPixels.length][];
        int[] pixels = new int[inputPixels.length];
        PointProvider pointProvider3 = new PointProviderLab();
        int pointCount3 = 0;
        for (int inputPixel : inputPixels) {
            Integer pixelCount = pixelToCount.get(Integer.valueOf(inputPixel));
            if (pixelCount == null) {
                points[pointCount3] = pointProvider3.fromInt(inputPixel);
                pixels[pointCount3] = inputPixel;
                pointCount3++;
                pixelToCount.put(Integer.valueOf(inputPixel), 1);
            } else {
                pixelToCount.put(Integer.valueOf(inputPixel), Integer.valueOf(pixelCount.intValue() + 1));
            }
        }
        int[] counts = new int[pointCount3];
        for (int i = 0; i < pointCount3; i++) {
            int pixel = pixels[i];
            counts[i] = pixelToCount.get(Integer.valueOf(pixel)).intValue();
        }
        int clusterCount = Math.min(maxColors, pointCount3);
        if (startingClusters.length != 0) {
            clusterCount = Math.min(clusterCount, startingClusters.length);
        }
        double[][] clusters = new double[clusterCount][];
        int clustersCreated = 0;
        for (int i2 = 0; i2 < startingClusters.length; i2++) {
            clusters[i2] = pointProvider3.fromInt(startingClusters[i2]);
            clustersCreated++;
        }
        int additionalClustersNeeded = clusterCount - clustersCreated;
        if (additionalClustersNeeded > 0) {
            for (int i3 = 0; i3 < additionalClustersNeeded; i3++) {
            }
        }
        int[] clusterIndices = new int[pointCount3];
        for (int i4 = 0; i4 < pointCount3; i4++) {
            clusterIndices[i4] = random.nextInt(clusterCount);
        }
        int[][] indexMatrix = new int[clusterCount][];
        for (int i5 = 0; i5 < clusterCount; i5++) {
            indexMatrix[i5] = new int[clusterCount];
        }
        Distance[][] distanceToIndexMatrix = new Distance[clusterCount][];
        int i6 = 0;
        while (i6 < clusterCount) {
            Random random2 = random;
            distanceToIndexMatrix[i6] = new Distance[clusterCount];
            for (int j = 0; j < clusterCount; j++) {
                distanceToIndexMatrix[i6][j] = new Distance();
            }
            i6++;
            random = random2;
        }
        int[] pixelCountSums3 = new int[clusterCount];
        int iteration = 0;
        while (true) {
            Map<Integer, Integer> pixelToCount2 = pixelToCount;
            if (iteration >= 10) {
                pixelCountSums = pixelCountSums3;
                pointProvider = pointProvider3;
                break;
            }
            int i7 = 0;
            while (i7 < clusterCount) {
                int[] pixels2 = pixels;
                int j2 = i7 + 1;
                while (j2 < clusterCount) {
                    int clustersCreated2 = clustersCreated;
                    double distance = pointProvider3.distance(clusters[i7], clusters[j2]);
                    distanceToIndexMatrix[j2][i7].distance = distance;
                    distanceToIndexMatrix[j2][i7].index = i7;
                    distanceToIndexMatrix[i7][j2].distance = distance;
                    distanceToIndexMatrix[i7][j2].index = j2;
                    j2++;
                    clustersCreated = clustersCreated2;
                    additionalClustersNeeded = additionalClustersNeeded;
                }
                int clustersCreated3 = clustersCreated;
                int additionalClustersNeeded2 = additionalClustersNeeded;
                Arrays.sort(distanceToIndexMatrix[i7]);
                for (int j3 = 0; j3 < clusterCount; j3++) {
                    indexMatrix[i7][j3] = distanceToIndexMatrix[i7][j3].index;
                }
                i7++;
                clustersCreated = clustersCreated3;
                pixels = pixels2;
                additionalClustersNeeded = additionalClustersNeeded2;
            }
            int[] pixels3 = pixels;
            int clustersCreated4 = clustersCreated;
            int additionalClustersNeeded3 = additionalClustersNeeded;
            int pointsMoved = 0;
            int i8 = 0;
            while (i8 < pointCount3) {
                double[] point = points[i8];
                int previousClusterIndex = clusterIndices[i8];
                double[] previousCluster = clusters[previousClusterIndex];
                double previousDistance = pointProvider3.distance(point, previousCluster);
                double minimumDistance = previousDistance;
                int[][] indexMatrix2 = indexMatrix;
                int newClusterIndex = -1;
                int j4 = 0;
                while (j4 < clusterCount) {
                    Distance[][] distanceToIndexMatrix2 = distanceToIndexMatrix;
                    int pointCount4 = pointCount3;
                    int[] counts2 = counts;
                    if (distanceToIndexMatrix[previousClusterIndex][j4].distance < 4.0d * previousDistance) {
                        double distance2 = pointProvider3.distance(point, clusters[j4]);
                        if (distance2 < minimumDistance) {
                            minimumDistance = distance2;
                            newClusterIndex = j4;
                        }
                    }
                    j4++;
                    pointCount3 = pointCount4;
                    distanceToIndexMatrix = distanceToIndexMatrix2;
                    counts = counts2;
                }
                Distance[][] distanceToIndexMatrix3 = distanceToIndexMatrix;
                int pointCount5 = pointCount3;
                int[] counts3 = counts;
                if (newClusterIndex != -1) {
                    double distanceChange = Math.abs(Math.sqrt(minimumDistance) - Math.sqrt(previousDistance));
                    if (distanceChange > 3.0d) {
                        pointsMoved++;
                        clusterIndices[i8] = newClusterIndex;
                    }
                }
                i8++;
                indexMatrix = indexMatrix2;
                pointCount3 = pointCount5;
                distanceToIndexMatrix = distanceToIndexMatrix3;
                counts = counts3;
            }
            Distance[][] distanceToIndexMatrix4 = distanceToIndexMatrix;
            int pointsMoved2 = pointCount3;
            int[] counts4 = counts;
            int[][] indexMatrix3 = indexMatrix;
            if (pointsMoved == 0 && iteration != 0) {
                pixelCountSums = pixelCountSums3;
                pointProvider = pointProvider3;
                break;
            }
            double[] componentASums2 = new double[clusterCount];
            double[] componentBSums = new double[clusterCount];
            double[] componentCSums = new double[clusterCount];
            char c = 0;
            Arrays.fill(pixelCountSums3, 0);
            int i9 = 0;
            while (true) {
                pointCount = pointsMoved2;
                if (i9 >= pointCount) {
                    break;
                }
                int clusterIndex = clusterIndices[i9];
                double[] point2 = points[i9];
                int count = counts4[i9];
                pixelCountSums3[clusterIndex] = pixelCountSums3[clusterIndex] + count;
                componentASums2[clusterIndex] = componentASums2[clusterIndex] + (point2[c] * count);
                componentBSums[clusterIndex] = componentBSums[clusterIndex] + (point2[1] * count);
                componentCSums[clusterIndex] = componentCSums[clusterIndex] + (point2[2] * count);
                i9++;
                pointProvider3 = pointProvider3;
                componentBSums = componentBSums;
                points = points;
                pointsMoved = pointsMoved;
                c = 0;
                pointsMoved2 = pointCount;
            }
            double[][] points2 = points;
            double[] componentBSums2 = componentBSums;
            PointProvider pointProvider4 = pointProvider3;
            int i10 = 0;
            while (i10 < clusterCount) {
                int count2 = pixelCountSums3[i10];
                if (count2 == 0) {
                    clusters[i10] = new double[]{0.0d, 0.0d, 0.0d};
                    componentASums = componentASums2;
                    pixelCountSums2 = pixelCountSums3;
                    pointCount2 = pointCount;
                } else {
                    double a = componentASums2[i10] / count2;
                    pointCount2 = pointCount;
                    double b = componentBSums2[i10] / count2;
                    componentASums = componentASums2;
                    pixelCountSums2 = pixelCountSums3;
                    double c2 = componentCSums[i10] / count2;
                    clusters[i10][0] = a;
                    clusters[i10][1] = b;
                    clusters[i10][2] = c2;
                }
                i10++;
                pointCount = pointCount2;
                pixelCountSums3 = pixelCountSums2;
                componentASums2 = componentASums;
            }
            iteration++;
            pointCount3 = pointCount;
            pixelToCount = pixelToCount2;
            clustersCreated = clustersCreated4;
            pixels = pixels3;
            additionalClustersNeeded = additionalClustersNeeded3;
            pointProvider3 = pointProvider4;
            indexMatrix = indexMatrix3;
            points = points2;
            distanceToIndexMatrix = distanceToIndexMatrix4;
            counts = counts4;
        }
        Map<Integer, Integer> argbToPopulation = new LinkedHashMap<>();
        int i11 = 0;
        while (i11 < clusterCount) {
            int count3 = pixelCountSums[i11];
            if (count3 == 0) {
                pointProvider2 = pointProvider;
            } else {
                pointProvider2 = pointProvider;
                int possibleNewCluster = pointProvider2.toInt(clusters[i11]);
                if (!argbToPopulation.containsKey(Integer.valueOf(possibleNewCluster))) {
                    argbToPopulation.put(Integer.valueOf(possibleNewCluster), Integer.valueOf(count3));
                }
            }
            i11++;
            pointProvider = pointProvider2;
        }
        return argbToPopulation;
    }
}
