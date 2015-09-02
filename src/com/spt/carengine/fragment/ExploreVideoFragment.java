
package com.spt.carengine.fragment;

import com.spt.carengine.view.explore.navigate.NavigateView.ExploreType;

/**
 * @author Rocky
 * @Time 2015年8月7日 下午4:36:35
 * @description 浏览视频的Fragment
 */
public class ExploreVideoFragment extends BaseExploreFileFragment {

    @Override
    protected ExploreType getExploreType() {
        return ExploreType.Video;
    }
}
