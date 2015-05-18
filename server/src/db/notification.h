#ifndef __NOTIFICATION_H__
#define __NOTIFICATION_H__

#include <ctime>
#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

#include "db_entity.h"

/** Clase que maneja las notificaciones en la db
 */
class Notification : public DbEntity  {

	public:
		DB_ENTITY_CLASS_PUBLIC(Notification)

		class Iterator;

		/** Enum que representa el tipo de notificacion
		 */
		typedef enum {
			NOTIFICATION_MESSAGE=0, ///< Tipo mensaje
			NOTIFICATION_ACK ///< Tipo ACK
		} NotificationType;

		/** Constructor
		 */
		Notification();

		/** Escribe una notificacion en la db, genera su Key e id correspondiente.
		 * @param to: usuario al que le corresponde la notificacion
		 * @param type: tipo de notificacion
		 * @param data: contenido de la notificacion (debe ser un JSON valido, es una precondicion, no se valida)
		 * @param n[out]: nueva notificacion
		 * @return Estado de error
		 */
		static rocksdb::Status Put(const std::string& to, Notification::NotificationType type, const std::string& data, Notification& n);
		static Notification Now();
		static Notification Now(const std::string& to, Notification::NotificationType type, const std::string& data);

		const uint64_t& getIdBin() const; ///< Id en formato binario
		std::string getId() const; ///< id en formato hexa
		const time_t& getTime() const; ///< timestamp de la notificacion
		std::string toJson() const; ///< Representacion en JSON

		void setOwner(const std::string&);
		void setType(const Notification::NotificationType);
		void setData(const std::string&);

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
		void packKey(std::string& key);
		void packValue(std::string& value);
		bool unPack(const std::string& key, const std::string& value);

	protected:
		DB_ENTITY_CLASS_PROTECTED(Notification)

		/** Genera Key (completa) de la db apartir de usuario e id.
		 * salida del tipo: usuario/id
		 * @param data[out]: buffer donde se escribira
		 * @param from[in]: usuario
		 * @param id[in]: id en formato binario
		 */
		static void GetKeyFromUser(std::vector<char>& data, const std::string& from, const uint64_t& tv);

		/** Serializa la notificacion
		 * @param data[out]: buffer destino
		 * @param tipo
		 * @param datos
		 * @return Slice de Rocksdb que apunta al buffer data
		 */
		static rocksdb::Slice Pack(std::string& data, const Notification::NotificationType&, const std::string& n_data);

		/** Deserializa la notificacion
		 * @param data[in]: notificacion serializado
		 * @param n[out]: instacia donde se escribiran los datos
		 * @return true: si data esta bien, false: si no se pudo deserializar
		 */
		static bool UnPack(const std::string& data, Notification& m);

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
