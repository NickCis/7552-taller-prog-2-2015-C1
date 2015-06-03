#ifndef __MESSAGE_H__
#define __MESSAGE_H__

#include <ctime>
#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"

/** Clase que maneja los Mensajes en la db
 */
class Message : public DbEntity {
	DB_ENTITY_CLASS(Message)

	public:
		/** Clase para iterar por los mensajes
		 */
		class Iterator;

		/** Constructor vacio
		 */
		Message();

		/** Crea un nuevo mensaje, setea el tiempo al actual.
		 * No la guarda en DB.
		 */
		static Message Now();

		/** Crea un nuevo mensaje, setea el tiempo al actual.
		 * No la guarda en DB.
		 * @param to: usuario al que le corresponde la notificacion
		 * @param from: usuario al que le corresponde la notificacion
		 * @param message: mensaje
		 */
		static Message Now(const std::string& to, const std::string& from, const std::string&);

		/** Devuelve un nuevo iterador.
		 * @return iterador
		 */
		static Message::Iterator NewIterator();

		const std::string& getFrom() const; ///< Devuelve Emisor del mensaje
		const std::string& getTo() const; ///< Devuelve receptor del mensaje
		const std::string& getMsg() const; ///< Mensaje
		const time_t& getTime() const; ///< Hora de cuando el msje fue enviado
		std::string getId() const; ///< Id en formato hexa
		const uint64_t& getIdBin() const; ///< Id en formato binario
		std::string toJson() const; ///< Representacion en JSON del mensaje

		void setFrom(const std::string&); ///< Setea Emisor del mensaje
		void setTo(const std::string&); ///< Setea destinatario del mensaje
		void setMsg(const std::string&); ///< Setea el mensaje

		bool unPack(const std::string& key, const std::string& value);

		/** Get, antes se debe setear el From y To.
		 * @param id: id en formato hexa
		 * @return estado de la consulta.
		 */
		rocksdb::Status get(const std::string& id);

	protected:
		using DbEntity::packKey;
		void packKey(std::string& key);
		void packValue(std::string& value);

		uint64_t id; ///< id [ tiempo en usecs en big endian ]
		std::string to; ///< Destinatario
		std::string from; ///< emisor
		std::string msg; ///< mensaje
		time_t arrived; ///< hora de arrivo (a destino)
		time_t read; ///< hora de lectura
		time_t t; ///< tiempo de servidor
		int has_file; ///< tiene attachment?
};

/** Clase para iterar por las notificaciones
*/
class Message::Iterator : public DbIterator<Message>{
	friend Message;
	public:
		/** Busca notificacion apartir de usuario. (mueve el iterador)
		 * Los usuarios son conmutativos, no importa el orden.
		 * @param from: usuario 1
		 * @param from: usuario 2
		 */
		void seek(const std::string&, const std::string&);

	protected:
		/** Constructor
		 * @param iterador de rocksdb asociado a la db
		 */
		Iterator(rocksdb::Iterator*, rocksdb::DB*, rocksdb::ColumnFamilyHandle*);
};

#endif
