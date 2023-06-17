package com.hansung.mini_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    public ArrayList<String> stringArrayList;
    public List<Movie> target_movie_list;



    //===== [Click 이벤트 구현을 위해 추가된 코드] ==========================
    // OnItemClickListener 인터페이스 선언
    public interface OnItemClickListener {
        void onItemClicked(Movie data);
    }

    // OnItemClickListener 참조 변수 선언
    private OnItemClickListener itemClickListener;

    // OnItemClickListener 전달 메소드
    public void setOnItemClickListener (OnItemClickListener listener) {
        itemClickListener = listener;
    }
    //======================================================================


    //===== 뷰홀더 클래스 =====================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public TextView getTextView() {

            return textView;
        }
    }
    //========================================================================


    //----- 생성자 --------------------------------------
    // 생성자를 통해서 데이터를 전달받도록 함

    public CustomAdapter (ArrayList<String> stringArrayList_parameter) {
        stringArrayList = stringArrayList_parameter;
    }

    public CustomAdapter (ArrayList<String> stringArrayList_parameter, List<Movie> target_movie_list_parameter) {
        stringArrayList = stringArrayList_parameter;
        target_movie_list = target_movie_list_parameter;
    }
    //--------------------------------------------------


    @NonNull
    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        CustomAdapter.ViewHolder viewHolder = new CustomAdapter.ViewHolder(view);


        //===== [Click 이벤트 구현을 위해 추가된 코드] =====================
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie data = null;
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    data = target_movie_list.get(position);
                }
                itemClickListener.onItemClicked(data);
            }
        });
        //==================================================================


        return viewHolder;
    }


    @Override   // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        String s = stringArrayList.get(position);
        holder.textView.setText(s);
    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return stringArrayList.size();
    }

    public void setTarget_movie_list(List<Movie> target_movie_list) {
        this.target_movie_list = target_movie_list;
    }
}