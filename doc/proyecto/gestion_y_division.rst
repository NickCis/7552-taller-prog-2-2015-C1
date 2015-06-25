Gestion y División de tareas
============================

Para una facil gestion del proyecto se decidió utilizar herramientas que permitan en tiempo real llevar un relevamiento del proyecto. Estas herramientas deberían poder usarse sin la necesidad de instalar ninguna aplicación, es decir, herramientas online que se puedan usar atraves de un navegador.

Se eligió utilizar la plataforma online del servicio `Github <https://www.github.com>`_. Esta plataforma dispone de:

* Sistema de control de versiones: *git*.
* Sistema de tareas
* Sistema de wiki

Gestion
-------

Proceso de desarrollo
^^^^^^^^^^^^^^^^^^^^^

Cada desarrollador debe seguir un proceso que determina el trabajo día a día. Para el desarrollo de este proyecto se adopto el siguiente esquema de trabajo.

1. **Elección de una tarea a realizar del sistema de tareas:**
   El desarrollador debe elegir una tarea a realizar de las pactadas en el sistema de tareas. La elección de la misma esta dejada a libertad del desarrollador, aunque este deberá a tener en cuenta los *milestones*. Cada tarea esta marcada con *tags* que identifican a que corresponde, es decir, si es del *cliente*, *servidor*, *documentación*, etc.
   El desarrollador deberá asignarse esa tarea a sí mismo.

2. **Resolución de la tarea:**
   El desarrollador escribirá el código pertinente para resolver el problema que la tarea pacta. Se deberá realizar *commits* periodicos y los comentarios de los mismos deben tener el número de la tarea, es decir *#<número tarea>*.
   En el comentario del último *commit* se deberá anteponer la palabra clave *closes* que provoca que el sistema de tareas cierre dicha tarea. **Nota:** Recordar incluir los *tests* antes de cerrar la tarea.

3. **Desarrollo de tests:**
   El desarrollador deberá escribir los test pertinentes al código desarrollado.

4. **Actualizacion de wiki:**
   El desarrollador deberá actualizar la wiki online para que refleje los cambios realizados a la aplicación.

5. **Generación de nuevos tickets:**
   Un frase común en los entornos de desarrollo es *Por cada bug resuelto se esconden dos nuevos bugs por resolver*. Se deben generar nuevas tareas o *tickets* por todo lo faltante que creo la resolución de esta tarea.

Diseño del desarrollo
^^^^^^^^^^^^^^^^^^^^^

El desarrollo del proyecto se dividió en tres secciones:

1. Desarrollo del protocolo *REST*
2. Desarrollo del servidor
3. Desarrollo del cliente

Primero se estandarizó el protocolo REST. Este protocolo es el que determina la interacción entre el servidor y el cliente. Todos las tareas relacionadas con esto llevan el *tag* de *protocolo*. Nicolas Cisco fue quien estuvo a cargo de estas.

Después, se desarrollo en forma paralela el diseño del cliente y el servidor. El diseño del servidor estuvo a cargo de Nicolas Cisco, todas las tareas llevan el tag de *servidor*. Mientras que el diseño del cliente estuvo a cargo de Rodrigo Burdet, las tareas llevan el tag de *cliente*.

División de tareas
------------------

Se plantearon 5 *milestones*, estos pueden verse en el `sistema de tareas <https://github.com/NickCis/7552-taller-prog-2-2015-C1/milestones>`_. Ellos son:

1. Diseño
2. Funcionalidades v0.1
3. Funcionalidades v0.2
4. Funcionalidades v0.3
5. Future

Diseño
^^^^^^
Su objetivo fue establecer el diseño básico del protocolo, servidor y cliente.

Tareas:
 * `Crear una estructura de datos para soportar almacenamiento parcial <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/22>`_ Realizada por: MatSebMan [Mon Apr 20 2015 10:24:35 GMT-0300 (ART)]
 * `Definir logging <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/16>`_ Realizada por: NickCis [Mon Apr 27 2015 23:41:36 GMT-0300 (ART)]
 * `definir estructura preservación datos en cliente <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/12>`_ Realizada por: rburdet [Fri May 29 2015 05:01:13 GMT-0300 (ART)]
 * `definir interfaz usuario <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/11>`_ Realizada por: rburdet [Sat Apr 25 2015 21:14:51 GMT-0300 (ART)]
 * `Decidir framework para tests <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/5>`_ Realizada por: NickCis [Tue Apr 28 2015 04:49:53 GMT-0300 (ART)]
 * `Estructura basica del servidor <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/3>`_ Realizada por: NickCis [Sat Apr 11 2015 16:18:28 GMT-0300 (ART)]
 * `Estructura basica del cliente <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/2>`_ Realizada por: NickCis [Fri May 29 2015 05:01:13 GMT-0300 (ART)]
 * `Dise~nar protocolo REST <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/1>`_ Realizada por: NickCis [Tue Apr 07 2015 00:06:07 GMT-0300 (ART)]


