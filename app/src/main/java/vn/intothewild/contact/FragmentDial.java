package vn.intothewild.contact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tin on 4/16/2017.
 */

public class FragmentDial extends Fragment implements  View.OnClickListener{
    private static final int MY_PERMISSIONS_REQUEST_READ_CALLLOG = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    ImageButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn_thang, btn_sao, btn_call
            , btn_messenger, btn_add_contact,btn_delete, moreButton;

    LinearLayout show_layout,last_call_layout;
    ImageView imgType;
    Cursor cursor;
    TextView time,tv_duration,name;
    boolean lastCall = true;

    private mRecyclerAdapter recyclerAdapter;
    EditText edt_dial_input, tv_dial_show, input_Number_ToSent, input_Message;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private String numString = null;
    public static List<ContactInfo> data = new ArrayList<>();
    List<ContactInfo> current_data = new ArrayList<>();

    public FragmentDial(List<ContactInfo> contactInfos){
        data.clear();
        data.addAll(contactInfos);
    }

    public void notifyData(List<ContactInfo> contactInfos){
        data.clear();
        current_data.clear();
        data.addAll(contactInfos);
        current_data.addAll(data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.layout_dial, container,false);
        connectView(view);
        return view;
    }

    public void connectView(final View view){
        btn0 = (ImageButton) view.findViewById(R.id.Button0);
        btn1 = (ImageButton) view.findViewById(R.id.Button1);
        btn2 = (ImageButton) view.findViewById(R.id.Button2);
        btn3 = (ImageButton) view.findViewById(R.id.Button3);
        btn4 = (ImageButton) view.findViewById(R.id.Button4);
        btn5 = (ImageButton) view.findViewById(R.id.Button5);
        btn6 = (ImageButton) view.findViewById(R.id.Button6);
        btn7 = (ImageButton) view.findViewById(R.id.Button7);
        btn8 = (ImageButton) view.findViewById(R.id.Button8);
        btn9 = (ImageButton) view.findViewById(R.id.Button9);
        btn_sao = (ImageButton) view.findViewById(R.id.Button_sao);
        btn_thang = (ImageButton) view.findViewById(R.id.Button_thang);
        btn_call = (ImageButton) view.findViewById(R.id.Button_call);
        btn_messenger = (ImageButton) view.findViewById(R.id.Button_message);
        btn_add_contact = (ImageButton) view.findViewById(R.id.Button_add_contact);
        btn_delete = (ImageButton) view.findViewById(R.id.button_delete);

        show_layout = (LinearLayout) view.findViewById(R.id.show_layout);
        last_call_layout = (LinearLayout) view.findViewById(R.id.last_Call);

        name = (TextView) view.findViewById(R.id.tv_show_calllog11);
        time = (TextView) view.findViewById(R.id.tv_show_calllog21);
        tv_duration = (TextView) view.findViewById(R.id.tv_duration1);

        imgType = (ImageView) view.findViewById(R.id.call_log_IMV1);
        moreButton = (ImageButton) view.findViewById(R.id.btn_more_callLog1);

        recyclerView = (RecyclerView) view.findViewById(R.id.dial_recycler_contact);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn_sao.setOnClickListener(this);
        btn_thang.setOnClickListener(this);
        btn_add_contact.setOnClickListener(this);
        btn_call.setOnClickListener(this);
        btn_messenger.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        getLastCall();

        edt_dial_input = (EditText) view.findViewById(R.id.edit_dial_input);
        tv_dial_show = (EditText) view.findViewById(R.id.dial_show);
        tv_dial_show.setInputType(InputType.TYPE_NULL);
        tv_dial_show.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = tv_dial_show.getText().toString().toLowerCase();

                if(number.length() > 0){
                    btn_delete.setVisibility(View.VISIBLE);
                    show_layout.setVisibility(View.VISIBLE);
                    last_call_layout.setVisibility(View.GONE);
                    lastCall = false;
                }else{
                    btn_delete.setVisibility(View.GONE);
                    show_layout.setVisibility(View.GONE);
                    getLastCall();
                    last_call_layout.setVisibility(View.VISIBLE);
                    lastCall = true;
                }


                current_data.clear();
                if(number.length() == 0)
                    current_data.addAll(data);
                else
                    for(ContactInfo ci: data) {
                        if(ci.getPhone().size() > 1){
                            for(int i = 0; i < ci.getPhone().size();i++){
                                String current = ci.getPhone().get(i).replaceAll("\\s+", "").toLowerCase();
                                if (current.contains(number)){
                                    current_data.add(ci);
                                    break;
                                }
                            }
                        }else{
                            String current = ci.getPhone().get(0).replaceAll("\\s+", "").toLowerCase();
                            if (current.contains(number))
                                current_data.add(ci);
                        }
                    }
                recyclerView.setAdapter(newmRecyclerAdapter());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button0:
                if(lastCall)
                    numString = "";
                doClickButton(0);
                break;
            case R.id.Button1:
                if(lastCall)
                    numString = "";
                doClickButton(1);
                break;
            case R.id.Button2:
                if(lastCall)
                    numString = "";
                doClickButton(2);
                break;
            case R.id.Button3:
                if(lastCall)
                    numString = "";
                doClickButton(3);
                break;
            case R.id.Button4:
                if(lastCall)
                    numString = "";
                doClickButton(4);
                break;
            case R.id.Button5:
                if(lastCall)
                    numString = "";
                doClickButton(5);
                break;
            case R.id.Button6:
                if(lastCall)
                    numString = "";
                doClickButton(6);
                break;
            case R.id.Button7:
                if(lastCall)
                    numString = "";
                doClickButton(7);
                break;
            case R.id.Button8:
                if(lastCall)
                    numString = "";
                doClickButton(8);
                break;
            case R.id.Button9:
                if(lastCall)
                    numString = "";
                doClickButton(9);
                break;
            case R.id.Button_sao:
                if(lastCall)
                    numString = "";
                doClickButton(10);
                break;
            case R.id.Button_thang:
                if(lastCall)
                    numString = "";
                doClickButton(11);
                break;
            case R.id.Button_add_contact:
                doClickButton(12);
                break;
            case R.id.Button_call:
                doClickButton(13);
                break;
            case R.id.Button_message:
                doClickButton(14);
                break;
            case R.id.button_delete:
                doClickButton(-1);
                break;
            default:
                break;
        }
    }

    public void doClickButton(int i){
        switch (i){
            case 0:
                if(numString!= null)
                    numString = numString + "0";
                else
                    numString = "0";
                tv_dial_show.setText(numString);
                break;
            case 1:
                if(numString!= null)
                    numString = numString + "1";
                else
                    numString = "1";
                tv_dial_show.setText(numString);
                break;
            case 2:
                if(numString!= null)
                    numString = numString + "2";
                else
                    numString = "2";
                tv_dial_show.setText(numString);
                break;
            case 3:
                if(numString!= null)
                    numString = numString + "3";
                else
                    numString = "3";
                tv_dial_show.setText(numString);
                break;
            case 4:
                if(numString!= null)
                    numString = numString + "4";
                else
                    numString = "4";
                tv_dial_show.setText(numString);
                break;
            case 5:
                if(numString!= null)
                    numString = numString + "5";
                else
                    numString = "5";
                tv_dial_show.setText(numString);
                break;
            case 6:
                if(numString!= null)
                    numString = numString + "6";
                else
                    numString = "6";
                tv_dial_show.setText(numString);
                break;
            case 7:
                if(numString!= null)
                    numString = numString + "7";
                else
                    numString = "7";
                tv_dial_show.setText(numString);
                break;
            case 8:
                if(numString!= null)
                    numString = numString + "8";
                else
                    numString = "8";
                tv_dial_show.setText(numString);
                break;

            case 9:
                if(numString!= null)
                    numString = numString + "9";
                else
                    numString = "9";
                tv_dial_show.setText(numString);
                break;
            case 10:
                if(numString!= null)
                    numString = numString + "*";
                else
                    numString = "*";
                tv_dial_show.setText(numString);
                break;
            case 11:
                if(numString!= null)
                    numString = numString + "#";
                else
                    numString = "#";
                tv_dial_show.setText(numString);
                break;
            case 12:
                    addContact();
                break;

            case 13:
                if(numString!=null) doMakeCall();
                break;
            case 14:
                doWriteMessage(getView());
                break;
            case -1:
                if(numString!=null){
                    if(numString.length() > 1) {
                        numString = numString.substring(0,numString.length()-1);
                    }else
                        numString ="";
                    tv_dial_show.setText(numString);
                }
                break;
        }
    }

    public void doGetLastCall(){
        if(ActivityCompat.checkSelfPermission(FragmentDial.this.getContext(),
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FragmentDial.this.getActivity(),
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    MY_PERMISSIONS_REQUEST_READ_CALLLOG);
            return;
        }
        getLastCall();
    }

    public void doMakeCall()
    {
        if(ActivityCompat.checkSelfPermission(FragmentDial.this.getContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FragmentDial.this.getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }
        makeCall();
    }


    public void doWriteMessage(View v){

        if (ActivityCompat.checkSelfPermission(FragmentDial.this.getContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FragmentDial.this.getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
            return;
        }

        sentMessage();

    }

    private mRecyclerAdapter newmRecyclerAdapter(){
        return new mRecyclerAdapter(current_data) {
            @Override
            public void infoContactOnClick(int position) {
                Intent intent = new Intent(getContext(), full_info.class);
                intent.putExtra("name", current_data.get(position).getName());
                intent.putExtra("id", current_data.get(position).getId());
                startActivity(intent);
            }

            @Override
            public boolean infoContactLongOnClick(final int position, View view) {
               return false;
            }

            @Override
            public void onClickCall(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void onClickSMS(Intent intent) {
                startActivity(intent);
            }
        };
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (permissions[0] == Manifest.permission.SEND_SMS
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sentMessage();
                    Toast.makeText(FragmentDial.this.getContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    return;
                }
            }
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (permissions[0] == Manifest.permission.CALL_PHONE
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                }
            }
            case MY_PERMISSIONS_REQUEST_READ_CALLLOG:{
                if (permissions[0] == Manifest.permission.READ_CALL_LOG
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastCall();
                }
            }
        }

    }

    public void getLastCall(){
        Uri uri = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cursor = getActivity().getContentResolver().query(uri, null, null, null, CallLog.Calls.DATE +" DESC");

        if(cursor != null)
            if(cursor.getCount() != 0){
            cursor.moveToFirst();
            String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
            final String number2String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            numString = number2String;
            final String Name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));


            final String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

            String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
            Date dateSetting = new Date(Long.valueOf(date));

            time.setText(String.valueOf(dateSetting).split("GMT")[0]);
            if (Integer.parseInt(type) != CallLog.Calls.MISSED_TYPE)
                if (Integer.parseInt(type) != CallLog.Calls.OUTGOING_TYPE && Integer.parseInt(duration) != 0)
                    tv_duration.setText(duration + " Secs");

            last_call_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencall = new Intent(Intent.ACTION_CALL);
                    intencall.setData(Uri.parse("tel:" + number2String));
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    getContext().startActivity(intencall);
                }
            });


            switch (Integer.parseInt(type)) {
                case CallLog.Calls.INCOMING_TYPE:
                    imgType.setImageResource(R.drawable.incoming_call);
                    if (Integer.parseInt(duration) == 0) tv_duration.setText("Denied");
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    imgType.setImageResource(R.drawable.outcoming_call);
                    if (Integer.parseInt(duration) == 0) tv_duration.setText("Not token");
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    imgType.setImageResource(R.drawable.miss_call);
                    tv_duration.setText("");
                    break;
                default:
                    break;
            }
            if (Name == null) {
                name.setText(number2String);
            } else {
                name.setText(Name);
            }

            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Name != null) {
                        ContactInfo ci = findContact(Name, data);
                        Intent intent = new Intent(getContext(), full_info.class);
                        if (ci != null) {
                            intent.putExtra("name", ci.getName());
                            intent.putExtra("id", ci.getId());
                        }
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), full_info.class);
                        intent.putExtra("phone", number2String);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    public void makeCall(){

        if(numString.startsWith("*") && numString.endsWith("#")){
            Intent callIntent = new Intent(Intent.ACTION_CALL, ussdToCallableUri(numString));
            startActivity(callIntent);
        }else{
            Intent intencall=new Intent(Intent.ACTION_CALL);
            intencall.setData(Uri.parse("tel:"+numString));
            startActivity(intencall);
        }
    }
    public void sentMessage(){
        if(numString == null) numString = "";
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+numString));
        startActivity(smsIntent);
    }


    public void addContact(){
        Intent intent = new Intent(getActivity(), addNewContact.class);
        intent.putExtra("phone", numString);
        startActivity(intent);

    }

    private Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }

    private ContactInfo findContact(String name,List<ContactInfo> lstContact){
        for(ContactInfo ci : lstContact){
            if (ci.getName().equals(name)) return ci;
        }
        return null;
    }

}
