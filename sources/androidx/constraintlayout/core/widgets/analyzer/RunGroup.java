package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
class RunGroup {
    public static final int BASELINE = 2;
    public static final int END = 1;
    public static final int START = 0;
    public static int index;
    int direction;
    WidgetRun firstRun;
    int groupIndex;
    WidgetRun lastRun;
    public int position = 0;
    public boolean dual = false;
    ArrayList<WidgetRun> runs = new ArrayList<>();

    public RunGroup(WidgetRun run, int dir) {
        this.firstRun = null;
        this.lastRun = null;
        this.groupIndex = 0;
        this.groupIndex = index;
        index++;
        this.firstRun = run;
        this.lastRun = run;
        this.direction = dir;
    }

    public void add(WidgetRun run) {
        this.runs.add(run);
        this.lastRun = run;
    }

    private long traverseStart(DependencyNode node, long startPosition) {
        WidgetRun run = node.run;
        if (run instanceof HelperReferences) {
            return startPosition;
        }
        long position = startPosition;
        int count = node.dependencies.size();
        for (int i = 0; i < count; i++) {
            Dependency dependency = node.dependencies.get(i);
            if (dependency instanceof DependencyNode) {
                DependencyNode nextNode = (DependencyNode) dependency;
                if (nextNode.run != run) {
                    position = Math.max(position, traverseStart(nextNode, nextNode.margin + startPosition));
                }
            }
        }
        if (node == run.start) {
            long dimension = run.getWrapDimension();
            return Math.max(Math.max(position, traverseStart(run.end, startPosition + dimension)), (startPosition + dimension) - run.end.margin);
        }
        return position;
    }

    private long traverseEnd(DependencyNode node, long startPosition) {
        WidgetRun run = node.run;
        if (run instanceof HelperReferences) {
            return startPosition;
        }
        long position = startPosition;
        int count = node.dependencies.size();
        for (int i = 0; i < count; i++) {
            Dependency dependency = node.dependencies.get(i);
            if (dependency instanceof DependencyNode) {
                DependencyNode nextNode = (DependencyNode) dependency;
                if (nextNode.run != run) {
                    position = Math.min(position, traverseEnd(nextNode, nextNode.margin + startPosition));
                }
            }
        }
        if (node == run.end) {
            long dimension = run.getWrapDimension();
            return Math.min(Math.min(position, traverseEnd(run.start, startPosition - dimension)), (startPosition - dimension) - run.start.margin);
        }
        return position;
    }

    public long computeWrapSize(ConstraintWidgetContainer container, int orientation) {
        if (this.firstRun instanceof ChainRun) {
            ChainRun chainRun = (ChainRun) this.firstRun;
            if (chainRun.orientation != orientation) {
                return 0L;
            }
        } else if (orientation == 0) {
            if (!(this.firstRun instanceof HorizontalWidgetRun)) {
                return 0L;
            }
        } else if (!(this.firstRun instanceof VerticalWidgetRun)) {
            return 0L;
        }
        DependencyNode containerStart = orientation == 0 ? container.horizontalRun.start : container.verticalRun.start;
        DependencyNode containerEnd = orientation == 0 ? container.horizontalRun.end : container.verticalRun.end;
        boolean runWithStartTarget = this.firstRun.start.targets.contains(containerStart);
        boolean runWithEndTarget = this.firstRun.end.targets.contains(containerEnd);
        long dimension = this.firstRun.getWrapDimension();
        if (runWithStartTarget && runWithEndTarget) {
            long maxPosition = traverseStart(this.firstRun.start, 0L);
            long minPosition = traverseEnd(this.firstRun.end, 0L);
            long endGap = maxPosition - dimension;
            long maxPosition2 = -this.firstRun.end.margin;
            if (endGap >= maxPosition2) {
                endGap += this.firstRun.end.margin;
            }
            long startGap = ((-minPosition) - dimension) - this.firstRun.start.margin;
            if (startGap >= this.firstRun.start.margin) {
                startGap -= this.firstRun.start.margin;
            }
            float bias = this.firstRun.widget.getBiasPercent(orientation);
            long gap = 0;
            if (bias > 0.0f) {
                gap = (long) ((startGap / bias) + (endGap / (1.0f - bias)));
            }
            long runDimension = ((long) ((gap * bias) + 0.5f)) + dimension + ((long) ((gap * (1.0f - bias)) + 0.5f));
            return (this.firstRun.start.margin + runDimension) - this.firstRun.end.margin;
        }
        if (runWithStartTarget) {
            long maxPosition3 = traverseStart(this.firstRun.start, this.firstRun.start.margin);
            long runDimension2 = this.firstRun.start.margin + dimension;
            return Math.max(maxPosition3, runDimension2);
        }
        if (runWithEndTarget) {
            long minPosition2 = traverseEnd(this.firstRun.end, this.firstRun.end.margin);
            long runDimension3 = (-this.firstRun.end.margin) + dimension;
            return Math.max(-minPosition2, runDimension3);
        }
        return (this.firstRun.start.margin + this.firstRun.getWrapDimension()) - this.firstRun.end.margin;
    }

    private boolean defineTerminalWidget(WidgetRun run, int orientation) {
        if (!run.widget.isTerminalWidget[orientation]) {
            return false;
        }
        for (Dependency dependency : run.start.dependencies) {
            if (dependency instanceof DependencyNode) {
                DependencyNode node = (DependencyNode) dependency;
                if (node.run != run && node == node.run.start) {
                    if (run instanceof ChainRun) {
                        ChainRun chainRun = (ChainRun) run;
                        Iterator<WidgetRun> it = chainRun.widgets.iterator();
                        while (it.hasNext()) {
                            WidgetRun widgetChainRun = it.next();
                            defineTerminalWidget(widgetChainRun, orientation);
                        }
                    } else if (!(run instanceof HelperReferences)) {
                        run.widget.isTerminalWidget[orientation] = false;
                    }
                    defineTerminalWidget(node.run, orientation);
                }
            }
        }
        for (Dependency dependency2 : run.end.dependencies) {
            if (dependency2 instanceof DependencyNode) {
                DependencyNode node2 = (DependencyNode) dependency2;
                if (node2.run != run && node2 == node2.run.start) {
                    if (run instanceof ChainRun) {
                        ChainRun chainRun2 = (ChainRun) run;
                        Iterator<WidgetRun> it2 = chainRun2.widgets.iterator();
                        while (it2.hasNext()) {
                            WidgetRun widgetChainRun2 = it2.next();
                            defineTerminalWidget(widgetChainRun2, orientation);
                        }
                    } else if (!(run instanceof HelperReferences)) {
                        run.widget.isTerminalWidget[orientation] = false;
                    }
                    defineTerminalWidget(node2.run, orientation);
                }
            }
        }
        return false;
    }

    public void defineTerminalWidgets(boolean horizontalCheck, boolean verticalCheck) {
        if (horizontalCheck && (this.firstRun instanceof HorizontalWidgetRun)) {
            defineTerminalWidget(this.firstRun, 0);
        }
        if (verticalCheck && (this.firstRun instanceof VerticalWidgetRun)) {
            defineTerminalWidget(this.firstRun, 1);
        }
    }
}
