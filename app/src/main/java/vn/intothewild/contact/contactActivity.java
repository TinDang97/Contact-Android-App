package vn.intothewild.contact;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.text.Normalizer;
import java.util.logging.LogRecord;

public class contactActivity extends AppCompatActivity {

    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    ViewPager viewPager_main;
    TabLayout tabLayout;
    List<ContactInfo> contactInfos = new ArrayList<>();
    ViewPagerAdapter adapterViewPager;
    FragmentContact fragmentContact;
    FragmentDial fragmentDial;
    Uri contactURI;
    String contactID;
    Handler mHandler;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mHandler = new Handler();
        fragmentContact = new FragmentContact(contactInfos);
        fragmentDial = new FragmentDial(contactInfos);


        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);

        adapterViewPager = new ViewPagerAdapter(getSupportFragmentManager());
        adapterViewPager.addFragment(fragmentDial, "Dial");
        adapterViewPager.addFragment(new FragmentHistory(), "History");
        adapterViewPager.addFragment(fragmentContact, "Contact");
        viewPager_main.setOffscreenPageLimit(2);
        viewPager_main.setAdapter(adapterViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs_main);
        tabLayout.setupWithViewPager(viewPager_main);

        for(int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            switch (i) {
                case 0:
                    if (tab != null) {
                        tab.setIcon(R.drawable.ic_dialpad_black_24dp);
                    }
                    break;
                case 1:
                    if (tab != null) {
                        tab.setIcon(R.drawable.ic_call_24dp);
                    }
                    break;
                case 2:
                    if (tab != null) {
                        tab.setIcon(R.drawable.ic_account_circle_white_24dp);
                    }
                    break;
                default:
                    break;
            }
        }

        ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_contact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(contactActivity.this, addNewContact.class));
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0) {
                    fab.animate().alpha(0f);
                    fab.setVisibility(View.GONE);
                    fragmentDial.getLastCall();
                }
                else {
                    fab.setVisibility(View.VISIBLE);
                    fab.animate().alpha(1f);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        registerReceiver(mBroadcastReceiver, new IntentFilter("update"));
        Log.i("register", "oke");

        new Thread(new Runnable() {
            @Override
            public void run() {
                retrieveContact();
            }
        }).start();
    }

    private void retrieveContact(){
        contactInfos.clear();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if(cursor != null){
            while (cursor.moveToNext()){
                ///READ NAME AND ID
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //READ PHONE

                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                List<String> phone = new ArrayList<>();
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!phone.contains(number))
                            phone.add(number);
                    }
                    phoneCursor.close();
                }

                    //READ PHOTO
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));


                contactInfos.add(new ContactInfo(id, name, BitmapFactory.decodeStream(inputStream), phone));
                if(contactInfos.size() % 20 == 0)
                    update();
            }
            cursor.close();
        }
        update();
    }

    private void update(){
        Collections.sort(contactInfos, new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo o1, ContactInfo o2) {
                String n1 = Normalizer.normalize(o1.getName().toUpperCase(), Normalizer.Form.NFD);
                String n2 = Normalizer.normalize(o2.getName().toUpperCase(), Normalizer.Form.NFD);
                if(n1.toUpperCase().contains("Đ")) {
                    n1 = n1.replaceAll("Đ", "D");
                }

                if(n2.toUpperCase().contains("Đ")) {
                    n2 = n2.replaceAll("Đ", "D");
                }
                return n1.compareTo(n2);
            }
        });
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                fragmentContact.notifyDataChanged(contactInfos);
                fragmentDial.notifyData(contactInfos);
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("update")){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        retrieveContact();
                    }
                }).start();
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        Log.i("unregister", "oke");
        super.onDestroy();
    }
}
