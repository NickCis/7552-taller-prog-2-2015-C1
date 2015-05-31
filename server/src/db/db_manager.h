#ifndef __DB_HANDLE_H__
#define __DB_HANDLE_H__

#include <string>
#include <memory>
#include <vector>

#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/options.h>

#include "db_comparator.h"

/** Clase que crea e inicializa la base de datos y todas las entidades relacionadas con ella.
 */
class DBManager {
	public:
		/** Enum que representa los Column Families existentes
		 */
		typedef enum ColumnFamilies {
			COLUMN_FAMILY_DEFAULT=0, ///< Column Family Default
			COLUMN_FAMILY_MESSAGES, ///< Column Family Para los mensajes
			COLUMN_FAMILY_USERS, ///< Column Family Para los usuarios
			COLUMN_FAMILY_NOTIFICATIONS, ///< Column Family Para las notificaciones
			COLUMN_FAMILY_ACCESS_TOKENS, ///< Column Family Para los access token
			COLUMN_FAMILY_CONTACT_LIST, ///< Column Family Para la lista de contactos
			COLUMN_FAMILY_TOTAL ///< Numero total de column families
		} ColumnFamilies;

		/** Constructor.
		 * @param path[in]: Path donde se encuentran los archivos de la db
		 * @param s[out]: estado de error acerca la inicializacion de la db
		 */
		DBManager(const std::string& path, rocksdb::Status& s);

		/** Devuelve la instancia a la db.
		 * @param instancia de db
		 */
		rocksdb::DB* get();

		/** Devuelve un column family.
		 * @param c[in]: Tipo de column family que se desea obtener
		 * @return column family
		 */
		rocksdb::ColumnFamilyHandle* getColumnFamily(DBManager::ColumnFamilies c);

		/** Inicializa todas las entidades relacionadas con la db
		 */
		void setEnviroment();

	protected:
		typedef enum Comparator {
			COMPARATOR_DEFAULT=0,
			COMPARATOR_DB_COMPARATOR,
			COMPARATOR_DB_COMPARATOR_REVERSE,
		} Comparator;

		typedef enum MergeOperator {
			MERGE_DEFAULT=0,
			MERGE_DB_CONTACT_LIST,
			MERGE_DB_USER
		} MergeOperator;

		typedef struct {
			const char* name;
			const DBManager::Comparator comparator;
			const DBManager::MergeOperator mergeOperator;
		} ColumnFamilyDescriptor;

		void columnFamilyOptionsFromDescriptor(const DBManager::ColumnFamilyDescriptor& cfd, rocksdb::ColumnFamilyOptions &cfo);

		static const DBManager::ColumnFamilyDescriptor ColumnFamilyDescriptors[];

		std::string path; ///< Path a la db
		std::unique_ptr<rocksdb::DB> db; ///< Instancia de la db
		std::vector < std::unique_ptr < rocksdb::ColumnFamilyHandle > > cfs; ///< vector con todas las instancias de column families
		DbComparator comparator; //< Clase comparadora

		/** Crea la db.
		 * @param s[out]: estado de error de la creacion de la db
		 */
		void create(rocksdb::Status& s);

		/** Abre la db.
		 * @param s[out]: estado de error de la apertura de la db
		 */
		void open(rocksdb::Status& s);
};

#endif
