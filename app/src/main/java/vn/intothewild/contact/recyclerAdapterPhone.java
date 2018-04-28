package vn.intothewild.contact;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Tin on 5/8/2017.
 */

public abstract class recyclerAdapterPhone extends RecyclerView.Adapter<recyclerAdapterPhone.viewHolderPhone> {
    private List<String> phones = new ArrayList<>();
    private List<Integer> phoneTypes = new ArrayList<>();

    public recyclerAdapterPhone(List<String> phones, List<Integer> phoneTypes) {
        this.phones = phones;
        this.phoneTypes = phoneTypes;
    }

    @Override
    public recyclerAdapterPhone.viewHolderPhone onCreateViewHolder(ViewGroup parent, int viewType) {
           return new recyclerAdapterPhone.viewHolderPhone(LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.layout_item_phone_full, parent, false));
    }

    @Override
    public void onBindViewHolder(recyclerAdapterPhone.viewHolderPhone holder, final int position) {
        holder.phone.setText(phones.get(position));
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phones.get(position)));
                onClickCall(callIntent);
            }
        });

        holder.sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+phones.get(position)));
                onClickSMS(smsIntent);
            }
        });
        if (position < phoneTypes.size()) {
            Integer type = phoneTypes.get(position);
            holder.type.setText(ContactsContract.CommonDataKinds.Phone.getTypeLabel(Resources.getSystem(), type, "").toString());
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phones.get(position)));
                onClickCall(callIntent);
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onViewLongClick(v, position);
            }
        });
    }

    abstract void onClickCall(Intent intent);
    abstract void onClickSMS(Intent intent);
    abstract boolean onViewLongClick(View view, int position);

    @Override
    public int getItemCount() {
        return phones.size();
    }

    class viewHolderPhone extends RecyclerView.ViewHolder{
        TextView phone;
        ImageButton call;
        ImageButton sms;
        TextView type;
        View view;

        viewHolderPhone(View itemView) {
            super(itemView);
            view = itemView;
            phone = (TextView) itemView.findViewById(R.id.item_phone_full);
            call  = (ImageButton) itemView.findViewById(R.id.contact_imgbtn_call_full);
            sms = (ImageButton) itemView.findViewById(R.id.contact_imgbtn_message_full);
            type = (TextView) itemView.findViewById(R.id.phoneType);
        }
    }
}
