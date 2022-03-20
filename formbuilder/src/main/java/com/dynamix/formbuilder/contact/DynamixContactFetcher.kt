package com.dynamix.formbuilder.contact

import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dynamix.core.utils.permission.PermissionUtils
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * Created by user on 11/2/20
 */
class DynamixContactFetcher {
    private val REQ_CODE_READ_CONTACT = 1
    private val contactSubject: BehaviorSubject<List<com.dynamix.formbuilder.contact.DynamixContact>> = BehaviorSubject.create()
    private var isRequestInProgress = false

    val isContactFetched: Boolean
        get() = contactSubject.hasValue()

    // make a shallow copy as this is shared
    val contactList: Observable<List<com.dynamix.formbuilder.contact.DynamixContact>>
        get() =// make a shallow copy as this is shared
            contactSubject.map { it.toList() }

    fun queryContacts(activity: AppCompatActivity): Disposable? {
        if (isRequestInProgress) {
            return null
        }
        isRequestInProgress = true
        if (!PermissionUtils.hasReadContactsPermission(activity)) {
            requestContactPermission(activity)
            return null
        }
        return Single.create<List<com.dynamix.formbuilder.contact.DynamixContact>> {
            val contentResolver = activity.contentResolver
            val cursor =
                contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            val contacts: MutableList<com.dynamix.formbuilder.contact.DynamixContact> = ArrayList()
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == "1"
                    if (hasPhoneNumber) {
                        val cp = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        val singleContacts: MutableList<com.dynamix.formbuilder.contact.DynamixContact> = ArrayList()
                        if (cp != null) {
                            while (cp.moveToNext()) {
                                var phone =
                                    cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                // strip and validate the number
                                phone =
                                    com.dynamix.formbuilder.contact.DynamixContactNumberUtils.getStrippedPhoneNumber(
                                        phone
                                    )
                                if (com.dynamix.formbuilder.contact.DynamixContactNumberUtils.isValidPhoneNumber(
                                        phone
                                    )
                                ) {
                                    // check if we already have added this number before
                                    var isUniqueContact = true
                                    for (contact in singleContacts) {
                                        if (contact.number.equals(phone, ignoreCase = true)) {
                                            isUniqueContact = false
                                            break
                                        }
                                    }
                                    if (isUniqueContact) {
                                        singleContacts.add(
                                            com.dynamix.formbuilder.contact.DynamixContact(
                                                name,
                                                phone
                                            )
                                        )
                                    }
                                }
                            }
                            cp.close()
                        }
                        contacts.addAll(singleContacts)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            it.onSuccess(contacts)
            isRequestInProgress = false
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ contactSubject.onNext(it) }) {
                contactSubject.onError(it)
            }
    }

    private fun requestContactPermission(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_CONTACTS),
            REQ_CODE_READ_CONTACT
        )
    }

    fun onRequestPermissionResult(
        activity: AppCompatActivity,
        requestCode: Int,
        grantResults: IntArray
    ) {
        isRequestInProgress = false
        if (requestCode == REQ_CODE_READ_CONTACT && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            queryContacts(activity)
        } else {
            contactSubject.onError(IllegalArgumentException("Failed to read contacts"))
        }
    }
}