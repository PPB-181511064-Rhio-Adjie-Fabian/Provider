package com.ppb.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    final private int REQUEST_READ_CONTACTS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else{
            ListContact();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] granResults){
        switch (requestCode){
            case REQUEST_READ_CONTACTS:
                if(granResults[0] == PackageManager.PERMISSION_GRANTED){
                    ListContact();
                } else{
                    Toast.makeText(MainActivity.this, "Permission Denied!!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, granResults);
        }
    }
    protected void ListContact(){
        Uri allContacts = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        Cursor c;
        CursorLoader cursorLoader = new CursorLoader(
          this,
             allContacts,
          projection,
                ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?",
                    new String[] {"%ade"},
                            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );
        c = cursorLoader.loadInBackground();
        PrintContacts(c);
         String[] columns = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID};
         int[] views = new int[] {R.id.contactName, R.id.contactId};
         SimpleCursorAdapter adapter;
         adapter = new SimpleCursorAdapter(
                 this, R.layout.activity_main, c, columns, views,
                 CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
         this.setListAdapter(adapter);
     }

     private void PrintContacts(Cursor c){
        if(c.moveToFirst()){
            do{
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String contactDisplayName =
                        c.getString(c.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME
                        ));
                Log.v("Content Providers", contactId + ", " + contactDisplayName);

                Cursor phoneCursor =
                        getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                                        contactId, null, null
                        );
                while (phoneCursor.moveToNext()){
                    Log.v(
                            "Content Providers",
                            phoneCursor.getString(
                                    phoneCursor.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                            )
                    );
                }
                phoneCursor.close();
            }while (c.moveToNext());
        }
     }
}
