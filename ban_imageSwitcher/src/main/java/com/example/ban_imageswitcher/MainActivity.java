package com.example.ban_imageswitcher;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

public class MainActivity extends AppCompatActivity {

    List images = new ArrayList();
    ImageSwitcher mSwitcher;
    int position;
    float downX;

    ViewGroup group;
    ImageView[] tips;
    boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitcher = findViewById(R.id.mSwitcher);
        group = findViewById(R.id.viewGroup);

        initData();
        mSwitcher.setFactory(imgFactor);
        mSwitcher.setOnTouchListener(touchListen);

        initPointer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;

                while(isRunning){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int cur_item=position;
                            cur_item=(cur_item+1)%images.size();
                            mSwitcher.setImageResource((Integer) images.get(cur_item));
                            setTips(cur_item);
                            position=cur_item;
                        }
                    });
                }
            }
        }).start();

    }

    private void initData() {
        images.add(R.drawable.t1);
        images.add(R.drawable.t2);
        images.add(R.drawable.t3);
    }

    private ViewSwitcher.ViewFactory imgFactor=new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource((Integer) images.get(position));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    };

    private View.OnTouchListener touchListen = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float lastX = motionEvent.getX();
                    //抬起的时候的X坐标大于按下的时候就显示上一张图片
                    if (lastX > downX){
                        if (position > 0){
                            //设置动画
                            mSwitcher.setInAnimation( AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_in_left));
                            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_out_right));
                            position --;
                            mSwitcher.setImageResource( (Integer) images.get( position % images.size() ) );
                           setTips(position);
                        }else {
                            Toast.makeText(getApplication(), "已经是第一张", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(position < images.size() - 1){
                            //右进左出安卓自带的没有，根据安卓自带的自己写
                            mSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.slide_in_right ));
                            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.slide_out_left ));
                            position ++ ;
                            mSwitcher.setImageResource( (Integer) images.get( position % images.size() ) );
                            setTips(position);
                        }else{
                            Toast.makeText(getApplication(), "到了最后一张", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
            return true;
        }
    };

    //初始化下面的小圆点的方法
    private void initPointer() {
        //有多少个界面就new多长的数组
        tips = new ImageView[images.size()];

        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置控件的宽高
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(85, 85));
            //设置控件的padding属性
            imageView.setPadding(20, 0, 20, 0);
            tips[i] = imageView;
            //初始化第一个page页面的图片的原点为选中状态
            if (i == 0) {
                //表示当前图片
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
                /**
                 * 在java代码中动态生成ImageView的时候
                 * 要设置其BackgroundResource属性才有效
                 * 设置ImageResource属性无效
                 */
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            }
            group.addView(tips[i]);
        }
    }

    void setTips(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i==selectItems){
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }else{
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            }
        }
    }
}