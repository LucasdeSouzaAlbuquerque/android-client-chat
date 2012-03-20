package com.chatmessenger.android.gmail;

import android.widget.ArrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import com.chatmessenger.android.R;

public class ContactArrayAdapter extends ArrayAdapter<Contact>
{
	private int resource;

	public ContactArrayAdapter(Context paramContext, int paramInt, List<Contact> paramList)
	{
		super(paramContext, paramInt, paramList);
		this.resource = paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		Contact localContact = (Contact)getItem(paramInt);
		LinearLayout localLinearLayout;
		localLinearLayout = new LinearLayout(getContext());
		((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.resource, localLinearLayout, true);
		TextView localTextView1 = (TextView)localLinearLayout.findViewById(R.id.txtName);
		TextView localTextView2 = (TextView)localLinearLayout.findViewById(R.id.txtStatus);
		ImageView localImageView = (ImageView)localLinearLayout.findViewById(R.id.imgContact);
		localTextView1.setText(localContact.getName());
		localTextView2.setText(localContact.getStatus());
		localImageView.setImageDrawable(localContact.getImage());
		return localLinearLayout;






	}
}
