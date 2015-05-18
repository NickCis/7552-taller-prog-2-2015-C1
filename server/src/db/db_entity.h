#ifndef __DB_ENTITY_H__
#define __DB_ENTITY_H__

#include <rocksdb/db.h>
#include <rocksdb/slice.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>

#include "db_iterator.h"

#define DB_ENTITY_CLASS_PROTECTED(T) \
		static rocksdb::DB *db; \
		static rocksdb::ColumnFamilyHandle *cf; \
		rocksdb::Status _put(const rocksdb::Slice& key, const rocksdb::Slice& value){ \
			return T::db->Put(rocksdb::WriteOptions(), T::cf, rocksdb::Slice(key), rocksdb::Slice(value)); \
		}

#define DB_ENTITY_CLASS_PUBLIC(T) \
		T(const DbIterator<T>& it) { \
			this->unPack(it.key().ToString(), it._value().ToString()); \
		} \
		static void SetDB(rocksdb::DB* db, rocksdb::ColumnFamilyHandle* cf){ \
			T::db = db; \
			T::cf = cf; \
		}

#define DB_ENTITY_DEF(T) \
rocksdb::DB* T::db = NULL; \
rocksdb::ColumnFamilyHandle* T::cf = NULL;


//template<typename T>
class DbEntity {

	public:
		DbEntity(){}

		virtual ~DbEntity(){}

		virtual std::string toJson() const = 0; ///< Representacion en JSON

		//virtual DbIterator<T> NewIterator() = 0;  ///< Crea iterador

		/** Metodo que sirve para setear la instancia a la base de datos y a la column family.
		 * @param db[in]: Instancia a la base de datos a usar
		 * @param cf[in]: instancia a la column family a usar
		 */
		/*static void SetDB(rocksdb::DB* db, rocksdb::ColumnFamilyHandle* cf){
			T::db = db;
			T::cf = cf;
		}*/

		rocksdb::Status put(){
			this->pack();
			return this->_put(rocksdb::Slice(this->key), rocksdb::Slice(this->value));
		}

	protected:
		std::string key;
		std::string value;

		/** Serializa utilizando buffers internos
		 */
		rocksdb::Slice pack(){
			return this->pack(this->key, this->value);
		}

		/** Serializa
		 * @param key[out]
		 * @param value[out]
		 * return Slice de Rocksdb
		 */
		rocksdb::Slice pack(std::string& key, std::string& value){
			this->packKey(key);
			this->packValue(value);
			return rocksdb::Slice(value);
		}

		virtual void packKey(std::string& key) = 0;
		virtual void packValue(std::string& value) = 0;

		/** Deserializa utilizando buffers internos
		 */
		bool unPack(){
			return this->unPack(this->key, this->value);
		}

		/** Deserializa
		 * @param key[in]
		 * @param value[in]
		 * @return true si es valido, false si no
		 */
		virtual bool unPack(const std::string& key, const std::string& value) = 0;

		virtual rocksdb::Status _put(const rocksdb::Slice& key, const rocksdb::Slice& value) = 0;

		/*rocksdb::Status put(const rocksdb::Slice& key, const rocksdb::Slice& value){
			return T::db->Put(rocksdb::WriteOptions(), T::cf, rocksdb::Slice(key), rocksdb::Slice(value));
		}*/

		//static rocksdb::DB *db;
		//static rocksdb::ColumnFamilyHandle *cf;
};

#endif
