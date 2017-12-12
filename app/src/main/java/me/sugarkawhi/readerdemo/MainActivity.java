package me.sugarkawhi.readerdemo;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import me.sugarkawhi.hyreader.view.SimpleReaderView;

public class MainActivity extends AppCompatActivity {
    SimpleReaderView simpleReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleReaderView = findViewById(R.id.readerView);
        simpleReaderView.setBackgroundBitmap(BitmapFactory.decodeResource(getResources(), me.sugarkawhi.hyreader.R.drawable.paper));
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                simpleReaderView.setBatteryElectricity(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menu_sync)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        simpleReaderView.setCurrentChapterName(Math.random() * 10 + "");
                        simpleReaderView.setTime("10:11");
                        return false;
                    }
                });
        return super.onCreateOptionsMenu(menu);
    }
}