Funcionalidades v0.1
^^^^^^^^^^^^^^^^^^^^
Su objetivo fue desarrollar funcionalidades básicas. Es decir, regristro y login de usuarios, envio y recepción de mensajes y manejo de notificaciones. Corresponde a lo que intentamos entregar para el primer checkpoint.


Tareas:
 * `Informe 1er CheckPoint <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/43>`_ Realizada por: martindonofrio [Fri May 08 2015 21:27:15 GMT-0300 (ART)]
 * `Notificaciones estan desordenadas <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/41>`_ Realizada por: NickCis [Sun Apr 26 2015 23:59:05 GMT-0300 (ART)]
 * `Dialogos informativos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/39>`_ Realizada por: rburdet [Sat Apr 25 2015 21:14:51 GMT-0300 (ART)]
 * `pantalla de registro <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/38>`_ Realizada por: rburdet [Sat Apr 25 2015 21:14:51 GMT-0300 (ART)]
 * `ip configurable  <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/37>`_ Realizada por: rburdet [Sat Apr 25 2015 21:14:51 GMT-0300 (ART)]
 * `Mensajes: Anda mal ordenado y filtrado <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/33>`_ Realizada por: NickCis [Fri Apr 24 2015 02:42:41 GMT-0300 (ART)]
 * `Desactivar conversaciones <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/32>`_ Realizada por: rburdet [Fri May 15 2015 04:50:18 GMT-0300 (ART)]
 * `Implementar notificaciones <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/26>`_ Realizada por: NickCis [Sun Apr 26 2015 18:19:54 GMT-0300 (ART)]
 * `Incorporar RocksDB a MgServer <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/18>`_ Realizada por: NickCis [Wed Apr 15 2015 02:42:06 GMT-0300 (ART)]
 * `Desarrollar endpoint /user/{username}/messages <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/15>`_ Realizada por: NickCis [Wed Apr 22 2015 02:28:51 GMT-0300 (ART)]
 * `Desarrollar endpoint auth <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/14>`_ Realizada por: NickCis [Wed Apr 22 2015 01:27:10 GMT-0300 (ART)]
 * `Desarrollar endpoint SignUp <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/13>`_ Realizada por: NickCis [Wed Apr 15 2015 02:42:58 GMT-0300 (ART)]
 * `Definir notificaciones pull <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/6>`_ Realizada por: NickCis [Fri Apr 17 2015 01:10:19 GMT-0300 (ART)]


Funcionalidades v0.2
^^^^^^^^^^^^^^^^^^^^
Su objetivo fue ya desarrollar una aplicación estable con todas las funcionalidades que se esperarian de ella. Es decir, perfiles de usuario, listas de contactos, checkin y mensajes de difusión. Corresponde a lo que se intentaría entregar para el segundo checkpoin.

