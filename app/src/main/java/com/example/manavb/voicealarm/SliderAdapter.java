package com.example.manavb.voicealarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slideImages = {
            R.mipmap.ic_new_launcher_icon_foreground,
            R.drawable.tutorial_slide2_img,
            R.drawable.tutorial_slide3_img,
            R.drawable.tutorial_slide4_img,
            R.drawable.tutorial_slide5_img,
            R.drawable.tutorial_slide6_img
    };

    public String[] slideHeadings = {
            "Welcome!",
            "\"What it does?\"",
            "\"How it works?\"",
            "\"What do I do?\"",
            "\"Then what?\"",
            "\"Sounds good.\""
    };

    public String[] slideDescriptions = {
            "Thanks for downloading Voice Alarm, a different approach to waking up.",
            "Voice Alarm uses your speech to turn off the alarm.",
            "When the alarm goes off, the app will show some text on the screen.",
            "Press the mic button, and speak those words to the phone",
            "If they match, the alarm will dismiss. If not, then it means you're not fully awake (try again).",
            "Fresh mornings make an active and healthy lifestyle.\nSay goodbye to waking up the traditional way."
    };

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView mSlideImageView = view.findViewById(R.id.slide_image);
        TextView mSlideHeading = view.findViewById(R.id.slide_heading);
        TextView mSlideDesc = view.findViewById(R.id.slide_description);

        mSlideImageView.setImageResource(slideImages[position]);
        mSlideHeading.setText(slideHeadings[position]);
        mSlideDesc.setText(slideDescriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((RelativeLayout)object);
    }
}
