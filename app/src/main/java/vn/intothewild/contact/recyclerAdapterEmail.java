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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tin on 5/8/2017.
 */

public abstract class recyclerAdapterEmail extends RecyclerView.Adapter<recyclerAdapterEmail.holderEmail>{
    private List<String> emails = new ArrayList<>();
    private List<Integer> emailTypes = new ArrayList<>();

    public recyclerAdapterEmail(List<String> emails, List<Integer> emailTypes) {
        if(emails != null) {
            this.emails = emails;
            this.emailTypes = emailTypes;
        }
    }

    @Override
    public recyclerAdapterEmail.holderEmail onCreateViewHolder(ViewGroup parent, int viewType) {
        return new holderEmail(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lst_email_full, parent, false));
    }

    @Override
    public void onBindViewHolder(recyclerAdapterEmail.holderEmail holder, final int position) {
        holder.email.setText(emails.get(position));
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+emails.get(position)));
                emailIntent.putExtra(Intent.EXTRA_CC, "");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                onClickSendEmail(emailIntent);
            }
        });

        if (position < emailTypes.size()) {
            Integer type = emailTypes.get(position);
            holder.type.setText(ContactsContract.CommonDataKinds.Email.getTypeLabel(Resources.getSystem(), type, "").toString());
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+emails.get(position)));
                emailIntent.putExtra(Intent.EXTRA_CC, "");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                onClickSendEmail(emailIntent);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onViewLongClick(v, position);
            }
        });
    }

    abstract void onClickSendEmail(Intent intent);
    abstract boolean onViewLongClick(View view, int position);

    @Override
    public int getItemCount() {
        return emails.size();
    }

    class holderEmail extends RecyclerView.ViewHolder{
        TextView email;
        ImageButton send;
        TextView type;
        View view;
        holderEmail(View itemView) {
            super(itemView);
            view = itemView;
            email = (TextView) itemView.findViewById(R.id.email_full);
            send = (ImageButton) itemView.findViewById(R.id.sendEmail);
            type = (TextView) itemView.findViewById(R.id.emailType);
        }
    }
}
