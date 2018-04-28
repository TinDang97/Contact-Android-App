package vn.intothewild.contact;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class full_info extends AppCompatActivity {

    String idContact = "";
    String name = "";
    List<String> phone = new ArrayList<>();
    List<Integer> phoneType = new ArrayList<>();
    List<String> email = new ArrayList<>();
    List<Integer> emailType = new ArrayList<>();
    String number = "";
    List<String> when = new ArrayList<>();
    List<String> time = new ArrayList<>();


    Handler handler;

    recyclerAdapterPhone mRecyclerAdapterPhone = new recyclerAdapterPhone(phone, phoneType) {
        @Override
        void onClickCall(Intent intent) {
            startActivity(intent);
        }

        @Override
        void onClickSMS(Intent intent) {
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Log.i("SMS", ex.toString());
                Toast.makeText(full_info.this,
                        "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        boolean onViewLongClick(View view, final int position) {
            final PopupMenu popupMenu = new PopupMenu(full_info.this, view);
            popupMenu.setGravity(Gravity.END);
            popupMenu.inflate(R.menu.contact_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menuItem_remove) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(full_info.this);
                        builder.setTitle("Are you sure?")
                                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                                                        new String[]{idContact, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, phone.get(position)})
                                                .build());
                                        try {
                                            if (ops.size() > 0) {
                                                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                                retrieveData();
                                                sendBroadcast(new Intent("update"));

                                            }
                                        } catch (RemoteException | OperationApplicationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        ;
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF006FFF"));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF006FFF"));
                        return true;
                    }
                    return false;
                }
            });
            return false;
        }
    };

    recyclerAdapterEmail mRecyclerAdapterEmail = new recyclerAdapterEmail(email, emailType) {
        @Override
        void onClickSendEmail(Intent intent) {
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(full_info.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        boolean onViewLongClick(View view, final int position) {
            final PopupMenu popupMenu = new PopupMenu(full_info.this, view);
            popupMenu.setGravity(Gravity.END);
            popupMenu.inflate(R.menu.contact_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menuItem_remove) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(full_info.this);
                        builder.setTitle("Are you sure?")
                                .setPositiveButton(Html.fromHtml("<font color='#f17204'>Sure</font>"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + "=?",
                                                        new String[]{idContact, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, email.get(position)})
                                                .build());
                                        try {
                                            if (ops.size() > 0) {
                                                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                                retrieveData();
                                                sendBroadcast(new Intent("update"));

                                            }
                                        } catch (RemoteException | OperationApplicationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        ;
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF006FFF"));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF006FFF"));
                        return true;
                    }
                    return false;
                }
            });
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RecyclerView lstPhones = (RecyclerView) findViewById(R.id.lst_phone_full);
        RecyclerView lstEmails = (RecyclerView) findViewById(R.id.lst_email_full);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageView avatar = (ImageView) findViewById(R.id.avatar_full);
        TextView tvPhone = (TextView) findViewById(R.id.tv_phone);
        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        TextView tvHistory = (TextView) findViewById(R.id.tv_history);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        handler = new Handler();
        //getID
        idContact = getIntent().getStringExtra("id");
        if (idContact != null) {
            //set Bitmap
            Bitmap imgAvatar = BitmapFactory.decodeStream(ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(idContact)), true));


            if (imgAvatar != null)
                avatar.setImageBitmap(imgAvatar);

            Cursor cursorName = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=?",
                    new String[]{idContact}, null);
            if(cursorName != null){
                if(cursorName.moveToFirst()) {
                    name = cursorName.getString(cursorName.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    if (actionBar != null && name != null) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setTitle(name);
                    }
                    cursorName.close();
                }
            }
            lstPhones.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            lstPhones.setHasFixedSize(true);
            lstPhones.setAdapter(mRecyclerAdapterPhone);


            lstEmails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            lstEmails.setHasFixedSize(true);

            lstEmails.setAdapter(mRecyclerAdapterEmail);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(full_info.this, addNewContact.class);
                    intent.setAction("edit");
                    intent.putExtra("id", idContact);
                    startActivityForResult(intent, 123);
                }
            });

            tvHistory.setVisibility(View.GONE);
            retrieveData();

            LinearLayout lstHistory = (LinearLayout) findViewById(R.id.lstHistory);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Cursor callLog = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.CACHED_NAME + "=?", new String[]{name}, CallLog.Calls.DATE + " DESC");
            if (callLog != null) {
                while(callLog.moveToNext()){
                    lstHistory.addView(addViewNew(LayoutInflater.from(this).inflate(R.layout.layout_dataline_history, null, false), callLog));
                }
                callLog.close();
            }

        }else {

            number = getIntent().getStringExtra("phone");

            if (number != null) {
                tvPhone.setVisibility(View.GONE);
                tvEmail.setVisibility(View.GONE);
                LinearLayout lstHistory = (LinearLayout) findViewById(R.id.lstHistory);

                View detail = LayoutInflater.from(this).inflate(R.layout.layout_item_phone_full, null, false);
                ((TextView) detail.findViewById(R.id.item_phone_full)).setText(number);
                detail.findViewById(R.id.contact_imgbtn_call_full).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + number));

                        if (ActivityCompat.checkSelfPermission(full_info.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(callIntent);
                    }
                });
                detail.findViewById(R.id.contact_imgbtn_message_full).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Log.i("SMS", ex.toString());
                            Toast.makeText(full_info.this,
                                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                lstHistory.addView(detail);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Cursor callLog = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + "=?", new String[]{number}, CallLog.Calls.DATE + " DESC");
                if (callLog != null) {
                    int count = 10;
                    while (callLog.moveToNext() && count > 0) {
                        lstHistory.addView(addViewNew(LayoutInflater.from(this).inflate(R.layout.layout_dataline_history, null, false), callLog));
                        count--;
                    }
                    callLog.close();
                }

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(full_info.this, addNewContact.class);
                        intent.putExtra("phone", number);
                        startActivity(intent);
                    }
                });

            } else {
                finish();
            }
        }
    }

    public View addViewNew(View view, Cursor cursor){
        ImageView typeImg = (ImageView) view.findViewById(R.id.call_log_IMV);
        TextView tv_call_number = (TextView) view.findViewById(R.id.tv_show_calllog1);
        TextView tv_call_date = (TextView) view.findViewById(R.id.tv_show_calllog2);
        TextView tv_call_duration = (TextView) view.findViewById(R.id.tv_duration);
        ImageButton tv_btn_more = (ImageButton) view.findViewById(R.id.btn_more_callLog);
        tv_btn_more.setVisibility(View.GONE);

        int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
        tv_call_duration.setText(String.format(Locale.ENGLISH, "%2d:%2ds", duration/60, duration%60));

        switch (cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))){
            case CallLog.Calls.INCOMING_TYPE:
                typeImg.setImageResource(R.drawable.incoming_call);
                if(duration == 0) tv_call_duration.setText("Denied");
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                typeImg.setImageResource(R.drawable.outcoming_call);
                if(duration == 0) tv_call_duration.setText("Not token");
                break;
            case CallLog.Calls.MISSED_TYPE:
                typeImg.setImageResource(R.drawable.miss_call);
                tv_call_duration.setText("");
                break;
            default:
                break;
        }

        tv_call_number.setText(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
        tv_call_date.setText(String.valueOf(new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)))).split("GMT")[0]);


        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 123 && resultCode == RESULT_OK){
            retrieveData();
        }
    }


    private void retrieveData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                phone.clear();
                phoneType.clear();
                //READ PHONE
                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{idContact}, null);

                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!phone.contains(number)) {
                            phone.add(number);
                            phoneType.add(phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                        }
                    }
                    phoneCursor.close();
                }

                //READ Email
                email.clear();
                emailType.clear();
                Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{idContact}, null);

                if (emailCursor != null) {
                    while (emailCursor.moveToNext()) {
                        String address = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        if(!email.contains(address)) {
                            email.add(address);
                            emailType.add(emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)));
                        }
                    }
                    emailCursor.close();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerAdapterEmail.notifyDataSetChanged();
                        mRecyclerAdapterPhone.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
