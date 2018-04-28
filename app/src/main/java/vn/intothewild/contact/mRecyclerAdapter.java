package vn.intothewild.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tin on 5/6/2017.
 */

abstract class mRecyclerAdapter extends RecyclerView.Adapter<mRecyclerAdapter.mRecyclerHolderContact> {

    private List<ContactInfo> lstContact = new ArrayList<>();
    private List<View> lstView = new ArrayList<>();

    mRecyclerAdapter(List<ContactInfo> lstContact) {
        this.lstContact = lstContact;
    }

    @Override
    public mRecyclerAdapter.mRecyclerHolderContact onCreateViewHolder(ViewGroup parent, int viewType) {
        return new mRecyclerAdapter.mRecyclerHolderContact(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final mRecyclerAdapter.mRecyclerHolderContact holder, final int position) {
        TextView textView = (TextView) holder.view.findViewById(R.id.tv_alphabet);
        char alpha = lstContact.get(position).getName().toUpperCase().charAt(0);
        char beta = '#';
        alpha = convert(alpha);

        if (!String.valueOf(alpha).matches("[a-zA-Z]"))
            alpha = '#';

        if(position > 0) {
            beta = lstContact.get(position - 1).getName().toUpperCase().charAt(0);
            beta = convert(beta);
            if (!String.valueOf(beta).matches("[a-zA-Z]"))
                beta = '#';
        }

        if (position == 0) {
            textView.setText(String.valueOf(alpha));
            textView.setVisibility(View.VISIBLE);
        } else if (alpha != beta) {

            textView.setText(String.valueOf(alpha));
            textView.setVisibility(View.VISIBLE);
        }
        else
            textView.setVisibility(View.GONE);
        final ContactInfo contactInfo = lstContact.get(position);
        holder.name.setText(contactInfo.getName());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+contactInfo.getPhone().get(0)));
                onClickCall(callIntent);
            }
        });

        holder.sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+contactInfo.getPhone().get(0)));
                onClickSMS(smsIntent);
            }
        });

        if(contactInfo.getPhone().size() > 0)
            holder.phone.setText(contactInfo.getPhone().get(0));
        else
            holder.phone.setText(String.valueOf("empty"));
        if(contactInfo.getAvatar() != null) {
            holder.avatar.setImageBitmap(contactInfo.getAvatar());
        }
        else holder.avatar.setImageResource(R.drawable.person_icon);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < lstView.size(); i++) {
                    if (i != position &&
                            lstView.get(i).findViewById(R.id.contact_item_phone_number).getTranslationX() != 0f)
                        closeAnimation(lstView.get(i));
                }

                if(holder.phone.getTranslationX() == 0f)
                    openAnimation(holder.view);

                else
                    closeAnimation(holder.view);
            }
        });

        holder.view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for(int i = 0; i < lstView.size(); i++){
                    if(lstView.get(i).findViewById(R.id.contact_item_phone_number).getTranslationX() != 0f)
                        closeAnimation(lstView.get(i));
                }
                return false;
            }
        });
        if(!lstView.contains(holder.view)) {
            lstView.add(holder.view);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoContactOnClick(position);
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return infoContactLongOnClick(position, v);
            }
        });
    }

    public abstract void infoContactOnClick(int position);
    public abstract boolean infoContactLongOnClick(int position, View view);
    public abstract void onClickCall(Intent intent);
    public abstract void onClickSMS(Intent intent);

    private void openAnimation(View v){
        View ct_item_phone_number = v.findViewById(R.id.contact_item_phone_number);
        View ct_mess = v.findViewById(R.id.contact_imgbtn_message);
        View ct_tv_call = v.findViewById(R.id.contact_tv_call);
        View ct_tv_mess = v.findViewById(R.id.contact_tv_mess);
        View ct_call = v.findViewById(R.id.contact_imgbtn_call);
        View name = v.findViewById(R.id.contact_item_name);

        name.setPadding(0,0, (int) contactActivity.displayMetrics.xdpi,0);
        ct_item_phone_number.animate().translationX(-contactActivity.displayMetrics.xdpi);
        ct_call.setVisibility(View.VISIBLE);
        ct_call.setRotation(0f);
        ct_call.setAlpha(0f);
        ct_call.animate().alpha(1f).setDuration(1000).rotation(360f);
        ct_mess.setVisibility(View.VISIBLE);
        ct_mess.setRotation(0f);
        ct_mess.setAlpha(0f);
        ct_mess.animate().alpha(1f).setDuration(1000).rotation(360f);
        ct_tv_call.setVisibility(View.VISIBLE);
        ct_tv_call.setAlpha(0f);
        ct_tv_call.animate().alpha(1f).setDuration(1000);
        ct_tv_mess.setVisibility(View.VISIBLE);
        ct_tv_mess.setAlpha(0f);
        ct_tv_mess.animate().alpha(1f).setDuration(1000);
    }

    private void closeAnimation(View v){
        View ct_item_phone_number = v.findViewById(R.id.contact_item_phone_number);
        View ct_mess = v.findViewById(R.id.contact_imgbtn_message);
        View ct_tv_call = v.findViewById(R.id.contact_tv_call);
        View ct_tv_mess = v.findViewById(R.id.contact_tv_mess);
        View ct_call = v.findViewById(R.id.contact_imgbtn_call);
        View name = v.findViewById(R.id.contact_item_name);
        ct_item_phone_number.animate().translationX(0);
        ct_call.animate().alpha(0f).setDuration(1000);
        ct_call.setVisibility(View.GONE);
        ct_mess.animate().alpha(0f).setDuration(1000);
        ct_mess.setVisibility(View.GONE);
        ct_tv_call.animate().alpha(0f).setDuration(1000);
        ct_tv_call.setVisibility(View.GONE);
        ct_tv_mess.animate().alpha(0f).setDuration(1000);
        ct_tv_mess.setVisibility(View.GONE);

        name.setPadding(0,0,0,0);
    }

    @Override
    public int getItemCount() {
        return lstContact.size();
    }

    class mRecyclerHolderContact extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView phone;
        private ImageView avatar;
        private View view;
        private ImageButton call;
        private ImageButton sms;

        mRecyclerHolderContact(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.contact_item_name);
            phone = (TextView) itemView.findViewById(R.id.contact_item_phone_number);
            avatar = (ImageView) itemView.findViewById(R.id.img_contact);
            call = (ImageButton) itemView.findViewById(R.id.contact_imgbtn_call);
            sms = (ImageButton) itemView.findViewById(R.id.contact_imgbtn_message);
        }
    }

    char convert(char c){
        switch (c){
            case 'Đ':
                c = 'D';
                break;
            case 'Ư':
            case 'Ú':
            case 'Ù':
            case 'Ụ':
            case 'Ủ':
            case 'Ũ':
            case 'Ứ':
            case 'Ừ':
            case 'Ự':
            case 'Ử':
            case 'Ữ':
                c = 'U';
                break;
            case 'Ọ':
            case 'Ỏ':
            case 'Õ':
            case 'Ò':
            case 'Ó':
            case 'Ô':
            case 'Ố':
            case 'Ồ':
            case 'Ộ':
            case 'Ổ':
            case 'Ỗ':
            case 'Ớ':
            case 'Ờ':
            case 'Ợ':
            case 'Ở':
            case 'Ỡ':
            case 'Ơ':
                c = 'O';
                break;
            case 'Ă':
            case 'Ắ':
            case 'Ằ':
            case 'Ẵ':
            case 'Ẳ':
            case 'Ặ':
            case 'Â':
            case 'Ậ':
            case 'Ẩ':
            case 'Ẫ':
            case 'Ấ':
            case 'Ầ':
            case 'Ạ':
            case 'Ả':
            case 'Ã':
            case 'Á':
            case 'À':
                c = 'A';
                break;
            case 'Ê':
            case 'Ế':
            case 'Ề':
            case 'Ệ':
            case 'Ễ':
            case 'Ể':
            case 'É':
            case 'È':
            case 'Ẽ':
            case 'Ẹ':
            case 'Ẻ':
                c = 'E';
                break;
            default:
                break;
        }
        return c;
    }
}
