package com.murmuler.organicstack.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.murmuler.organicstack.DetailActivity;
import com.murmuler.organicstack.MainActivity;
import com.murmuler.organicstack.R;
import com.murmuler.organicstack.vo.RoomSummaryViewVO;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class RoomSummaryViewAdapter extends BaseAdapter {
    Context context;
    List<RoomSummaryViewVO> roomList;
    LayoutInflater layoutInflater;
    Bitmap bitmap = null;

    public RoomSummaryViewAdapter(Context context, List<RoomSummaryViewVO> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout = convertView;
        if(itemLayout == null)
            itemLayout = layoutInflater.inflate(R.layout.room_detail_list_item, parent, false);

        RoomSummaryViewVO room = roomList.get(position);
        ImageView imageView = itemLayout.findViewById(R.id.imageView);

        Thread mThread = new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    URL url = new URL("http://www.murmul-er.com/manage/download?middlePath=/room/roomId_" + room.getRoomId() + "&imageFileName=" + room.getRoomImg());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        mThread.start();

        try {
            mThread.join();
            imageView.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TextView title = itemLayout.findViewById(R.id.title);
        String titleText = room.getTitle();
        if(titleText.length() >= 14) titleText = titleText.substring(0,12) + "...";
        title.setText(titleText);

        ImageButton likeButton = itemLayout.findViewById(R.id.likeButton);
        likeButton.setBackgroundColor(Color.WHITE);
        likeButton.setImageResource(R.drawable.heart_custom);

        TextView address = itemLayout.findViewById(R.id.address);
        String addressText = room.getSido() + " " + room.getSigungu() + " " + room.getRoadname();
        address.setText(addressText);

        TextView detail = itemLayout.findViewById(R.id.detail);
        String cost = room.getMonthlyCost() == 0 ? "전세" : "월세 " + room.getMonthlyCost();
        String manageCost = room.getManageCost() == 0 ? "없음" : "" + room.getManageCost();
        detail.setText("[" + room.getRentType() + "] "
                + "보증금 " + room.getDeposit() + " / " + cost + "\n"
                + room.getRoomType() + " " + room.getArea() + " 관리비 " + manageCost
        );
        itemLayout.setMinimumHeight(450);
        itemLayout.setId(room.getRoomId());
//        Log.i("room set id", itemLayout.getId()+"");
//        itemLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("아이템 클릭",v.getId()+"");
//            }
//        });
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("아이템 클릭", "roomId("+roomList.get(position).getRoomId()+")");
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("roomId", v.getId()+"");
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        return itemLayout;
    }
}