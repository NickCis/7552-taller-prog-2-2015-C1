#ifndef __DB_ITERATOR_H__
#define __DB_ITERATOR_H__

#include <rocksdb/db.h>
#include <rocksdb/slice.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>
#include <rocksdb/write_batch.h>

template <typename T>
class DbIterator {
	friend T;

	public:
		/** mueve el iterador para adelante
		 */
		void next(){
			this->it->Next();
			this->hasToUnPack = true;
		}

		/** Va a la primera notifcacion. La verdad no tiene mucha utilidad, se puede usar para listar todos las notificaiciones
		 * de todos los usuarios. (mueve el iterador)
		 */
		void seekToFirst(){
			this->it->SeekToFirst();
			this->hasToUnPack = true;
		}

		/** Devuelve la notificacion correspondiste a la posicion actual*/
		const T& value(){
			if(this->hasToUnPack && this->valid())
				this->unPack();

			return this->val;
		}; 

		/** Devuelve la Key de la notificacion
		 */
		rocksdb::Slice key() const{
			return this->it->key();
		}; 

		/* Estado del iterador
		 */
		rocksdb::Status status() const{
			return this->it->status();
		}

		/** Devuelve si el iterador esta en una posicion valida
		 */
		virtual bool valid() const{
			return this->it->Valid() && ( this->prefix.size() ? this->it->key().starts_with(rocksdb::Slice(this->prefix)) : true);
		}

		/** Operacion Delete. Delete es una palabra reservada =(, por eso tiene este nombre criptico.
		 */
		void dlt() {
			this->batch.Delete(this->cf, this->key());
		}

		/** Busca notificacion apartir de usuario. (mueve el iterador)
		 * @param prefijo de la key
		 */
		virtual void seek(const std::string& prefix){
			this->prefix = prefix;
			this->it->Seek(rocksdb::Slice(this->prefix));
			this->hasToUnPack = true;
		}

		rocksdb::Status write(){
			if(this->batch.Count()){
				rocksdb::Status s = this->db->Write(rocksdb::WriteOptions(), &this->batch);
				this->batch.Clear();
				return s;
			}

			return rocksdb::Status::OK();
		}

		virtual ~DbIterator(){
			this->write();
		}

	protected:
		rocksdb::Iterator *it; ///< Instancia al iterador de rocksdb
		rocksdb::DB *db; ///< Instancia db
		rocksdb::ColumnFamilyHandle* cf; ///< Instancia column family

		rocksdb::WriteBatch batch; ///< Batch que guarda las operaciones a realizaar con el iterator.
		bool hasToUnPack;
		T val;

		rocksdb::Slice _value() const{
			return this->it->value();
		}; 

		bool unPack(){
			this->hasToUnPack = false;
			return this->val.unPack(this->key().ToString(), this->_value().ToString());
		}

		std::string prefix; ///< Prefijo de las Keys buscadas

		/** Constructor
		 * @param iterador de rocksdb asociado a la db
		 */
		DbIterator(rocksdb::Iterator* i, rocksdb::DB* d, rocksdb::ColumnFamilyHandle* c) :
			it(i),
			db(d),
			cf(c),
			hasToUnPack(true) {}

};

#endif
