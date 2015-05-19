#ifndef __DB_ENTITY_H__
#define __DB_ENTITY_H__

#include <rocksdb/db.h>
#include <rocksdb/slice.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>

#include "db_iterator.h"

#define DB_ENTITY_CLASS(T) \
	protected: \
		static rocksdb::DB *db; \
		static rocksdb::ColumnFamilyHandle *cf; \
		rocksdb::ColumnFamilyHandle* getCf(){ return T::cf; }\
		rocksdb::DB* getDb(){ return T::db; }\
	public: \
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

		/** Obtiene registro (GET de rocksdb)
		 * @return Salida de estado del put
		 */
		rocksdb::Status get(const std::string& key){
			this->key = key;
			rocksdb::Status s = this->getDb()->Get(rocksdb::ReadOptions(), this->getCf(), rocksdb::Slice(this->key), &this->value);
			if(s.ok())
				if(!this->unPack())
					return rocksdb::Status::Corruption(this->key);

			return s;
		}

		/** Guarda registro (PUT de rocksdb)
		 * @return Salida de estado del put
		 */
		rocksdb::Status put(){
			this->pack();
			return this->put(rocksdb::Slice(this->key), rocksdb::Slice(this->value));
		}

		/** Deserializa
		 * @param key[in]
		 * @param value[in]
		 * @return true si es valido, false si no
		 */
		virtual bool unPack(const std::string& key, const std::string& value) = 0;

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

		virtual rocksdb::ColumnFamilyHandle* getCf() = 0;
		virtual rocksdb::DB* getDb() = 0;

		rocksdb::Status put(const rocksdb::Slice& key, const rocksdb::Slice& value){
			return this->getDb()->Put(rocksdb::WriteOptions(), this->getCf(), key, value);
		}

		rocksdb::Status merge(const std::string& value){
			return this->merge(rocksdb::Slice(this->key), rocksdb::Slice(value));
		} 

		rocksdb::Status merge(const rocksdb::Slice& key, const rocksdb::Slice& value){
			return this->getDb()->Merge(rocksdb::WriteOptions(), this->getCf(), key, value);
		}
};

#endif
