package com.berkeyilmaz.cardapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.berkeyilmaz.cardapp.data.local.entity.InternalContactEntity

@Dao
abstract class InternalContactDAO {

    @Query("SELECT * FROM internal_contacts")
    abstract suspend fun getAllContacts(): List<InternalContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertContacts(contacts: List<InternalContactEntity>)

    @Query("DELETE FROM internal_contacts")
    abstract suspend fun deleteAllContacts()

    @Transaction
    open suspend fun replaceAllContacts(contacts: List<InternalContactEntity>) {
        deleteAllContacts()
        insertContacts(contacts)
    }
}

