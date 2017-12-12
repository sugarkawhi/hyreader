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
    private String content = "直到有人在办公室放《大悲咒》，我终于忍不住了。我需要他们给我解释清楚。\n" +
            "我找来一堆90后，请他们详细讲了讲自己的佛系生活方式。比如——\n" +
            "佛系乘客：给司机打电话说，你不要动，我来找你\n" +
            "佛系健身：下班后去健身房走一走，就很开心\n" +
            "佛系好友：在朋友圈随缘点赞，都是爱的鼓励\n" +
            "把佛祖无欲无求的概念偷换到自己身上，其实就是丧文化的一种表现。\n" +
            "我不把这理解为真正的自我放弃，而是压力和焦虑下，年轻人的自我消解。\n" +
            "况且，好像真的很管用。\n" +
            "我每听完一个故事，都想念一句阿弥陀佛";
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
        simpleReaderView.setCurrentContent(content);
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
