package vn.intothewild.contact;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class addNewContact extends AppCompatActivity{

    private EditText firstName;
    private EditText middleName;
    private EditText lastName;

    private List<View> phone = new ArrayList<>();
    private List<View> email = new ArrayList<>();
    private List<String> oldPhone = new ArrayList<>();
    private List<String> oldEmail = new ArrayList<>();
    private List<Integer> oldEmailType = new ArrayList<>();
    private List<Integer> oldPhoneType = new ArrayList<>();
    private String idContact = "";
    private Bitmap image;
    private ImageView avatar;
    private LinearLayout layoutPhone;
    private LinearLayout layoutEmail;
    private boolean haveError = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("New Contact");
        }
        ///Name
        firstName = (EditText) findViewById(R.id.input_firstname);
        middleName = (EditText) findViewById(R.id.input_middlename);
        lastName = (EditText) findViewById(R.id.input_lastname);

        //Avatar
        avatar = (ImageView) findViewById(R.id.avatar_add);

        //Phone
        layoutPhone = (LinearLayout) findViewById(R.id.layout_phone);

        //Email
        layoutEmail = (LinearLayout) findViewById(R.id.layout_email);

        //Get photo
        FloatingActionButton getImage = (FloatingActionButton) findViewById(R.id.take_new_img);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });




        //Retrieve Data
        if(getIntent().getAction() != null)
            if(getIntent().getAction().equals("edit"))

                if (actionBar != null) {
                    actionBar.setTitle("Edit");
                }

                if(getIntent().getStringExtra("id") != null) {
                    //get Id from intent
                    idContact = getIntent().getStringExtra("id");

                    //get Bitmap
                    image = BitmapFactory.decodeStream(ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(idContact)), true
                    ));

                    avatar.setImageBitmap(image);

                    //get Name
                    Cursor nameCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,null
                            ,ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{idContact, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);

                    if (nameCursor != null && nameCursor.getCount() > 0) {
                        nameCursor.moveToFirst();
                        firstName.setText(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
                        middleName.setText(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)));
                        lastName.setText(nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
                        nameCursor.close();
                    }

                    //get Phone
                    Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{idContact}, null);

                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            oldPhone.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            oldPhoneType.add(phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                        }
                        phoneCursor.close();

                        Iterator<Integer> type = oldPhoneType.iterator();
                        for (String number : oldPhone){
                            addNewViewPhone(number, type.next());
                        }
                    }

                    //get Email
                    Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{idContact}, null);

                    if (emailCursor != null) {
                        while (emailCursor.moveToNext()) {
                            oldEmail.add(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                            oldEmailType.add(emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)));
                        }
                        emailCursor.close();

                        Iterator<Integer> type = oldEmailType.iterator();
                        for (String address : oldEmail){
                            addNewViewEmail(address, type.next());
                        }
                    }
                }

        if(getIntent().getStringExtra("phone") != null)
            addNewViewPhone(getIntent().getStringExtra("phone"), 1);
        else
            addNewViewPhone("", 1);
        addNewViewEmail("", 1);

    }

    private void addNewViewPhone(String number, int type){
        final View view = LayoutInflater.from(this).inflate(R.layout.add_item_phone,null,false);

        Spinner type_phone = (Spinner) view.findViewById(R.id.add_phone_type);
        ArrayAdapter<CharSequence> adapterPhone = ArrayAdapter.createFromResource(this,
                R.array.typePhone, android.R.layout.simple_spinner_item);
        adapterPhone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_phone.setAdapter(adapterPhone);
        type_phone.setSelection(type);

        final TextInputLayout inputLayoutPhone = (TextInputLayout) view.findViewById(R.id.input_phone);
        final EditText inputPhone = (EditText) view.findViewById(R.id.input_phone_new);
        if(!number.isEmpty())
            inputPhone.setText(number);
        inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                haveError = !checkPhone(inputLayoutPhone, inputPhone);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(phone.indexOf(view) == phone.size()-1 && !inputPhone.getText().toString().isEmpty())
                    addNewViewPhone("",1);
            }
        });

        phone.add(view);
        layoutPhone.addView(view);
    }

    private void addNewViewEmail(String address, int type){
        final View view = LayoutInflater.from(this).inflate(R.layout.add_item_email, null,false);

        Spinner type_email = (Spinner) view.findViewById(R.id.add_email_Type);
        ArrayAdapter<CharSequence> adapterEmail = ArrayAdapter.createFromResource(this,
                R.array.typeEmail, android.R.layout.simple_spinner_item);
        adapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_email.setAdapter(adapterEmail);
        type_email.setSelection(type);

        final EditText inputEmail = (EditText) view.findViewById(R.id.input_email_new);
        final TextInputLayout inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_email);
        if(!address.isEmpty())
            inputEmail.setText(address);
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                haveError = !checkEmail(inputLayoutEmail, inputEmail);

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(email.indexOf(view) == email.size()-1 && !inputEmail.getText().toString().isEmpty())
                    addNewViewEmail("", 1);
            }
        });

        email.add(view);
        layoutEmail.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    image = BitmapFactory.decodeFile(picturePath);
                    avatar.setImageBitmap(image);

                }
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean save(){

            for(int i = 0; i< phone.size()-1; i++) {
                View view = phone.get(i);
                TextInputLayout inputLayoutPhone = (TextInputLayout) view.findViewById(R.id.input_phone);
                EditText inputPhone = (EditText) view.findViewById(R.id.input_phone_new);

                if (!checkPhone(inputLayoutPhone, inputPhone))
                    return false;
            }
            if(haveError)
                return false;

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if(getIntent().getAction() != null) {
            if (getIntent().getAction().equals("edit")) {
                //update name
                ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                                + "=?", new String[]{idContact, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, middleName.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName.getText().toString().trim())
                        .build());

                //deleteOldNumber
                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{idContact, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE} )
                        .build());

                //update phone
                phone.remove(phone.size()-1);
                for (View view : phone) {
                    String newNumber = ((EditText) view.findViewById(R.id.input_phone_new)).getText().toString().trim();
                    int newType = ((Spinner) view.findViewById(R.id.add_phone_type)).getSelectedItemPosition();
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(idContact))
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, newType)
                            .build());
                }

                //deleteOldEmail
                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{idContact, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE} )
                        .build());

                //update Email
                email.remove(email.size()-1);
                for (View view : email) {
                    String newAddress = ((EditText) view.findViewById(R.id.input_email_new)).getText().toString().trim();
                    int newType = ((Spinner) view.findViewById(R.id.add_email_Type)).getSelectedItemPosition();
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(idContact))
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, newAddress)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, newType)
                            .build());
                }
                //deleteOldImage
                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{idContact, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE} )
                        .build());

                //update image
                if(image != null) {
                    Log.i("imgbackgroud","check");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(idContact))
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                            .build());
                }
            }
        }

        else {
            Cursor checkID = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.MIMETYPE+ "=?",
                    new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);

            boolean isExist = false;
            if(checkID != null && checkID.getCount() > 0){
                while (checkID.moveToNext()) {
                    isExist = firstName.getText().toString().trim().toLowerCase().equals(checkID.getString(checkID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)).toLowerCase());
                    isExist = isExist && middleName.getText().toString().trim().toLowerCase().equals(checkID.getString(checkID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)).toLowerCase());
                    isExist = isExist && lastName.getText().toString().trim().toLowerCase().equals(checkID.getString(checkID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)).toLowerCase());
                    if(isExist){
                        idContact = checkID.getString(checkID.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                        break;
                    }
                }
                checkID.close();
            }

            //insert Old
            if(isExist){
                //update phone
                phone.remove(phone.size()-1);
                for (View view : phone) {
                    String newNumber = ((EditText) view.findViewById(R.id.input_phone_new)).getText().toString().trim();
                    int newType = ((Spinner) view.findViewById(R.id.add_phone_type)).getSelectedItemPosition();
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(idContact))
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, newType)
                            .build());
                }

                //deleteOldEmail
                ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{idContact, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE} )
                        .build());

                //update Email
                email.remove(email.size()-1);
                for (View view : email) {
                    String newAddress = ((EditText) view.findViewById(R.id.input_email_new)).getText().toString().trim();
                    int newType = ((Spinner) view.findViewById(R.id.add_email_Type)).getSelectedItemPosition();
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(idContact))
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, newAddress)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, newType)
                            .build());
                }

                if(image != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                                    ContactsContract.Data.MIMETYPE + "=?", new String[]{idContact, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                            .build());

                    try {
                        stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //insert new
            else {
                int rawContactInsertIndex = ops.size();
                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, middleName.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName.getText().toString().trim())
                        .build());

                phone.remove(phone.size()-1);
                for(View view : phone){
                    String number = ((EditText) view.findViewById(R.id.input_phone_new)).getText().toString().trim();
                    int type = ((Spinner) view.findViewById(R.id.add_phone_type)).getSelectedItemPosition();

                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, type)
                            .build());
                }

                email.remove(email.size()-1);
                for(View view : email){
                    String address = ((EditText) view.findViewById(R.id.input_email_new)).getText().toString().trim();
                    int type = ((Spinner) view.findViewById(R.id.add_email_Type)).getSelectedItemPosition();

                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, address)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, type)
                            .build());
                }

                if(image != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                            .build());


                    try {
                        stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            if(ops.size() > 0) {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                sendBroadcast(new Intent("update"));

            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Save successfully", Toast.LENGTH_LONG).show();
        return true;
    }

    public String getRawContactId(String contactId)
    {
        String res = "";
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{ contactId };
        Cursor c = getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if(c != null && c.moveToFirst())
        {
            res = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
            c.close();
        }
        return res;
    }

    private boolean checkPhone(TextInputLayout textInputLayout, EditText editText) {
        String phone = editText.getText().toString();

        if(phone.length() < 10){
            textInputLayout.setError("Numbers phone must larger than 10 digits!");
            textInputLayout.setErrorEnabled(true);
            requestFocus(editText);
            return false;
        }
        else
            textInputLayout.setErrorEnabled(false);

        return true;
    }

    private boolean checkEmail(TextInputLayout textInputLayout, EditText editText) {
        String email = editText.getText().toString();
        if(email.isEmpty())
            return true;
        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            textInputLayout.setError("Error email! Example: name@email.com");
            textInputLayout.setErrorEnabled(true);
            requestFocus(editText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.btn_add_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                AlertDialog.Builder save =new AlertDialog.Builder(addNewContact.this);
                save.setTitle("Do you want to save?");
                save.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(save())
                            finish();
                    }
                });
                save.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                save.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = save.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF006FFF"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF006FFF"));
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#FF006FFF"));
                break;
            case R.id.save:
                if(save())
                    finish();
                break;
            case R.id.close:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder save =new AlertDialog.Builder(addNewContact.this);
        save.setTitle("Do you want to save?");
        save.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(save())
                    finish();
            }
        });
        save.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        save.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = save.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF006FFF"));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF006FFF"));
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#FF006FFF"));
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}