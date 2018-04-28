package vn.intothewild.contact;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tin on 4/16/2017.
 */

public class FragmentContact extends Fragment {
    private mRecyclerAdapter recyclerAdapter;
    private EditText editText;
    private List<ContactInfo> contactInfos = new ArrayList<>();
    private List<ContactInfo> contactInfoBasic = new ArrayList<>();
    RecyclerView recyclerView;

    public FragmentContact(List<ContactInfo> contactInfos){
        this.contactInfos.addAll(contactInfos);
        this.contactInfoBasic.addAll(this.contactInfos);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        recyclerAdapter = newmRecyclerAdapter();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contact, container,false);
        editText = (EditText) view.findViewById(R.id.search_tv);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = editText.getText().toString().toLowerCase();
                contactInfos.clear();
                if(text.isEmpty())
                    contactInfos.addAll(contactInfoBasic);
                else
                    for (ContactInfo ci : contactInfoBasic)
                        if (ci.getName().toLowerCase().contains(text))
                            contactInfos.add(ci);

                recyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethod = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethod.hideSoftInputFromWindow(v.getWindowToken(),0);
                    editText.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                }
            }
        });
        return view;
    }

    private mRecyclerAdapter newmRecyclerAdapter(){
        return new mRecyclerAdapter(contactInfos) {
            @Override
            public void infoContactOnClick(int position) {
                Intent intent = new Intent(getContext(), full_info.class);
                intent.putExtra("id", contactInfos.get(position).getId());
                intent.putExtra("name", contactInfos.get(position).getName());
                startActivity(intent);
            }

            @Override
            public boolean infoContactLongOnClick(final int position, View view) {
                final PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.setGravity(Gravity.END);
                popupMenu.inflate(R.menu.contact_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.menuItem_remove){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Are you sure?")
                                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                                                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?",new String[]{contactInfos.get(position).getId()})
                                        .build());
                                        try {
                                            if(ops.size() > 0) {
                                                getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                                getActivity().sendBroadcast(new Intent("update"));

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
                            android.app.AlertDialog alertDialog = builder.create();
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

            @Override
            public void onClickCall(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void onClickSMS(Intent intent) {
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.i("SMS", ex.toString());
                    Toast.makeText(getContext(),
                            "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void notifyDataChanged(List<ContactInfo> lstnew){
        recyclerAdapter.notifyDataSetChanged();
        contactInfoBasic.clear();
        contactInfos.clear();
        this.contactInfos.addAll(lstnew);
        lstnew = contactInfos;
        this.contactInfoBasic.addAll(this.contactInfos);
    }

}