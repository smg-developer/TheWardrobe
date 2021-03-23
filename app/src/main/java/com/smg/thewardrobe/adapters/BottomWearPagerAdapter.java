package com.smg.thewardrobe.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.smg.thewardrobe.R;
import com.smg.thewardrobe.models.ImageWearModel;
import com.smg.thewardrobe.utilities.ImageUtility;

import java.util.ArrayList;

public class BottomWearPagerAdapter extends PagerAdapter {

    private ArrayList<ImageWearModel> IMAGES;
    private LayoutInflater inflater;
    private Context context;


    public BottomWearPagerAdapter(Context context,ArrayList<ImageWearModel> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.pager_image_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);


        imageView.setImageBitmap(ImageUtility.getImage(IMAGES.get(position).imageBlob));

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