Tareas:
 * `Borrar conversaciones  <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/67>`_ Realizada por: rburdet [Sat Jun 13 2015 19:27:44 GMT-0300 (ART)]
 * `Conversaciones completas <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/66>`_ Realizada por: rburdet [Tue Jun 02 2015 16:07:11 GMT-0300 (ART)]
 * `Perfil y Contact list, deben mostar correctamente informacion de conectado <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/64>`_ Realizada por: NickCis [Wed Jun 17 2015 04:21:14 GMT-0300 (ART)]
 * `AuthNode: actualice tiempo de last_activity <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/63>`_ Realizada por: NickCis [Sun May 31 2015 18:16:15 GMT-0300 (ART)]
 * `Implementar mensajes de difusion <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/62>`_ Realizada por: NickCis [Wed Jun 24 2015 02:14:09 GMT-0300 (ART)]
 * `Notificar cambios en perfiles y/o avatar <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/61>`_ Realizada por: NickCis [Thu Jun 04 2015 02:30:54 GMT-0300 (ART)]
 * `Configuracion de perfil <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/59>`_ Realizada por: rburdet [Tue Jun 23 2015 17:33:54 GMT-0300 (ART)]
 * `Visualizacion de datos de contacto <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/58>`_ Realizada por: rburdet [Mon Jun 01 2015 17:49:24 GMT-0300 (ART)]
 * `Checkin <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/57>`_ Realizada por: rburdet [Tue Jun 23 2015 03:18:54 GMT-0300 (ART)]
 * `Manejo de avatares <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/56>`_ Realizada por: rburdet [Sat Jun 13 2015 19:27:44 GMT-0300 (ART)]
 * `Manejo de usuarios en el cliente <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/55>`_ Realizada por: rburdet [Thu Jun 04 2015 05:56:43 GMT-0300 (ART)]
 * `Endpoint /contact <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/52>`_ Realizada por: NickCis [Sat May 30 2015 16:10:28 GMT-0300 (ART)]
 * `Servidor: error registrando y logeando con usuarios que tienen mayusculas <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/50>`_ Realizada por: NickCis [Thu Jun 25 2015 06:13:58 GMT-0300 (ART)]
 * `Metricas: Code coverage <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/49>`_ Realizada por: NickCis [Sat May 16 2015 07:39:19 GMT-0300 (ART)]
 * `Instalador <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/47>`_ Realizada por: NickCis [Thu Jun 25 2015 06:30:16 GMT-0300 (ART)]
 * `Realizar Tests: Llegar a un 75% de code coverage <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/46>`_ Realizada por: NickCis [Wed Jun 24 2015 00:35:48 GMT-0300 (ART)]
 * `Mensajes de difusion <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/36>`_ Realizada por: NickCis [Wed Jun 24 2015 00:34:37 GMT-0300 (ART)]
 * `CheckIn usuario <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/31>`_ Realizada por: rburdet [Sat Apr 25 2015 21:14:51 GMT-0300 (ART)]
 * `Implementar checkins <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/30>`_ Realizada por: NickCis [Sat May 30 2015 15:52:30 GMT-0300 (ART)]
 * `Enpoint usuario/avatar <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/29>`_ Realizada por: NickCis [Fri May 29 2015 02:53:02 GMT-0300 (ART)]
 * `Endpoint: usuario/Perfil <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/28>`_ Realizada por: NickCis [Sat May 30 2015 02:09:24 GMT-0300 (ART)]
 * `Archivo de configuracion y parametros de cmd <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/17>`_ Realizada por: NickCis [Sat Jun 06 2015 15:22:14 GMT-0300 (ART)]
 * `Definir endpoint para contactos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/9>`_ Realizada por: NickCis [Sat May 16 2015 00:44:28 GMT-0300 (ART)]

Funcionalidades v0.3
^^^^^^^^^^^^^^^^^^^^
Su objetivo fue pullir las cosas no terminadas de las anteriores entregas. Ya es la entrega final, se tiene desarrollado todo lo que comprende el enunciado.

