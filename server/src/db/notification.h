#ifndef __NOTIFICATION_H__
#define __NOTIFICATION_H__

#include <ctime>
#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/write_batch.h>
#include <rocksdb/iterator.h>

/** Clase que maneja las notificaciones en la db
 */
class Notification {
	public:
		/** Enum que representa el tipo de notificacion
		 */
		typedef enum {
			NOTIFICATION_MESSAGE=0, ///< Tipo mensaje
			NOTIFICATION_ACK ///< Tipo ACK
		} NotificationType;

		/** Clase para iterar por las notificaciones
		 */
		class NotificationIterator;

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

		/** Devuelve un nuevo iterador
		 */
		static std::shared_ptr<Notification::NotificationIterator> NewIterator();

		/** Metodo que sirve para setear la instancia a la base de datos y a la column family.
		 * @param db[in]: Instancia a la base de datos a usar
		 * @param cf[in]: instancia a la column family a usar
		 */
		static void SetDB(rocksdb::DB* db, rocksdb::ColumnFamilyHandle* cf);

		const uint64_t& getIdBin() const; ///< Id en formato binario
		std::string getId() const; ///< id en formato hexa
		const time_t& getTime() const; ///< timestamp de la notificacion
		std::string toJson() const; ///< Representacion en JSON

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

	protected:
		static rocksdb::DB* db; ///< Instancia db
		static rocksdb::ColumnFamilyHandle* cf; ///< Instancia column family

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

		std::string data; ///< data que guarda la notificacion
		Notification::NotificationType type; ///< tipo de notificacion
		time_t t; ///< timestamp de la notificacion
		uint64_t id; ///< id en formato binario
};

/** Clase para iterar por las notificaciones
*/
class Notification::NotificationIterator {
	friend class Notification;
	public:
		/** mueve el iterador para adelante
		 */
		void next();

		//rocksdb::Slice key() const;
		//rocksdb::Slice value() const;

		/** Va a la primera notifcacion. La verdad no tiene mucha utilidad, se puede usar para listar todos las notificaiciones
		 * de todos los usuarios. (mueve el iterador)
		 */
		void seekToFirst();
		const Notification& value() const; ///< Devuelve la notificacion correspondiste a la posicion actual
		rocksdb::Slice key() const; ///< Devuelve la Key de la notificacion
		rocksdb::Status status() const;  ///< Estado del iterador
		bool valid() const;  ///< Devuelve si el iterador esta en una posicion valida

		/** Operacion Delete. Delete es una palabra reservada =(, por eso tiene este nombre criptico.
		 */
		void dlt();

		/** Busca notificacion apartir de usuario. (mueve el iterador)
		 * @param from: usuario
		 */
		void seek(const std::string& from);

		rocksdb::Status write();

		~NotificationIterator();

	protected:
		std::shared_ptr<rocksdb::Iterator> it; ///< Instancia al iterador de rocksdb
		rocksdb::DB *db; ///< Instancia db
		rocksdb::ColumnFamilyHandle* cf; ///< Instancia column family
		rocksdb::WriteBatch batch; ///< Batch que guarda las operaciones a realizaar con el iterator.
		Notification notif; ///< notificacion actual
		void unPack(); ///< deserializador del mensaje
		std::string prefix; ///< Prefijo de las Keys buscadas

		/** Constructor
		 * @param iterador de rocksdb asociado a la db
		 */
		NotificationIterator(rocksdb::Iterator*, rocksdb::DB*, rocksdb::ColumnFamilyHandle*);
};

#endif
