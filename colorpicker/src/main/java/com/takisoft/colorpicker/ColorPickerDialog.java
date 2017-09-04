package com.takisoft.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ColorPickerDialog extends AlertDialog implements ColorPickerSwatch.OnColorSelectedListener {
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    @IntDef({SIZE_LARGE, SIZE_SMALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Size {
    }

    private final ColorPickerPaletteFlex2 mPalette;
    private final ProgressBar mProgress;

    private ColorPickerSwatch.OnColorSelectedListener listener;

    private Params params;


    public ColorPickerDialog(@NonNull Context context, ColorPickerSwatch.OnColorSelectedListener listener, Params params) {
        this(context, 0, listener, params);
    }

    public ColorPickerDialog(@NonNull Context context, int themeResId, ColorPickerSwatch.OnColorSelectedListener listener, Params params) {
        super(context, resolveDialogTheme(context, themeResId));

        final Context themeContext = getContext();

        this.listener = listener;
        this.params = params;

        View view = LayoutInflater.from(themeContext).inflate(R.layout.color_picker_dialog, this.getListView());
        setView(view);

        mProgress = view.findViewById(android.R.id.progress);
        mPalette = view.findViewById(R.id.color_picker);
        //mPalette.init(params.mSize, params.mColumns, this);
        mPalette.setOnColorSelectedListener(this);


        if (params.mColors != null) {
            showPaletteView();
        }

        /*
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);
         */
    }

    public void showPaletteView() {
        if (mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.GONE);
            refreshPalette();
            mPalette.setVisibility(View.VISIBLE);
        }
    }

    private void refreshPalette() {
        if (mPalette != null && params.mColors != null) {
            mPalette.setup(params);
            //mPalette.drawPalette(params.mColors, params.mSelectedColor, params.mColorContentDescriptions);
        }
    }

    @Override
    public void onColorSelected(int color) {
        if (listener != null) {
            listener.onColorSelected(color);
        }

        if (color != params.mSelectedColor) {
            params.mSelectedColor = color;
            // Redraw palette to show checkmark on newly selected color before dismissing.
            //mPalette.drawPalette(params.mColors, params.mSelectedColor);
            mPalette.setup(params);
        }

        dismiss();
    }

    private static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            return R.style.ThemeOverlay_Material_Dialog_ColorPicker;
        } else {
            return resId;
        }
    }

    public static class Params {
        int[] mColors;
        CharSequence[] mColorContentDescriptions;
        int mSelectedColor;
        int mColumns;
        int mSize;

        int mSwatchLength;
        int mMarginSize;

        private Params() {
        }

        public static class Builder {
            private int[] colors;
            private CharSequence[] colorContentDescriptions;
            private int selectedColor;
            private int columns;

            @Size
            private int size;

            private Context context;

            public Builder(Context context) {
                this.context = context;
            }

            public Builder setColors(int[] colors) {
                this.colors = colors;
                return this;
            }

            public Builder setColorContentDescriptions(CharSequence[] colorContentDescriptions) {
                this.colorContentDescriptions = colorContentDescriptions;
                return this;
            }

            public Builder setSelectedColor(int selectedColor) {
                this.selectedColor = selectedColor;
                return this;
            }

            public Builder setColumns(int columns) {
                this.columns = columns;
                return this;
            }

            public Builder setSize(@Size int size) {
                this.size = size;
                return this;
            }

            public Params build() {
                Params params = new Params();

                params.mColors = colors;
                params.mColorContentDescriptions = colorContentDescriptions;
                params.mSelectedColor = selectedColor;
                params.mColumns = columns;
                params.mSize = size;

                Resources res = context.getResources();

                if (size == ColorPickerDialog.SIZE_LARGE) {
                    params.mSwatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_large);
                    params.mMarginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margins_large);
                } else {
                    params.mSwatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_small);
                    params.mMarginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margins_small);
                }

                return params;
            }
        }
    }
}
