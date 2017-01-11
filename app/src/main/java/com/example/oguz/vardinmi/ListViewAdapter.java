package com.example.oguz.vardinmi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<PersonInfo> {

    private OnClickListener clickListener;
    private String btnText;

    public ListViewAdapter(Context context, List<PersonInfo> items,OnClickListener onClickListener,String btnText) {
        super(context, R.layout.contacts_list_item, items);
        this.clickListener = onClickListener;
        this.btnText = btnText;
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder viewHolder;
        if(view == null){


            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.contacts_list_item, parent,false);

            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView)view.findViewById(R.id.txtName);
            viewHolder.txtNumber = (TextView)view.findViewById(R.id.txtNumber);
           // viewHolder.txtUsing = (TextView)view.findViewById(R.id.txtUsing);
            viewHolder.btnSendNotification = (Button)view.findViewById(R.id.btnSendNotification);
            view.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        final PersonInfo personInfo = getItem(position);


        setClickListeners(viewHolder.btnSendNotification);
        setTagsToViews(viewHolder.btnSendNotification,position);

        viewHolder.txtName.setText(personInfo.getName());
        viewHolder.txtNumber.setText(personInfo.getPhoneNumber());
        // viewHolder.txtUsing.setText(personInfo.isUsing()?"using loginApp!":"");
        viewHolder.btnSendNotification.setVisibility(personInfo.isUsing()?View.VISIBLE:View.INVISIBLE);
        viewHolder.btnSendNotification.setText(btnText);



        return view;
    }

    private void setTagsToViews(View view, int position) {
        view.setTag(R.id.key_position, position);
    }

    private void setClickListeners(View view) {
        view.setOnClickListener(clickListener);
    }


    private static class ViewHolder {
        TextView txtName;
        TextView txtNumber;
        TextView txtUsing;
        Button btnSendNotification;
    }
}