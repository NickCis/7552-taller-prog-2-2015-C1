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

/** Clase que representa una entidad de la base de datos.
 * Todas las clases que sean hijas deben utilizar la macro DB_ENTITY_CLASS(T) en su declaracion de clase y DB_ENTITY_DEF(T) en su definicion de la clase.
 */
class DbEntity {

	public:
		DbEntity();

		virtual ~DbEntity();

		/** Genera representacion en JSON
		 * @return devuelve la representacion generada
		 */
		virtual std::string toJson() const = 0;

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
		rocksdb::Status get(const std::string& key);

		/** Guarda registro (PUT de rocksdb).
		 * Utiliza buffers internos: this->key y this->value.
		 * @return Salida de estado del put
		 */
		rocksdb::Status put();

		/** Deserializa
		 * @param key[in]
		 * @param value[in]
		 * @return true si es valido, false si no
		 */
		virtual bool unPack(const std::string& key, const std::string& value) = 0;

	protected:
		std::string key; ///< Buffer interno usado para guarda el key
		std::string value; ///< Buffer interno usado para guardar el value

		/** Serializa utilizando buffers internos
		 */
		rocksdb::Slice pack();

		/** Serializa
		 * @param key[out]
		 * @param value[out]
		 * return Slice de Rocksdb
		 */
		rocksdb::Slice pack(std::string& key, std::string& value);

		/** Serializa el key.
		 * @param key: buffer donde se guardara la serializacion.
		 */
		virtual void packKey(std::string& key) = 0;

		/** Serializa el key utilizando como buffer this->key
		 */
		void packKey();


		/** Serializa value.
		 * @param value: buffer donde se guardara la serializacion
		 */
		virtual void packValue(std::string& value) = 0;

		/** Serializa value utilizando como buffer this->value;
		 */
		void packValue();


		/** Deserializa utilizando buffers internos
		 */
		bool unPack();

		/** Devuelve la instancia de Column Family de la base de datos de la clase.
		 * Este metodo lo crea por defecto la macro DB_ENTITY_CLASS(T).
		 */
		virtual rocksdb::ColumnFamilyHandle* getCf() = 0;

		/** Devuelve la instancia de Db de la clase
		 * Este metodo lo crea por defecto la macro DB_ENTITY_CLASS(T).
		 */
		virtual rocksdb::DB* getDb() = 0;

		/** Ejecuta DB::Put utilizando key y value provistos.
		 * @param key
		 * @param value
		 * @return Devolucion del DB::Put
		 */
		rocksdb::Status put(const rocksdb::Slice& key, const rocksdb::Slice& value);

		/** Ejecuta DB::Merge utilizando this->key y value.
		 * @param value
		 * @return Devolicon del DB::Merge
		 */
		rocksdb::Status merge(const std::string& value);

		/** Ejecuta DB::Merge utilizando key y value provistos.
		 * @param key
		 * @param value
		 * @return Devolicon del DB::Merge
		 */
		rocksdb::Status merge(const rocksdb::Slice& key, const rocksdb::Slice& value);
};

#endif
