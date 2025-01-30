package com.dullbluelab.voicewriter3.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "symbols")
data class SymbolTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "key_code") val keyCode: String,
    val symbol: String,
    val token: String,
    @ColumnInfo(name = "close_brace")
    val closeBrace: String,
)

@Dao
interface SymbolDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: SymbolTable)

    @Update
    suspend fun update(item: SymbolTable)

    @Delete
    suspend fun delete(item: SymbolTable)

    @Query("SELECT * FROM symbols ORDER BY symbol ASC")
    fun getAll(): Flow<List<SymbolTable>>

    @Query("SELECT * FROM symbols WHERE key_code = :key")
    fun getItem(key: String): Flow<SymbolTable>

    @Query("SELECT * FROM symbols WHERE token = :token")
    fun getByToken(token: String): Flow<SymbolTable>

    @Query("SELECT * FROM symbols WHERE key_code = :key")
    fun getByKey(key: String): Flow<SymbolTable>

    @Query("SELECT * FROM symbols WHERE symbol = :symbol")
    fun getBySymbol(symbol: String): Flow<SymbolTable>

    @Query("SELECT COUNT(*) FROM symbols")
    suspend fun count(): Int
}
