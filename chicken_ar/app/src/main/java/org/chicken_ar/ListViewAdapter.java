package org.chicken_ar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chicken on 2017-03-19.
 */

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<?> listViewItemList;
    private boolean isDiningInfoType;

    public void initAdapterToDiningInfo(ArrayList<DiningInfoListViewItem> listViewItemList) {
        isDiningInfoType = true;
        this.listViewItemList = listViewItemList;
    }

    public void initAdapterToReviewInfo(ArrayList<ReviewInfoListViewItem> listViewItemList) {
        isDiningInfoType = false;
        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        if(isDiningInfoType == true) {
            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_dining, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView nameTextView = (TextView) convertView.findViewById(R.id.name);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            TextView distanceTextView = (TextView) convertView.findViewById(R.id.distanceView);

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            DiningInfoListViewItem listViewItem = (DiningInfoListViewItem) listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            ratingBar.setRating(listViewItem.getRatingStar());
            nameTextView.setText(listViewItem.getName());
            distanceTextView.setText(Integer.toString(listViewItem.getDistance()) + "m");

            return convertView;

        } else {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_review, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView reviewIdTextView = (TextView) convertView.findViewById(R.id.textViewReviewId);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBarFromReview);
            TextView contentsTextView = (TextView) convertView.findViewById(R.id.textViewReviewContents);

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ReviewInfoListViewItem listViewItem = (ReviewInfoListViewItem) listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            ratingBar.setRating(listViewItem.getRatingStars());
            reviewIdTextView.setText(listViewItem.getUserId());
            contentsTextView.setText(listViewItem.getContents());

            return convertView;
        }
    }
}
