package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.EditInsureActivity;
import com.icalinks.obd.vo.ServiceType;


public class InsureTypeAdapter extends BaseAdapter{
    private static final String TAG = InsureTypeAdapter.class.getSimpleName();

    private Context mContext;
    
    private List<EditInsureActivity.InsureTypeInfo> mDataList;
    private LayoutInflater mInflater;
    
   
    public final class ViewHolder{
    	TextView typeName;
        Button checkbox;
        View itemView;
    }

    public InsureTypeAdapter(Context context){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<EditInsureActivity.InsureTypeInfo> dataList) {
        
        this.mDataList = dataList;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public int getIndex(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder holder = null;
        final EditInsureActivity.InsureTypeInfo insureType = mDataList.get(position);
        if(convertView == null){
        	holder = new ViewHolder();
        	convertView = mInflater.inflate(R.layout.info_type_item, null);
        	holder.itemView = convertView;//.findViewById(R.id.info_item_view);
        	holder.typeName = (TextView) convertView.findViewById(R.id.info_type_name);
        	holder.checkbox = (Button) convertView.findViewById(R.id.info_checkbox);
        	convertView.setTag(holder);
        }else {
        	holder = (ViewHolder)convertView.getTag();
		}
        holder.typeName.setText(insureType.getTypeName());
        holder.checkbox.setSelected(insureType.isSelected());
        
        final ViewHolder fHolder = holder;
        fHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int eAction = event.getAction();
				if (eAction == MotionEvent.ACTION_DOWN) {
					
				}
				fHolder.checkbox.setPressed(true);
				fHolder.itemView.setPressed(true);
				if (eAction == MotionEvent.ACTION_UP || eAction == MotionEvent.ACTION_CANCEL) {
					fHolder.checkbox.setPressed(false);
				}
				return false;
			}
		});
        
        fHolder.itemView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fHolder.checkbox.setPressed(false);
				fHolder.checkbox.setSelected(!insureType.isSelected());
				insureType.setSelected(!insureType.isSelected());
			}
		});
        return convertView;
    }

}