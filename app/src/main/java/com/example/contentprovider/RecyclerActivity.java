package com.example.contentprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.contentprovider.databinding.ActivityRecyclerBinding;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {
    ActivityRecyclerBinding binding;
    public static final int READ_CONTACTS_REQUEST = 1;
    ArrayList<Contacts> contactsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecyclerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // اذا البيرمشن تبعتي مش مقبولة
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST);
        } else {
            getAllContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // كيف بدي اتحقق اذا وافق عليها ولا لا
        // grantResults لازم تكون اكبر من صفر
        // عشان اذا كانت اكبر من صفر يعني انقبلو البيانات
        if (requestCode == READ_CONTACTS_REQUEST && grantResults.length > 0) {
            getAllContacts();
        }
    }
    public void getAllContacts() {
        contactsArrayList = new ArrayList<>();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if (cursor.moveToFirst()) {
            // do while عشان اجيب كل الاي دي اللي عندي
            do {
                long contactId = cursor.getLong(cursor.getColumnIndexOrThrow("_ID"));
                Uri uri1 = ContactsContract.Data.CONTENT_URI;
                Cursor cursor1 = getContentResolver().query(uri1, null, ContactsContract.Data.CONTACT_ID + " =?",
                        new String[]{String.valueOf(contactId)}, null);
                String contactName;
                String contactNumber = null;
                // عشان حطيت سليكشن مش محتاج دو وايل
                if (cursor1.moveToFirst()){
                    contactName = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    do {
                        if (cursor1.getString(cursor1.getColumnIndexOrThrow("mimeType"))
                                .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                            if (cursor1.getInt(cursor1.getColumnIndexOrThrow("data2"))
                                    == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                contactNumber = cursor1.getString(cursor1.getColumnIndexOrThrow("data1"));
                            }
                        }
                    } while (cursor1.moveToNext());
                    contactsArrayList.add(new Contacts(contactName,contactNumber));
                    Adapter adapter = new Adapter(getApplicationContext(), contactsArrayList, new MyListener() {
                        @Override
                        public void click(int position) {
                            Contacts contacts = contactsArrayList.get(position);
                            String num = contacts.getNumber();
                            Intent intent = getIntent();
                            intent.putExtra("contactNumber",num);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
                    binding.MainRecycler.setAdapter(adapter);
                    binding.MainRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                            RecyclerView.VERTICAL,false));
                }
            } while (cursor.moveToNext());
        }
    }
}