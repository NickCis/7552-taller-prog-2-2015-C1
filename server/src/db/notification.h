#ifndef __NOTIFICATION_H__
#define __NOTIFICATION_H__

#include <ctime>
#include <string>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"

/** Clase que maneja las notificaciones en la db
 */
class Notification : public DbEntity  {
	DB_ENTITY_CLASS(Notification)

	public:
		class Iterator;

		/** Enum que representa el tipo de notificacion
		 */
		typedef enum {
			NOTIFICATION_MESSAGE=0, ///< Tipo mensaje
			NOTIFICATION_ACK, ///< Tipo ACK
			NOTIFICATION_AVATAR, ///< Tipo Avatar
			NOTIFICATION_PROFILE, ///< Tipo Profile
			NOTIFICATION_CHECKIN ///< Tipo checkin
		} NotificationType;

		/** Constructor
		 */
		Notification();

		/** Crea una nueva notificacion, setea el tiempo al actual.
		 * No la guarda en DB.
		 */
		static Notification Now();

		/** Crea una nueva notificacion, setea el tiempo al actual.
		 * No la guarda en DB.
		 * @param to: usuario al que le corresponde la notificacion
		 * @param type: tipo de notificacion
		 * @param data: contenido de la notificacion (debe ser un JSON valido, es una precondicion, no se valida)
		 */
		static Notification Now(const std::string& to, Notification::NotificationType type, const std::string& data);

		const uint64_t& getIdBin() const; ///< Id en formato binario
		std::string getId() const; ///< id en formato hexa
		const time_t& getTime() const; ///< timestamp de la notificacion
		std::string toJson() const; ///< Representacion en JSON


		void setOwner(const std::string&);
		void setType(const Notification::NotificationType);
		void setData(const std::string&);
		const std::string& getData() const;

		/** Convierte un tipo de notificacion a su representacion en texto
		 * @param type:
		 * @return representacion en texto
		 */
		static std::string TypeToStr(const NotificationType& type);

		/** Borra notificaciones hasta (inclusive) la indicada.
		 * Marca como leidas, envia ACK de msjes recibidos.
		 * @param from: usuario due~no de las notificaciones
		 * @param id: ultima id (inclusive) que se borrara (formato hexa)
		 */
		static rocksdb::Status DeleteUpTo(const std::string& from, const std::string& id);

		/** Borra notificaciones hasta (inclusive) la indicada.
		 * @param from: usuario due~no de las notificaciones
		 * @param id: ultima id (inclusive) que se borrara (formato binario)
		 */
		static rocksdb::Status DeleteUpTo(const std::string& from, const uint64_t& id);

		static Notification::Iterator NewIterator();  ///< Crea iterador

		bool unPack(const std::string& key, const std::string& value);

	protected:
		void packKey(std::string& key);
		void packValue(std::string& value);

		/** Infomacion de la notificacion **/
		std::string owner; ///< a quien le pertenece la notificacion
		uint64_t id; ///< id en formato binario
		Notification::NotificationType type; ///< tipo de notificacion
		time_t t; ///< timestamp de la notificacion
		std::string data; ///< data que guarda la notificacion
};

/** Clase para iterar por las notificaciones
*/
class Notification::Iterator : public DbIterator<Notification>{
	friend Notification;
	public:
		/** Busca notificacion apartir de usuario. (mueve el iterador)
		 * @param from: usuario
		 */
		void seek(const std::string& from);

	protected:
		/** Constructor
		 * @param iterador de rocksdb asociado a la db
		 */
		Iterator(rocksdb::Iterator*, rocksdb::DB*, rocksdb::ColumnFamilyHandle*);
};

#endif
