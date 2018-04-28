package vn.intothewild.contact;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tin on 4/16/2017.
 */

public class FragmentHistory extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_CALLLOG = 1;
    ContentResolver contentResolver;
    callLogAdapter clAdapter;
    ListView lst1;
    TextView tv1;
    Cursor cursor;

    public FragmentHistory() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_history, container, false);

        if(ContextCompat.checkSelfPermission(FragmentHistory.this.getActivity(), Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FragmentHistory.this.getActivity(),
                    new String[] {Manifest.permission.READ_CALL_LOG},MY_PERMISSIONS_REQUEST_READ_CALLLOG);
        }
        else{
            ConnectView(view);
        }


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALLLOG: {
                if (permissions[0].equals(Manifest.permission.READ_CALL_LOG)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    settingList();
                } else {
                    return;
                }
            }
            default:
                break;
        }

    }

    public void ConnectView(View view) {
//        tv1 = (TextView) view.findViewById(R.id.textView);
        lst1 = (ListView) view.findViewById(R.id.lst1);

        settingList();

    }

    public void settingList(){
        Uri uri = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cursor = getActivity().getContentResolver().query(uri, null, null, null, CallLog.Calls.DATE + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
            clAdapter = new callLogAdapter(getActivity(), cursor);

            lst1.setAdapter(clAdapter);
        }
    }
}
