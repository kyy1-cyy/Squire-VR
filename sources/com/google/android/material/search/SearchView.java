package com.google.android.material.search;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.TextViewCompat;
import androidx.customview.view.AbsSavedState;
import com.google.android.material.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.ElevationOverlayProvider;
import com.google.android.material.internal.ClippableRoundedCornerLayout;
import com.google.android.material.internal.ContextUtils;
import com.google.android.material.internal.FadeThroughDrawable;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.internal.ToolbarUtils;
import com.google.android.material.internal.TouchObserverFrameLayout;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.shape.MaterialShapeUtils;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class SearchView extends FrameLayout implements CoordinatorLayout.AttachedBehavior {
    private static final int DEF_STYLE_RES = R.style.Widget_Material3_SearchView;
    private static final long TALKBACK_FOCUS_CHANGE_DELAY_MS = 100;
    private boolean animatedMenuItems;
    private boolean animatedNavigationIcon;
    private boolean autoShowKeyboard;
    final View backgroundView;
    private Map<View, Integer> childImportantForAccessibilityMap;
    final ImageButton clearButton;
    final TouchObserverFrameLayout contentContainer;
    private TransitionState currentTransitionState;
    final View divider;
    final Toolbar dummyToolbar;
    final EditText editText;
    private final ElevationOverlayProvider elevationOverlayProvider;
    final FrameLayout headerContainer;
    private final boolean layoutInflated;
    final ClippableRoundedCornerLayout rootView;
    final View scrim;
    private SearchBar searchBar;
    final TextView searchPrefix;
    private final SearchViewAnimationHelper searchViewAnimationHelper;
    private int softInputMode;
    final View statusBarSpacer;
    private boolean statusBarSpacerEnabledOverride;
    final MaterialToolbar toolbar;
    final FrameLayout toolbarContainer;
    private final Set<TransitionListener> transitionListeners;
    private boolean useWindowInsetsController;

    public interface TransitionListener {
        void onStateChanged(SearchView searchView, TransitionState transitionState, TransitionState transitionState2);
    }

    public enum TransitionState {
        HIDING,
        HIDDEN,
        SHOWING,
        SHOWN
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialSearchViewStyle);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr);
        this.transitionListeners = new LinkedHashSet();
        this.softInputMode = 16;
        this.currentTransitionState = TransitionState.HIDDEN;
        Context context2 = getContext();
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context2, attrs, R.styleable.SearchView, defStyleAttr, DEF_STYLE_RES, new int[0]);
        int headerLayoutResId = a.getResourceId(R.styleable.SearchView_headerLayout, -1);
        int textAppearanceResId = a.getResourceId(R.styleable.SearchView_android_textAppearance, -1);
        String text = a.getString(R.styleable.SearchView_android_text);
        String hint = a.getString(R.styleable.SearchView_android_hint);
        String searchPrefixText = a.getString(R.styleable.SearchView_searchPrefixText);
        boolean useDrawerArrowDrawable = a.getBoolean(R.styleable.SearchView_useDrawerArrowDrawable, false);
        this.animatedNavigationIcon = a.getBoolean(R.styleable.SearchView_animateNavigationIcon, true);
        this.animatedMenuItems = a.getBoolean(R.styleable.SearchView_animateMenuItems, true);
        boolean hideNavigationIcon = a.getBoolean(R.styleable.SearchView_hideNavigationIcon, false);
        this.autoShowKeyboard = a.getBoolean(R.styleable.SearchView_autoShowKeyboard, true);
        a.recycle();
        LayoutInflater.from(context2).inflate(R.layout.mtrl_search_view, this);
        this.layoutInflated = true;
        this.scrim = findViewById(R.id.search_view_scrim);
        this.rootView = (ClippableRoundedCornerLayout) findViewById(R.id.search_view_root);
        this.backgroundView = findViewById(R.id.search_view_background);
        this.statusBarSpacer = findViewById(R.id.search_view_status_bar_spacer);
        this.headerContainer = (FrameLayout) findViewById(R.id.search_view_header_container);
        this.toolbarContainer = (FrameLayout) findViewById(R.id.search_view_toolbar_container);
        this.toolbar = (MaterialToolbar) findViewById(R.id.search_view_toolbar);
        this.dummyToolbar = (Toolbar) findViewById(R.id.search_view_dummy_toolbar);
        this.searchPrefix = (TextView) findViewById(R.id.search_view_search_prefix);
        this.editText = (EditText) findViewById(R.id.search_view_edit_text);
        this.clearButton = (ImageButton) findViewById(R.id.search_view_clear_button);
        this.divider = findViewById(R.id.search_view_divider);
        this.contentContainer = (TouchObserverFrameLayout) findViewById(R.id.search_view_content_container);
        this.searchViewAnimationHelper = new SearchViewAnimationHelper(this);
        this.elevationOverlayProvider = new ElevationOverlayProvider(context2);
        setUpRootView();
        setUpBackgroundViewElevationOverlay();
        setUpHeaderLayout(headerLayoutResId);
        setSearchPrefixText(searchPrefixText);
        setUpEditText(textAppearanceResId, text, hint);
        setUpBackButton(useDrawerArrowDrawable, hideNavigationIcon);
        setUpClearButton();
        setUpContentOnTouchListener();
        setUpInsetListeners();
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (this.layoutInflated) {
            this.contentContainer.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateSoftInputMode();
    }

    @Override // android.view.View
    public void setElevation(float elevation) {
        super.setElevation(elevation);
        setUpBackgroundViewElevationOverlay(elevation);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MaterialShapeUtils.setParentAbsoluteElevation(this);
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior
    public CoordinatorLayout.Behavior<SearchView> getBehavior() {
        return new Behavior();
    }

    private Window getActivityWindow() {
        Activity activity = ContextUtils.getActivity(getContext());
        if (activity == null) {
            return null;
        }
        return activity.getWindow();
    }

    static /* synthetic */ boolean lambda$setUpRootView$0(View v, MotionEvent event) {
        return true;
    }

    private void setUpRootView() {
        this.rootView.setOnTouchListener(new View.OnTouchListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SearchView.lambda$setUpRootView$0(view, motionEvent);
            }
        });
    }

    private void setUpBackgroundViewElevationOverlay() {
        setUpBackgroundViewElevationOverlay(getOverlayElevation());
    }

    private void setUpBackgroundViewElevationOverlay(float elevation) {
        if (this.elevationOverlayProvider == null || this.backgroundView == null) {
            return;
        }
        int backgroundColor = this.elevationOverlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(elevation);
        this.backgroundView.setBackgroundColor(backgroundColor);
    }

    private float getOverlayElevation() {
        if (this.searchBar != null) {
            return this.searchBar.getCompatElevation();
        }
        return getResources().getDimension(R.dimen.m3_searchview_elevation);
    }

    private void setUpHeaderLayout(int headerLayoutResId) {
        if (headerLayoutResId != -1) {
            View headerView = LayoutInflater.from(getContext()).inflate(headerLayoutResId, (ViewGroup) this.headerContainer, false);
            addHeaderView(headerView);
        }
    }

    private void setUpEditText(int textAppearanceResId, String text, String hint) {
        if (textAppearanceResId != -1) {
            TextViewCompat.setTextAppearance(this.editText, textAppearanceResId);
        }
        this.editText.setText(text);
        this.editText.setHint(hint);
    }

    private void setUpBackButton(boolean useDrawerArrowDrawable, boolean hideNavigationIcon) {
        if (hideNavigationIcon) {
            this.toolbar.setNavigationIcon((Drawable) null);
            return;
        }
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m65x40e9b054(view);
            }
        });
        if (useDrawerArrowDrawable) {
            DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(getContext());
            drawerArrowDrawable.setColor(MaterialColors.getColor(this, R.attr.colorOnSurface));
            this.toolbar.setNavigationIcon(drawerArrowDrawable);
        }
    }

    /* renamed from: lambda$setUpBackButton$1$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ void m65x40e9b054(View v) {
        hide();
    }

    private void setUpClearButton() {
        this.clearButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m66xf4a71c3b(view);
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() { // from class: com.google.android.material.search.SearchView.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchView.this.clearButton.setVisibility(s.length() > 0 ? 0 : 8);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /* renamed from: lambda$setUpClearButton$2$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ void m66xf4a71c3b(View v) {
        clearText();
        requestFocusAndShowKeyboardIfNeeded();
    }

    private void setUpContentOnTouchListener() {
        this.contentContainer.setOnTouchListener(new View.OnTouchListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.m67x1cd2d198(view, motionEvent);
            }
        });
    }

    /* renamed from: lambda$setUpContentOnTouchListener$3$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ boolean m67x1cd2d198(View v, MotionEvent event) {
        if (isAdjustNothingSoftInputMode()) {
            clearFocusAndHideKeyboard();
            return false;
        }
        return false;
    }

    private void setUpStatusBarSpacer(int height) {
        if (this.statusBarSpacer.getLayoutParams().height != height) {
            this.statusBarSpacer.getLayoutParams().height = height;
            this.statusBarSpacer.requestLayout();
        }
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void updateNavigationIconIfNeeded() {
        if (this.toolbar == null || isNavigationIconDrawerArrowDrawable(this.toolbar)) {
            return;
        }
        int navigationIcon = R.drawable.ic_arrow_back_black_24;
        if (this.searchBar == null) {
            this.toolbar.setNavigationIcon(navigationIcon);
            return;
        }
        Drawable navigationIconDrawable = DrawableCompat.wrap(AppCompatResources.getDrawable(getContext(), navigationIcon).mutate());
        if (this.toolbar.getNavigationIconTint() != null) {
            DrawableCompat.setTint(navigationIconDrawable, this.toolbar.getNavigationIconTint().intValue());
        }
        this.toolbar.setNavigationIcon(new FadeThroughDrawable(this.searchBar.getNavigationIcon(), navigationIconDrawable));
        updateNavigationIconProgressIfNeeded();
    }

    private boolean isNavigationIconDrawerArrowDrawable(Toolbar toolbar) {
        return DrawableCompat.unwrap(toolbar.getNavigationIcon()) instanceof DrawerArrowDrawable;
    }

    private void setUpInsetListeners() {
        setUpToolbarInsetListener();
        setUpDividerInsetListener();
        setUpStatusBarSpacerInsetListener();
    }

    private void setUpToolbarInsetListener() {
        ViewUtils.doOnApplyWindowInsets(this.toolbar, new ViewUtils.OnApplyWindowInsetsListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda8
            @Override // com.google.android.material.internal.ViewUtils.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
                return this.f$0.m69x7371bf54(view, windowInsetsCompat, relativePadding);
            }
        });
    }

    /* renamed from: lambda$setUpToolbarInsetListener$4$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m69x7371bf54(View view, WindowInsetsCompat insets, ViewUtils.RelativePadding initialPadding) {
        boolean isRtl = ViewUtils.isLayoutRtl(this.toolbar);
        int paddingLeft = isRtl ? initialPadding.end : initialPadding.start;
        int paddingRight = isRtl ? initialPadding.start : initialPadding.end;
        this.toolbar.setPadding(insets.getSystemWindowInsetLeft() + paddingLeft, initialPadding.top, insets.getSystemWindowInsetRight() + paddingRight, initialPadding.bottom);
        return insets;
    }

    private void setUpStatusBarSpacerInsetListener() {
        setUpStatusBarSpacer(getStatusBarHeight());
        ViewCompat.setOnApplyWindowInsetsListener(this.statusBarSpacer, new OnApplyWindowInsetsListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda4
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return this.f$0.m68x941b8403(view, windowInsetsCompat);
            }
        });
    }

    /* renamed from: lambda$setUpStatusBarSpacerInsetListener$5$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m68x941b8403(View v, WindowInsetsCompat insets) {
        int systemWindowInsetTop = insets.getSystemWindowInsetTop();
        setUpStatusBarSpacer(systemWindowInsetTop);
        if (!this.statusBarSpacerEnabledOverride) {
            setStatusBarSpacerEnabledInternal(systemWindowInsetTop > 0);
        }
        return insets;
    }

    private void setUpDividerInsetListener() {
        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.divider.getLayoutParams();
        final int leftMargin = layoutParams.leftMargin;
        final int rightMargin = layoutParams.rightMargin;
        ViewCompat.setOnApplyWindowInsetsListener(this.divider, new OnApplyWindowInsetsListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda3
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return SearchView.lambda$setUpDividerInsetListener$6(layoutParams, leftMargin, rightMargin, view, windowInsetsCompat);
            }
        });
    }

    static /* synthetic */ WindowInsetsCompat lambda$setUpDividerInsetListener$6(ViewGroup.MarginLayoutParams layoutParams, int leftMargin, int rightMargin, View v, WindowInsetsCompat insets) {
        layoutParams.leftMargin = insets.getSystemWindowInsetLeft() + leftMargin;
        layoutParams.rightMargin = insets.getSystemWindowInsetRight() + rightMargin;
        return insets;
    }

    public boolean isSetupWithSearchBar() {
        return this.searchBar != null;
    }

    public void setupWithSearchBar(SearchBar searchBar) {
        this.searchBar = searchBar;
        this.searchViewAnimationHelper.setSearchBar(searchBar);
        if (searchBar != null) {
            searchBar.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m70x986696e6(view);
                }
            });
        }
        updateNavigationIconIfNeeded();
        setUpBackgroundViewElevationOverlay();
    }

    /* renamed from: lambda$setupWithSearchBar$7$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ void m70x986696e6(View v) {
        show();
    }

    public void addHeaderView(View headerView) {
        this.headerContainer.addView(headerView);
        this.headerContainer.setVisibility(0);
    }

    public void removeHeaderView(View headerView) {
        this.headerContainer.removeView(headerView);
        if (this.headerContainer.getChildCount() == 0) {
            this.headerContainer.setVisibility(8);
        }
    }

    public void removeAllHeaderViews() {
        this.headerContainer.removeAllViews();
        this.headerContainer.setVisibility(8);
    }

    public void setAnimatedNavigationIcon(boolean animatedNavigationIcon) {
        this.animatedNavigationIcon = animatedNavigationIcon;
    }

    public boolean isAnimatedNavigationIcon() {
        return this.animatedNavigationIcon;
    }

    public void setMenuItemsAnimated(boolean menuItemsAnimated) {
        this.animatedMenuItems = menuItemsAnimated;
    }

    public boolean isMenuItemsAnimated() {
        return this.animatedMenuItems;
    }

    public void setAutoShowKeyboard(boolean autoShowKeyboard) {
        this.autoShowKeyboard = autoShowKeyboard;
    }

    public boolean isAutoShowKeyboard() {
        return this.autoShowKeyboard;
    }

    public void setUseWindowInsetsController(boolean useWindowInsetsController) {
        this.useWindowInsetsController = useWindowInsetsController;
    }

    public boolean isUseWindowInsetsController() {
        return this.useWindowInsetsController;
    }

    public void addTransitionListener(TransitionListener transitionListener) {
        this.transitionListeners.add(transitionListener);
    }

    public void removeTransitionListener(TransitionListener transitionListener) {
        this.transitionListeners.remove(transitionListener);
    }

    public void inflateMenu(int menuResId) {
        this.toolbar.inflateMenu(menuResId);
    }

    public void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        this.toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    public TextView getSearchPrefix() {
        return this.searchPrefix;
    }

    public void setSearchPrefixText(CharSequence searchPrefixText) {
        this.searchPrefix.setText(searchPrefixText);
        this.searchPrefix.setVisibility(TextUtils.isEmpty(searchPrefixText) ? 8 : 0);
    }

    public CharSequence getSearchPrefixText() {
        return this.searchPrefix.getText();
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public EditText getEditText() {
        return this.editText;
    }

    public Editable getText() {
        return this.editText.getText();
    }

    public void setText(CharSequence text) {
        this.editText.setText(text);
    }

    public void setText(int textResId) {
        this.editText.setText(textResId);
    }

    public void clearText() {
        this.editText.setText(HttpUrl.FRAGMENT_ENCODE_SET);
    }

    public CharSequence getHint() {
        return this.editText.getHint();
    }

    public void setHint(CharSequence hint) {
        this.editText.setHint(hint);
    }

    public void setHint(int hintResId) {
        this.editText.setHint(hintResId);
    }

    public int getSoftInputMode() {
        return this.softInputMode;
    }

    public void updateSoftInputMode() {
        Window window = getActivityWindow();
        if (window != null) {
            this.softInputMode = window.getAttributes().softInputMode;
        }
    }

    public void setStatusBarSpacerEnabled(boolean enabled) {
        this.statusBarSpacerEnabledOverride = true;
        setStatusBarSpacerEnabledInternal(enabled);
    }

    private void setStatusBarSpacerEnabledInternal(boolean enabled) {
        this.statusBarSpacer.setVisibility(enabled ? 0 : 8);
    }

    public TransitionState getCurrentTransitionState() {
        return this.currentTransitionState;
    }

    void setTransitionState(TransitionState state) {
        if (this.currentTransitionState.equals(state)) {
            return;
        }
        TransitionState previousState = this.currentTransitionState;
        this.currentTransitionState = state;
        Set<TransitionListener> listeners = new LinkedHashSet<>(this.transitionListeners);
        for (TransitionListener listener : listeners) {
            listener.onStateChanged(this, previousState, state);
        }
    }

    public boolean isShowing() {
        return this.currentTransitionState.equals(TransitionState.SHOWN) || this.currentTransitionState.equals(TransitionState.SHOWING);
    }

    public void show() {
        if (this.currentTransitionState.equals(TransitionState.SHOWN) || this.currentTransitionState.equals(TransitionState.SHOWING)) {
            return;
        }
        this.searchViewAnimationHelper.show();
        setModalForAccessibility(true);
    }

    public void hide() {
        if (this.currentTransitionState.equals(TransitionState.HIDDEN) || this.currentTransitionState.equals(TransitionState.HIDING)) {
            return;
        }
        this.searchViewAnimationHelper.hide();
        setModalForAccessibility(false);
    }

    public void setVisible(boolean visible) {
        boolean wasVisible = this.rootView.getVisibility() == 0;
        this.rootView.setVisibility(visible ? 0 : 8);
        updateNavigationIconProgressIfNeeded();
        if (wasVisible != visible) {
            setModalForAccessibility(visible);
        }
        setTransitionState(visible ? TransitionState.SHOWN : TransitionState.HIDDEN);
    }

    private void updateNavigationIconProgressIfNeeded() {
        ImageButton backButton = ToolbarUtils.getNavigationIconButton(this.toolbar);
        if (backButton == null) {
            return;
        }
        int progress = this.rootView.getVisibility() == 0 ? 1 : 0;
        Drawable drawable = DrawableCompat.unwrap(backButton.getDrawable());
        if (drawable instanceof DrawerArrowDrawable) {
            ((DrawerArrowDrawable) drawable).setProgress(progress);
        }
        if (drawable instanceof FadeThroughDrawable) {
            ((FadeThroughDrawable) drawable).setProgress(progress);
        }
    }

    void requestFocusAndShowKeyboardIfNeeded() {
        if (this.autoShowKeyboard) {
            requestFocusAndShowKeyboard();
        }
    }

    public void requestFocusAndShowKeyboard() {
        this.editText.postDelayed(new Runnable() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m64x2b2700d7();
            }
        }, TALKBACK_FOCUS_CHANGE_DELAY_MS);
    }

    /* renamed from: lambda$requestFocusAndShowKeyboard$8$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ void m64x2b2700d7() {
        if (this.editText.requestFocus()) {
            this.editText.sendAccessibilityEvent(8);
        }
        ViewUtils.showKeyboard(this.editText, this.useWindowInsetsController);
    }

    public void clearFocusAndHideKeyboard() {
        this.editText.post(new Runnable() { // from class: com.google.android.material.search.SearchView$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m63xff5aa7db();
            }
        });
    }

    /* renamed from: lambda$clearFocusAndHideKeyboard$9$com-google-android-material-search-SearchView, reason: not valid java name */
    /* synthetic */ void m63xff5aa7db() {
        this.editText.clearFocus();
        if (this.searchBar != null) {
            this.searchBar.requestFocus();
        }
        ViewUtils.hideKeyboard(this.editText, this.useWindowInsetsController);
    }

    boolean isAdjustNothingSoftInputMode() {
        return this.softInputMode == 48;
    }

    public void setModalForAccessibility(boolean isSearchViewModal) {
        ViewGroup rootView = (ViewGroup) getRootView();
        if (isSearchViewModal) {
            this.childImportantForAccessibilityMap = new HashMap(rootView.getChildCount());
        }
        updateChildImportantForAccessibility(rootView, isSearchViewModal);
        if (!isSearchViewModal) {
            this.childImportantForAccessibilityMap = null;
        }
    }

    public void setToolbarTouchscreenBlocksFocus(boolean touchscreenBlocksFocus) {
        this.toolbar.setTouchscreenBlocksFocus(touchscreenBlocksFocus);
    }

    private void updateChildImportantForAccessibility(ViewGroup parent, boolean isSearchViewModal) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child != this) {
                if (child.findViewById(this.rootView.getId()) != null) {
                    updateChildImportantForAccessibility((ViewGroup) child, isSearchViewModal);
                } else if (!isSearchViewModal) {
                    if (this.childImportantForAccessibilityMap != null && this.childImportantForAccessibilityMap.containsKey(child)) {
                        ViewCompat.setImportantForAccessibility(child, this.childImportantForAccessibilityMap.get(child).intValue());
                    }
                } else {
                    this.childImportantForAccessibilityMap.put(child, Integer.valueOf(child.getImportantForAccessibility()));
                    ViewCompat.setImportantForAccessibility(child, 4);
                }
            }
        }
    }

    public static class Behavior extends CoordinatorLayout.Behavior<SearchView> {
        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
        public boolean onDependentViewChanged(CoordinatorLayout parent, SearchView child, View dependency) {
            if (!child.isSetupWithSearchBar() && (dependency instanceof SearchBar)) {
                child.setupWithSearchBar((SearchBar) dependency);
                return false;
            }
            return false;
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        CharSequence text = getText();
        savedState.text = text == null ? null : text.toString();
        savedState.visibility = this.rootView.getVisibility();
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setText(savedState.text);
        setVisible(savedState.visibility == 0);
    }

    static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: com.google.android.material.search.SearchView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        String text;
        int visibility;

        public SavedState(Parcel source) {
            this(source, null);
        }

        public SavedState(Parcel source, ClassLoader classLoader) {
            super(source, classLoader);
            this.text = source.readString();
            this.visibility = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override // androidx.customview.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.text);
            dest.writeInt(this.visibility);
        }
    }
}
