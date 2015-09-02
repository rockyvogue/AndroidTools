
package com.spt.carengine.entertainment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.spt.carengine.R;
import com.spt.carengine.album.entity.AlbumListInfo;
import com.spt.carengine.album.entity.PhotoListInfo;
import com.spt.carengine.entertainment.bean.MusicSortBean;
import com.spt.carengine.utils.ScreenUtils;

import org.apache.http.Header;

/**
 * <功能描述> 音乐排行榜中GridView 适配器
 * 
 * @author ymm
 */
public class MusicSortAdapter extends ArrayAdapter<MusicSortBean> {

    private static final int ALBUM_ITEM_WIDTH = 234;
    private static final int ALBUM_ITEM_HEIGHT = 277;
    private LayoutInflater mInflater;

    public MusicSortAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MusicSortBean info = getItem(position);

        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_music_sort,
                    parent, false);
            holder = new Holder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.iv_music_sort);

            int itemWidth = ScreenUtils.getRealWidthValue(getContext(),
                    ALBUM_ITEM_WIDTH);
            int itemHeight = ScreenUtils.getRealHeightValue(getContext(),
                    ALBUM_ITEM_HEIGHT);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    itemWidth, itemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(holder);
        } else {

            holder = (Holder) convertView.getTag();
        }

        final Holder fHolder = holder;

        if (info.getUrl() != null) {
            // 读取网络数据
            client.get(info.getUrl(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                        String responseString) {
                    super.onSuccess(statusCode, headers, responseString);

                }
            });
        }

        // 使用ImageLoader加载数据
        ImageLoader.getInstance().loadImage(info.getImageUrl(),
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                            Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);

                        fHolder.image.setImageBitmap(loadedImage);
                    }
                });

        return convertView;
    }

    private static class Holder {
        ImageView image;
    }

}
