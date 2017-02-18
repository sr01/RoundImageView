package com.rosi.roundimageview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private static final int MAX_WIDTH = 1000;
  private RoundImageView imageView;
  private int imageWidth = 800;

  public static float normalizeAB(float minA, float maxA, float minB, float maxB, float value) {
    if ((maxA - minA) == 0) return 0;
    return (maxB - minB) / (maxA - minA) * (value - minA) + minB;
  }

  public static int normalizeAB(int minA, int maxA, int minB, int maxB, int value) {
    return Math.round(normalizeAB((float) minA, (float) maxA, (float) minB, (float) maxB, (float) value));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    imageView = (RoundImageView) findViewById(R.id.iv);

    final TextView widthTextView = (TextView) findViewById(R.id.text_width);
    final TextView borderWidthTextView = (TextView) findViewById(R.id.text_border_width);
    final TextView shadowRadiusTextView = (TextView) findViewById(R.id.text_shadow_radius);

    final SeekBar widthSeekBar = (SeekBar) findViewById(R.id.seekBar_width);
    final SeekBar borderWidthSeekBar = (SeekBar) findViewById(R.id.seekBar_border_width);
    final SeekBar shadowRadiusSeekBar = (SeekBar) findViewById(R.id.seekBar_shadow_radius);

    widthSeekBar.setProgress(normalizeAB(0, MAX_WIDTH, 0, widthSeekBar.getMax(), imageWidth));
    widthTextView.setText(getString(R.string.image_width, imageWidth));
    widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        imageWidth = normalizeAB(0, seekBar.getMax(), 0, MAX_WIDTH, progress);
        widthTextView.setText(getString(R.string.image_width, imageWidth));
        updateImage();
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    borderWidthSeekBar.setProgress((int) imageView.getStrokeWidth());
    borderWidthTextView.setText(getString(R.string.border_width, (int) imageView.getStrokeWidth()));
    borderWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        imageView.setStrokeWidth(progress);
        borderWidthTextView.setText(getString(R.string.border_width, (int) imageView.getStrokeWidth()));
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    shadowRadiusSeekBar.setProgress((int) imageView.getShadowRadius());
    shadowRadiusTextView.setText(getString(R.string.shadow_radius, shadowRadiusSeekBar.getProgress()));
    shadowRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        imageView.setShadowRadius(progress);
        shadowRadiusTextView.setText(getString(R.string.shadow_radius, shadowRadiusSeekBar.getProgress()));
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    updateImage();
  }

  private void updateImage() {
    ViewGroup.LayoutParams params = imageView.getLayoutParams();
    params.height = params.width = imageWidth;
    imageView.setLayoutParams(params);
  }
}
