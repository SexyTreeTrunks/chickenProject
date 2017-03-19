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
    private ArrayList<DiningInfoListViewItem> listViewItemList = new ArrayList<DiningInfoListViewItem>();

    ListViewAdapter(ArrayList<DiningInfoListViewItem> listViewItemList) {
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

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name) ;
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        TextView distanceTextView = (TextView) convertView.findViewById(R.id.distanceView);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        DiningInfoListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        ratingBar.setRating(listViewItem.getRatingStar());
        nameTextView.setText(listViewItem.getName());
        distanceTextView.setText(Float.toString(listViewItem.getDistance()));

        return convertView;
    }
}
