package com.example.module4app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class LoadContacts : AppCompatActivity(){

    @Suppress("unused")
    companion object {
        private const val PERMISSION_READ_CONTACT = 101

        private val CONTENT_URI = ContactsContract.Contacts.CONTENT_URI
        private val PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        private const val ID = ContactsContract.Contacts._ID
        private const val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME
        private const val HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER
        private const val PHONE_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        private const val NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER

        private const val CONTACT_NAME = "CONTACT_NAME"
        private const val CONTACT_PHONE = "CONTACT_PHONE"
        private const val CONTACT_IMAGE = "CONTACT_IMAGE"
        private const val IS_FETCHED_FROM_CP = "CONTACT_IMAGE"
        private const val KONTACT_PREFERENCES = "KONTACT_PREFERENCES"
    }
    private var myContacts : ArrayList<MyContacts> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contactsload)

        val addContact: FloatingActionButton = findViewById(R.id.addContact)
        val listView: ListView = findViewById(R.id.listView)

        addContact.setOnClickListener {
            val intent = Intent(this, AddContact::class.java)
            startActivity(intent)
        }
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_CONTACTS
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                loadContactFromProvider()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@LoadContacts,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()

        listView.adapter = CAdapter(this,myContacts)

    }

    class CAdapter(mContext: Context, list: ArrayList<MyContacts>) :
        ContactsAdapter(mContext, list) {

        private val contactli = list

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return super.getView(position, convertView, parent).apply  {
                if (convertView == null) {

                    findViewById<View>(R.id.button).setOnClickListener { MyContacts ->

                        val contact = contactli[position]

                        val i = Intent(context,ShareMain::class.java)

                        i.putExtra("name", contact.contactName)
                        i.putExtra("number", contact.contactNumber)
                        context.startActivity(i)
                    }
                }
            }
        }

    }

    private fun log(message: String) = Log.d("DEBUG",message)

    @SuppressLint("Range")
    private fun loadContactFromProvider() {
        log("loading from Provider")

        val contentResolver = contentResolver
        val cursor = contentResolver.query(CONTENT_URI, null, null, null, DISPLAY_NAME)

        log("loading started...")
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                log("loading name")
                val id = cursor.getString(cursor.getColumnIndex(ID))
                val name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER))
                val contacts = MyContacts()
                if (hasPhoneNumber > 0) {
                    contacts.contactName = name
                    val phoneCursor = contentResolver.query(PHONE_URI, arrayOf(NUMBER), "$PHONE_ID = ?", arrayOf(id), null)
                    val phoneNumbers = ArrayList<String>()
                    phoneCursor!!.moveToFirst()
                    while (!phoneCursor.isAfterLast) {
                        val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ", "")
                        phoneNumbers.add(phoneNumber)
                        phoneCursor.moveToNext()
                    }
                    contacts.contactNumber = phoneNumbers
                    phoneCursor.close()
                }
                log("loading image")
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, ContentUris.withAppendedId(CONTENT_URI, id.toLong()), true)
                if (inputStream != null) {
                    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
                    contacts.contactImage = bitmap
                }
                log("""${contacts.contactName} ${contacts.contactNumber} ${contacts.contactImage.toString()}""")
                myContacts.add(contacts)
            }

            cursor.close()
        }

        log("loading done...")

    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


}
