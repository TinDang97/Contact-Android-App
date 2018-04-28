package vn.intothewild.contact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.CursorAdapter;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Trung Kieu Photo on 5/10/2017.
 */

public class callLogAdapter extends CursorAdapter {

    private int Count = 20;

    public callLogAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.layout_dataline_history, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        final TextView name = (TextView) view.findViewById(R.id.tv_show_calllog1);
        TextView time = (TextView) view.findViewById(R.id.tv_show_calllog2);
        TextView tv_duration = (TextView) view.findViewById(R.id.tv_duration);

        ImageView imgType = (ImageView) view.findViewById(R.id.call_log_IMV);
        ImageButton imgButton = (ImageButton) view.findViewById(R.id.btn_more_callLog);

        final List<ContactInfo> currentData = new ArrayList<>();
        currentData.addAll(FragmentDial.data);

        String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
        final String number2String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        final String Name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

        String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
        String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
        Date dateSetting = new Date(Long.valueOf(date));

        time.setText(String.valueOf(dateSetting).split("GMT")[0]);
        if (Integer.parseInt(type) != CallLog.Calls.MISSED_TYPE)
            if(Integer.parseInt(type) != CallLog.Calls.OUTGOING_TYPE && Integer.parseInt(duration) != 0)
                tv_duration.setText(duration+" Secs");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencall = new Intent(Intent.ACTION_CALL);
                intencall.setData(Uri.parse("tel:" + number2String));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intencall);
            }
        });


        switch (Integer.parseInt(type)){
            case CallLog.Calls.INCOMING_TYPE:
                imgType.setImageResource(R.drawable.incoming_call);
                if(Integer.parseInt(duration) == 0) tv_duration.setText("Denied");
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                imgType.setImageResource(R.drawable.outcoming_call);
                if(Integer.parseInt(duration) == 0) tv_duration.setText("Not token");
                break;
            case CallLog.Calls.MISSED_TYPE:
                    imgType.setImageResource(R.drawable.miss_call);
                    tv_duration.setText("");
                break;
            default:
                break;
        }

        if(Name ==  null){
            name.setText(number2String);
        }
        else { name.setText(Name); }

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number2String != null){
                    ContactInfo ci = findContact(number2String, currentData);
                    Intent intent = new Intent(context, full_info.class);
                    if(ci != null) {
                        intent.putExtra("id", ci.getId());
                    }
                    else
                        intent.putExtra("phone", number2String);
                    context.startActivity(intent);
                }
            }
        });

    }
    private ContactInfo findContact(String phone,List<ContactInfo> lstContact){
        for(ContactInfo ci : lstContact){
            for(String number : ci.getPhone())
                if (number.replaceAll("\\s+", "").equals(phone.replaceAll("\\s+", "")))
                    return ci;
        }
        return null;
    }
}