Tareas:
 * `Mostrar bien el perfil de usuario <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/113>`_ Realizada por: rburdet
 * `Manual de proyecto  (gestión y división de tareas) <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/112>`_ Realizada por: NickCis
 * `Mensajes de difusion: cuando mando, no me aparece en las conversaciones <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/111>`_ Realizada por: NickCis
 * `Cliente: sincronizar lista de contactos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/110>`_ Realizada por: NickCis
 * `Login: si borras http de la ip no se puede volver a escribir. <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/109>`_ Realizada por: NickCis
 * `Al registrarse no esta sincronizando informacion de perfil por defecto <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/107>`_ Realizada por: NickCis [Thu Jun 25 2015 15:07:24 GMT-0300 (ART)]
 * `Javadoc cliente <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/106>`_ Realizada por: rburdet [Thu Jun 25 2015 14:22:52 GMT-0300 (ART)]
 * `Generar Changelog <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/105>`_ Realizada por: NickCis
 * `Client: Login al ingresar mal los datos crea un nuevo Activity <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/104>`_ Realizada por: NickCis [Thu Jun 25 2015 13:13:49 GMT-0300 (ART)]
 * `ProfileConfiguration: manda al servidor la informacion de perfil vieja <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/103>`_ Realizada por: NickCis [Thu Jun 25 2015 05:36:49 GMT-0300 (ART)]
 * `Cliente: al logearse deberia traer la informacion del perfil <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/101>`_ Realizada por: NickCis [Thu Jun 25 2015 05:36:49 GMT-0300 (ART)]
 * `Servidor: leakea al usar iteradores de la db <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/100>`_ Realizada por: NickCis [Thu Jun 25 2015 02:45:49 GMT-0300 (ART)]
 * `Cliente: login: distinguir servidor caido (no hay connecion) de error de datos. <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/99>`_ Realizada por: NickCis
 * `Cliente: register si usuario ya existe que mensaje de error diga eso <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/98>`_ Realizada por: NickCis
 * `Atras no deberia deslogear al cliente, deberia minimizar la app <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/97>`_ Realizada por: NickCis
 * `Pedir ultima conexion <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/96>`_ Realizada por: NickCis
 * `Borrar conversaciones no borra los mensajes <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/95>`_ Realizada por: rburdet [Wed Jun 24 2015 17:06:49 GMT-0300 (ART)]
 * `Se debe usar username como nickname por defecto <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/94>`_ Realizada por: NickCis [Wed Jun 24 2015 16:34:11 GMT-0300 (ART)]
 * `Configuracion de perfil: inicialmente traer datos del servidor <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/93>`_ Realizada por: NickCis [Thu Jun 25 2015 15:29:49 GMT-0300 (ART)]
 * `Login: Ultimo siguiente deberia ser conectar <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/92>`_ Realizada por: NickCis [Thu Jun 25 2015 01:58:51 GMT-0300 (ART)]
 * `Configuracion de perfil: los cambios se guardan en db sin que sean enviados al servidor <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/91>`_ Realizada por: NickCis [Wed Jun 24 2015 17:33:45 GMT-0300 (ART)]
 * `problema mandando grandes archivos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/90>`_ Realizada por: rburdet
 * `broadcast de mensajes <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/88>`_ Realizada por: rburdet [Wed Jun 24 2015 04:10:07 GMT-0300 (ART)]
 * `Profile: Status sacar opcion do not disturb <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/87>`_ Realizada por: NickCis [Wed Jun 24 2015 16:45:46 GMT-0300 (ART)]
 * `mandar avatar al servidor <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/86>`_ Realizada por: rburdet [Thu Jun 25 2015 02:45:49 GMT-0300 (ART)]
 * `agregar username a notification de checkin <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/85>`_ Realizada por: rburdet [Wed Jun 24 2015 02:26:00 GMT-0300 (ART)]
 * `mostrar ultima conexion <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/84>`_ Realizada por: rburdet [Wed Jun 24 2015 16:33:36 GMT-0300 (ART)]
 * `Checkin en base de datos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/83>`_ Realizada por: rburdet [Wed Jun 24 2015 18:31:51 GMT-0300 (ART)]
 * `Desconectado no se muestra <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/82>`_ Realizada por: rburdet
 * `notificaciones cambio perfil <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/81>`_ Realizada por: rburdet [Sat Jun 20 2015 03:09:06 GMT-0300 (ART)]

Future
^^^^^^
La idea de este *milestone* es dejar anotado todo que escapaba al enunciado, se quiso desarrollar y no se hizo por una cuestion de tiempo. Es el *milestone* que representa a futuro cual sería el camino a seguir.

Tareas:
 * `Client: poner un tag para los logs <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/102>`_ Realizada por: NickCis
 * `Acks de mensajes <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/78>`_ Realizada por: rburdet
 * `Notificaciones Push <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/73>`_ Realizada por: NickCis
 * `Investigar posibilidad de gzip o alguna otra compresion <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/71>`_ Realizada por: NickCis
 * `Posibilidad de utilizar SSL <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/70>`_ Realizada por: NickCis
 * `Reemplazar MD5 por SHA256 <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/65>`_ Realizada por: NickCis
 * `Tests ! <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/60>`_ Realizada por: rburdet
 * `Log: poner algo de informacion extra <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/44>`_ Realizada por: NickCis
 * `Mensajes: implementar adjuntos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/25>`_ Realizada por: NickCis
 * `Listar y eliminar access_token s <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/24>`_ Realizada por: NickCis
 * `Colectar access_tokens <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/21>`_ Realizada por: NickCis
 * `MgServer reemplazar pthrads por threads nativos de c++ <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/19>`_ Realizada por: NickCis
 * `Establecer limites para subida de archivos <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/8>`_ Realizada por: NickCis
 * `Definir conversaciones grupales <https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues/7>`_ Realizada por: NickCis

